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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.storage.client.Storage;
import com.google.gwt.storage.client.StorageEvent;
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
 * @see Storage
 */
@SuppressWarnings("serial")
public class LocalStorage extends AbstractExtension {
	
	/**
	 * Java logger
	 */
	private final static Logger LOGGER = Logger.getLogger(LocalStorage.class.getName()); 
	
	/**
	 * Map of callbacks
	 */
	private LocalStorageItemCallbacks itemCallbacks = new LocalStorageItemCallbacks();
	
	/**
	 * {@link ServerRpc} that contains methods who get invoked by the client side.
	 */
	private LocalStorageServerRpc serverRpc = new LocalStorageServerRpc() {

		/*
		 * (non-Javadoc)
		 * @see eu.maxschuster.vaadin.localstorage.shared.LocalStorageServerRpc#callLocalStorageItemCallback(eu.maxschuster.vaadin.localstorage.shared.LocalStorageItemCallback, eu.maxschuster.vaadin.localstorage.shared.LocalStorageItem)
		 */
		@Override
		public void callLocalStorageItemCallback(int callback, boolean success, String key, String oldData, String data) {
			synchronized (itemCallbacks) {
				LocalStorageItemCallback callbackImpl = itemCallbacks.get(callback);
				if(callbackImpl != null) {
					if (success) {
						callbackImpl.onSuccess(new LocalStorageItem(key, oldData, data));
					} else {
						callbackImpl.onError(key);
					}
					itemCallbacks.remove(callback);
				}
			}
		}

		@Override
		public void triggerItemUpdateEvent(String key, String oldData, String data) {
			fireItemUpdateEvent(new LocalStorageItem(key, oldData, data));
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
	public UI getParent() {
		return (UI) super.getParent();
	}
	
	/**
	 * Gets the items data from the {@link Storage} on the client-side.
	 * @param key Items key
	 * @param callback A callback
	 * @return void
	 */
	public void getItem(String key, LocalStorageItemCallback callback) {
		if (callback == null)
			throw new NullPointerException("Get an item from LocalStorage doesn't make much sense when callback is null... ;-)");
		getRpcProxy(LocalStorageClientRpc.class).getItem(key, itemCallbacks.add(callback));
	}
	
	/**
	 * Sets the items data in the {@link Storage} on the
	 * client-side and calls the given callback
	 * @param key Items key
	 * @param data Items new data.
	 * If null the item will get removed
	 * @param callback A callback
	 * @return void
	 */
	public void setItem(String key, String data, LocalStorageItemCallback callback) {
		getRpcProxy(LocalStorageClientRpc.class).setItem(key, data, itemCallbacks.add(callback));
	}
	
	/**
	 * Sets the items data in the {@link Storage} on the client-side
	 * @param key Items key
	 * @param data Items new data.
	 * If null the item will get removed
	 * @return void
	 */
	public void setItem(String key, String data) {
		setItem(key, data, null);
	}
	
	/**
	 * Removes the item from the {@link Storage} on the client-side 
	 * and calls the given callback.
	 * @param key Items key
	 * @param callback A callback
	 * @return void
	 */
	public void removeItem(String key, LocalStorageItemCallback callback) {
		setItem(key, null, callback);
	}
	
	/**
	 * Removes the item from the {@link Storage} on the client-side
	 * @param key Items key
	 * @return void
	 */
	public void removeItem(String key) {
		removeItem(key, null);
	}
	
	/**
	 * Clears the {@link Storage} on the client-side and
	 * calls the given callback
	 * @param callback A callback
	 * @return void
	 */
	public void clear(LocalStorageItemCallback callback) {
		getRpcProxy(LocalStorageClientRpc.class).clear(itemCallbacks.add(callback));
	}
	
	/**
	 * Clears the {@link Storage} on the client-side
	 * @return void
	 */
	public void clear() {
		clear(null);
	}
	
	/**
	 * @return SimulateNotSupported is enabled.
	 * <p><u>When SimulateNotSupported is enabled the client-side acts
	 * as if localStorage is not supported!</u></p>
	 */
	public boolean isSimulateNotSupported() {
		return getState().simulateNotSupported;
	}
	
	/**
	 * @param simulateNotSupported SimulateNotSupported is enabled.
	 * <p><u>When SimulateNotSupported is enabled the client-side acts
	 * as if localStorage is not supported!</u></p>
	 */
	public void setSimulateNotSupported(boolean simulateNotSupported) {
		if (getState().simulateNotSupported != simulateNotSupported) {
			if (simulateNotSupported) {
				LOGGER.warning("SimulateNotSupported mode activated!");	
			} else {
				LOGGER.info("SimulateNotSupported mode deactivated!");	
			}
			getState().simulateNotSupported = simulateNotSupported;
		}
	}
	
	/**
	 * Fires an item update event
	 * @param item Updated {@link LocalStorageItem}
	 */
	private void fireItemUpdateEvent(LocalStorageItem item) {
		fireEvent(new ItemUpdateEvent(getParent(), this, item));
	}
	
	/**
	 * Adds a listener for the {@link ItemUpdateEvent}
	 * @param listener Listener for the {@link ItemUpdateEvent}
	 * @see ItemUpdateEvent
	 */
	public void addItemUpdateListener(ItemUpdateListener listener) {
		addListener(ItemUpdateEvent.ITEM_UPDATE_EVENT_IDENTIFIER, ItemUpdateEvent.class,
				listener, ItemUpdateListener.onUpdateMethod);
	}
	
	/**
	 * Removes a listener for the {@link ItemUpdateEvent}
	 * @param listener Listener for the {@link ItemUpdateEvent}
	 */
	public void removeItemUpdateListener(ItemUpdateListener listener) {
		removeListener(ItemUpdateEvent.ITEM_UPDATE_EVENT_IDENTIFIER, ItemUpdateEvent.class,
				listener);
	}
	
	/**
	 * Contains all callbacks of a {@link LocalStorage} instance
	 * @author Max Schuster <dev@maxschutser.eu>
	 */
	private class LocalStorageItemCallbacks extends HashMap<Integer, LocalStorageItemCallback> {
		
		/**
		 * Current key counter
		 */
		private int currentKey = 0;
		
		/**
		 * Adds a callback and returns a callback id or
		 * -1 if callback is null and has not been added
		 * @param callback A callback
		 * @return Callback id
		 */
		public int add(LocalStorageItemCallback callback) {
			if (callback == null)
				return -1;
			int key = currentKey++;
			super.put(key, callback);
			return key;
		}

		/* (non-Javadoc)
		 * @see java.util.HashMap#put(java.lang.Object, java.lang.Object)
		 */
		@Override
		public LocalStorageItemCallback put(Integer key,
				LocalStorageItemCallback value) throws UnsupportedOperationException {
			throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see java.util.HashMap#putAll(java.util.Map)
		 */
		@Override
		public void putAll(
				Map<? extends Integer, ? extends LocalStorageItemCallback> m)
					throws UnsupportedOperationException {
			throw new UnsupportedOperationException();
		}
		
	}
	
	/**
	 * Listener for Local {@link ItemUpdateEvent}
	 * @author Max Schuster <dev@maxschutser.eu>
	 * @see ItemUpdateEvent
	 */
	public static interface ItemUpdateListener {
		
		public final static Method onUpdateMethod =
				ReflectTools.findMethod(ItemUpdateListener.class, "onUpdate", ItemUpdateEvent.class);
		
		/**
		 * @param event {@link ItemUpdateEvent}
		 */
		public void onUpdate(ItemUpdateEvent event);
		
	}
	
	/**
	 * Basic {@link LocalStorage} event
	 * @author Max Schuster <dev@maxschutser.eu>
	 */
	public abstract static class LocalStorageEvent extends Component.Event {

		/**
		 * The source {@link LocalStorage} instance
		 */
		private final LocalStorage localStorage;
		
		/**
		 * @param source Source {@link UI} instance.
		 * @param localStorage Source {@link LocalStorage} instance.
		 */
		public LocalStorageEvent(UI source, LocalStorage localStorage) {
			super(source);
			this.localStorage = localStorage;
		}

		/**
		 * @return The source {@link LocalStorage} instance
		 */
		public final LocalStorage getLocalStorage() {
			return localStorage;
		}

		/* (non-Javadoc)
		 * @see com.vaadin.ui.Component.Event#getComponent()
		 */
		@Override
		public UI getComponent() {
			return (UI) super.getComponent();
		}
		
	}
	
	/**
	 * Fires when an item of the localStorage on the client-side has changed.
	 * @author Max Schuster <dev@maxschutser.eu>
	 * @see StorageEvent
	 */
	public static class ItemUpdateEvent extends LocalStorageEvent {
		
		public static final String ITEM_UPDATE_EVENT_IDENTIFIER = "localStorageItemUpdate";
		
		/**
		 * Item update type
		 * @author Max Schuster <dev@maxschutser.eu>
		 */
		public enum Type {
			CLEAR,
			REMOVE,
			UPDATE
		}

		/**
		 * The updated item
		 */
		private final LocalStorageItem item;
		
		/**
		 * The update type
		 */
		private final Type type;
		
		/**
		 * @param source Source {@link UI} instance.
		 * @param localStorage Source {@link LocalStorage} instance.
		 * @param item Updated {@link LocalStorageItem}
		 */
		public ItemUpdateEvent(UI source, LocalStorage localStorage, LocalStorageItem item) {
			super(source, localStorage);
			this.item = item;
			if (item != null && item.getKey() == null) {
				type = Type.CLEAR;
			} else if (item != null && item.getData() == null) {
				type = Type.REMOVE;
			} else {
				type = Type.UPDATE;
			}
		}

		/**
		 * @return The updated {@link LocalStorageItem}
		 */
		public LocalStorageItem getItem() {
			return item;
		}

		/**
		 * @return The update type
		 */
		public Type getType() {
			return type;
		}
		
	}

}
