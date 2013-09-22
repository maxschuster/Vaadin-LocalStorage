/*
 * eu.maxschuster.vaadin.localstorage.LocalStorage.java
 * 
 * Copyright 2013 Max Schuster
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.maxschuster.vaadin.localstorage;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import com.vaadin.server.AbstractClientConnector;
import com.vaadin.server.AbstractExtension;
import com.vaadin.server.Extension;
import com.vaadin.shared.communication.ServerRpc;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import com.vaadin.util.ReflectTools;

import eu.maxschuster.vaadin.localstorage.client.LocalStorageClientRpc;
import eu.maxschuster.vaadin.localstorage.shared.LocalStorageServerRpc;
import eu.maxschuster.vaadin.localstorage.shared.LocalStorageState;

/**
 * Allows limited access to the browsers localStorage.
 * 
 * You have to use {@link LocalStorage#getCurrent()} or {@link LocalStorage#getCurrent(UI)} to get an instance of {@link LocalStorage}
 * 
 * @author Max Schuster <dev@maxschutser.eu>
 */
@SuppressWarnings("serial")
public class LocalStorage extends AbstractExtension {

	/**
	 * Local mirror of the client side localStorage.
	 */
	private Map<String, String> items = null;
	
	/**
	 * Queue of jobs that should run when {@link LocalStorage} becomes ready
	 */
	private Queue<Runnable> doWhenReadyQueue = new LinkedList<Runnable>();
	
	/**
	 * {@link ServerRpc} that contains methods who get invoked by the client side.
	 */
	private LocalStorageServerRpc serverRpc = new LocalStorageServerRpc() {

		/*
		 * (non-Javadoc)
		 * @see eu.maxschuster.vaadin.localstorage.shared.LocalStorageServerRpc#ready(java.util.Map)
		 */
		@Override
		public void ready(Map<String, String> items) {
			if (!getState().ready) {
				getState().ready = true;
				getState().supported = true;
				LocalStorage.this.items = items;
				runDoWhenReadyQueue();
				fireReadyEvent();
			}
		}

		/*
		 * (non-Javadoc)
		 * @see eu.maxschuster.vaadin.localstorage.shared.LocalStorageServerRpc#readyNotSupported(boolean)
		 */
		@Override
		public void readyNotSupported() {
			if (!getState().ready) {
				getState().ready = true;
				getState().supported = false;
				runDoWhenReadyQueue();
				fireReadyEvent();
			}
		}

		/*
		 * (non-Javadoc)
		 * @see eu.maxschuster.vaadin.localstorage.shared.LocalStorageServerRpc#refresh(java.util.Map)
		 */
		@Override
		public void refresh(Map<String, String> storage) {
			LocalStorage.this.items = storage;
			fireRefreshEvent();
		}

		/*
		 * (non-Javadoc)
		 * @see eu.maxschuster.vaadin.localstorage.shared.LocalStorageServerRpc#updateItem(java.lang.String, java.lang.String, java.lang.String)
		 */
		@Override
		public void updateItem(String key, String oldValue, String newValue) {
			synchronized (items) {
				if (key == null) {
					items.clear();
				}
				else if (newValue == null) {
					items.remove(key);
				} else {
					items.put(key, newValue);
				}
			}
			fireUpdateEvent(key, oldValue, newValue);
		}
		
	};

	/**
	 * Main constructor.
	 * Extends the given {@link AbstractClientConnector}.
	 * @param clientConnector {@link AbstractClientConnector} that should get extended.
	 */
	private LocalStorage(AbstractComponent componentToExtend) {
		registerRpc(serverRpc, LocalStorageServerRpc.class);
		extend(componentToExtend);
	}
	
	/**
	 * Gets or creates the {@link LocalStorage} instance of the currently active {@link UI}.
	 * @return {@link LocalStorage} instance of the currently active {@link UI}.
	 */
	public static LocalStorage getCurrent() {
		return getCurrent(UI.getCurrent());
	}
	
	/**
	 * Gets or creates the {@link LocalStorage} instance of the given parent {@link UI}.
	 * @param parent Parent {@link UI}
	 * @return {@link LocalStorage} instance of the given parent {@link UI}.
	 */
	public static LocalStorage getCurrent(UI parent) {
		if (parent == null) {
			throw new NullPointerException();
		}
		
		for (Extension extension : parent.getExtensions()) {
			if (extension instanceof LocalStorage) {
				return (LocalStorage) extension;
			}
		}
		
		return new LocalStorage(parent);
	}
	
	/**
	 * Returns an unmodifiable {@link Map} that contains all items.
	 * Returns <code>null</code> if the {@link LocalStorage} is not ready, yet.
	 * @return Unmodifiable map containing all items
	 */
	public Map<String, String> getItems() {
		return items == null ? null : Collections.unmodifiableMap(items);
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.server.AbstractClientConnector#getState()
	 */
	@Override
	protected LocalStorageState getState() {
		return (LocalStorageState) super.getState();
	}
	
	/* (non-Javadoc)
	 * @see com.vaadin.server.AbstractExtension#getParent()
	 */
	@Override
	public AbstractComponent getParent() {
		return (AbstractComponent) super.getParent();
	}

	/**
	 * Clears the storage.
	 * Clears the local localStorage mirror immediately if it is available! 
	 * Will have no effect if localStorage is not supported!
	 */
	public void clear() {
		getRpcProxy(LocalStorageClientRpc.class).clear();
		if (items != null) {
			synchronized (items) {
				items.clear();
			}
		}
	}
	
	/**
	 * Forces a complete refresh of the local localStorage mirror.
	 * Does not immediately come into effect!
	 * Will have no effect if localStorage is not supported!
	 */
	public void refresh() {
		getRpcProxy(LocalStorageClientRpc.class).refresh();
	}
	
	/**
	 * Adds a {@link Runnable} job that gets invoked when {@link LocalStorage}
	 * has become ready or immediately when {@link LocalStorage} already is ready.
	 * If the job has been added to the queue the run later it will get invoked
	 * before the {@link ReadyEvent} fires.
	 * @param job Job that should be done when {@link LocalStorage} is ready.
	 */
	public void doWhenReady(Runnable job) {
		if (job == null)
			throw new NullPointerException();
		
		if (isReady()) {
			job.run();
		} else {
			doWhenReadyQueue.offer(job);
		}
	}
	
	/**
	 * Runs all queued jobs.
	 */
	private void runDoWhenReadyQueue() {
		Runnable job = null;
		while ((job = doWhenReadyQueue.poll()) != null) {
			job.run();
		}
	}
	
	/**
	 * @return live update is enabled.
	 */
	public boolean isLiveUpdate() {
		return getState().liveUpdate;
	}
	
	/**
	 * @param liveUpdate enable/disable live update.
	 */
	public void setLiveUpdate(boolean liveUpdate) {
		getState().liveUpdate = liveUpdate;
	} 

	/**
	 * @return Browser supports localStorage. <code>null</code> if not checked, yet.
	 */
	public Boolean getSupported() {
		return getState().supported;
	}
	
	/**
	 * @return ready for usage.
	 */
	public boolean isReady() {
		return getState().ready;
	}

	/**
	 * Returns the data for the given item identifier.
	 * @param key Identifier of the item.
	 * @return Data of the item.
	 */
	public String getItem(String key) {
		return items == null ? null : items.get(key);
	}
	
	/**
	 * Sets the date for the given item identifier.
	 * Does not immediately come into effect!
	 * Will have no effect if localStorage is not supported!
	 * @param key Identifier of the item.
	 * @param data New data of the item.
	 */
	public void setItem(String key, String data) {
		getRpcProxy(LocalStorageClientRpc.class).setItem(key, data);
	}
	
	/**
	 * Removes the given item identifier.
	 * Will have no effect if localStorage is not supported!
	 * @param key Item identifier to remove.
	 */
	public void removeItem(String key) {
		getRpcProxy(LocalStorageClientRpc.class).removeItem(key);
	}
	
	/**
	 * Fires a {@link ReadyEvent}.
	 */
	private void fireReadyEvent() {
		fireEvent(new ReadyEvent(getParent(), this));
	}
	
	/**
	 * Adds a {@link ReadyListener}.
	 * @param listener {@link ReadyListener} to add.
	 */
	public void addReadyListener(ReadyListener listener) {
		addListener(ReadyEvent.EVENT_ID, ReadyEvent.class, listener, ReadyListener.onReadyMethod);
	}
	
	/**
	 * Removes a {@link ReadyListener}.
	 * @param listener {@link ReadyListener} to remove.
	 */
	public void removeReadyListener(ReadyListener listener) {
		removeListener(ReadyEvent.EVENT_ID, ReadyEvent.class, listener);
	}
	
	/**
	 * Fires a {@link RefreshEvent}.
	 */
	private void fireRefreshEvent() {
		fireEvent(new RefreshEvent(getParent(), this));
	}
	
	/**
	 * Adds a {@link RefreshListener}.
	 * @param listener {@link RefreshListener} to add.
	 */
	public void addRefreshListener(RefreshListener listener) {
		addListener(RefreshEvent.EVENT_ID, RefreshEvent.class, listener, RefreshListener.onRefreshMethod);
	}
	
	/**
	 * Removes a {@link RefreshListener}.
	 * @param listener {@link RefreshListener} to remove.
	 */
	public void removeRefreshListener(RefreshListener listener) {
		removeListener(RefreshEvent.EVENT_ID, RefreshEvent.class, listener);
	}
	
	/**
	 * Fires an {@link UpdateEvent}.
	 * @param key Identifier of the changed item.
	 * @param oldValue Old Value.
	 * @param newValue New Value.
	 */
	private void fireUpdateEvent(String key, String oldValue, String newValue) {
		fireEvent(new UpdateEvent(getParent(), this, key, oldValue, newValue));
	}
	
	/**
	 * Adds an {@link UpdateListener}.
	 * @param listener {@link UpdateListener} to add.
	 */
	public void addUpdateListener(UpdateListener listener) {
		addListener(UpdateEvent.EVENT_ID, UpdateEvent.class, listener, UpdateListener.onUpdateMethod);
	}
	
	/**
	 * Removes an {@link UpdateListener}.
	 * @param listener {@link UpdateListener} to remove.
	 */
	public void removeUpdateListener(UpdateListener listener) {
		removeListener(UpdateEvent.EVENT_ID, UpdateEvent.class, listener);
	}
	
	/**
	 * Basic event of {@link LocalStorage} events.
	 * @author Max Schuster <dev@maxschutser.eu>
	 */
	abstract public static class LocalStorageEvent extends Component.Event {

		/**
		 * The {@link LocalStorage} instance that has caused the event.
		 */
		private LocalStorage localStorage;
		
		/**
		 * @param source The source component of the event.
		 * @param localStorage The {@link LocalStorage} instance that has caused the event.
		 */
		public LocalStorageEvent(AbstractComponent source, LocalStorage localStorage) {
			super(source);
			this.localStorage = localStorage;
		}

		/**
		 * @return The {@link LocalStorage} instance that has caused the event.
		 */
		public LocalStorage getLocalStorage() {
			return localStorage;
		}

		/* (non-Javadoc)
		 * @see com.vaadin.ui.Component.Event#getComponent()
		 */
		@Override
		public AbstractComponent getComponent() {
			return (AbstractComponent) super.getComponent();
		}
		
	}
	
	/**
	 * Listener interface for the {@link ReadyEvent}.
	 * @author Max Schuster <dev@maxschutser.eu>
	 */
	public interface ReadyListener {
		
		/**
		 * Callback method.
		 */
		public static final Method onReadyMethod =
			ReflectTools.findMethod(ReadyListener.class, "onReady", ReadyEvent.class);
		
		/**
		 * Gets called when {@link LocalStorage} is ready for use (even when its not supported).
		 * @param event {@link ReadyEvent}.
		 */
		public void onReady(ReadyEvent event);
		
	}
	
	/**
	 * Fires when {@link LocalStorage} is ready for use (even when its not supported).
	 * @author Max Schuster <dev@maxschutser.eu>
	 */
	public static class ReadyEvent extends LocalStorageEvent {
		
		/**
		 * Static event identifier.
		 */
		public static final String EVENT_ID = "localStorage.Ready";

		/**
		 * @param source The source component of the event.
		 * @param localStorage The {@link LocalStorage} instance that has caused the event.
		 */
		public ReadyEvent(AbstractComponent source, LocalStorage localStorage) {
			super(source, localStorage);
		}
	}
	
	/**
	 * Listener interface for the {@link RefreshEvent}.
	 * @author Max Schuster <dev@maxschutser.eu>
	 */
	public interface RefreshListener {
		
		/**
		 * Callback method.
		 */
		public static final Method onRefreshMethod =
			ReflectTools.findMethod(RefreshListener.class, "onRefresh", RefreshEvent.class);
		
		/**
		 * Gets called when {@link LocalStorage} has completely refreshed the local mirror of localStorage.
		 * @param event {@link RefreshEvent}.
		 */
		public void onRefresh(RefreshEvent event);
		
	}
	
	/**
	 * Fires when {@link LocalStorage} has completely refreshed the local mirror of localStorage.
	 * @author Max Schuster <dev@maxschutser.eu>
	 */
	public static class RefreshEvent extends LocalStorageEvent {
		
		public static final String EVENT_ID = "localStorage.Refresh";

		/**
		 * @param source The source component of the event.
		 * @param localStorage The {@link LocalStorage} instance that has caused the event.
		 */
		public RefreshEvent(AbstractComponent source, LocalStorage localStorage) {
			super(source, localStorage);
		}
	}
	
	/**
	 * Listener interface for the {@link UpdateEvent}.
	 * @author Max Schuster <dev@maxschutser.eu>
	 */
	public interface UpdateListener {
		
		/**
		 * Callback method.
		 */
		public static final Method onUpdateMethod =
			ReflectTools.findMethod(UpdateListener.class, "onUpdate", UpdateEvent.class);
		
		/**
		 * Gets called when localStorage on the clientSide has been updated and {@link LocalStorage#isLiveUpdate()} is true.
		 * @param event {@link UpdateEvent}.
		 */
		public void onUpdate(UpdateEvent event);
		
	}
	
	/**
	 * Fires when localStorage on the clientSide has been updated and {@link LocalStorage#isLiveUpdate()} is true.
	 * @author Max Schuster <dev@maxschutser.eu>
	 */
	public static class UpdateEvent extends LocalStorageEvent {
		
		public static final String EVENT_ID = "localStorage.Update";
		
		/**
		 * Item identifier.
		 */
		private String key;
		
		/**
		 * Old value.
		 */
		private String oldValue;
		
		/**
		 * New value.
		 */
		private String newValue;

		/**
		 * @param source The source component of the event.
		 * @param localStorage The {@link LocalStorage} instance that has caused the event.
		 * @param key Identifier of the item that has changed.
		 * @param oldValue Old value
		 * @param newValue New value
		 */
		public UpdateEvent(
				AbstractComponent source,
				LocalStorage localStorage,
				String key,
				String oldValue,
				String newValue
		) {
			super(source, localStorage);
			this.key = key;
			this.oldValue = oldValue;
			this.newValue = newValue;
		}

		/**
		 * @return The item identifier.
		 */
		public String getKey() {
			return key;
		}

		/**
		 * @return The old value.
		 */
		public String getOldValue() {
			return oldValue;
		}

		/**
		 * @return The new value.
		 */
		public String getNewValue() {
			return newValue;
		}
		
		/**
		 * Returns true if the {@link LocalStorage} has been cleared.
		 * @return {@link LocalStorage} cleared.
		 */
		public boolean isClear() {
			return key == null;
		}
		
		/**
		 * Returns true if the current key has been removed from the {@link LocalStorage},
		 * @return Key removed.
		 */
		public boolean isRemove() {
			return key != null && newValue == null;
		}
	}
}
