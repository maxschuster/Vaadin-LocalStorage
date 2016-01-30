/*
 * eu.maxschuster.vaadin.localstorage.client.LocalStorageConnector.java
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

package eu.maxschuster.vaadin.localstorage.client;

import java.util.Set;

import com.google.gwt.storage.client.Storage;
import com.google.gwt.storage.client.StorageEvent;
import com.google.gwt.storage.client.StorageEvent.Handler;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.shared.communication.ClientRpc;
import com.vaadin.shared.communication.ServerRpc;
import com.vaadin.shared.ui.Connect;

import eu.maxschuster.vaadin.localstorage.LocalStorage;
import eu.maxschuster.vaadin.localstorage.LocalStorage.ItemUpdateEvent;
import eu.maxschuster.vaadin.localstorage.shared.LocalStorageServerRpc;
import eu.maxschuster.vaadin.localstorage.shared.LocalStorageState;

/**
 * Client side part of {@link LocalStorage}.
 * @author Max Schuster
 */
@SuppressWarnings("serial")
@Connect(LocalStorage.class)
public class LocalStorageConnector extends AbstractExtensionConnector {
	
	/**
	 * {@link ClientRpc} that contains methods who get invoked by the server side.
	 */
	private LocalStorageClientRpc clientRpc = new LocalStorageClientRpc() {

		/*
		 * (non-Javadoc)
		 * @see eu.maxschuster.vaadin.localstorage.client.LocalStorageClientRpc#getItem(java.lang.String, eu.maxschuster.vaadin.localstorage.shared.LocalStorageItemCallback)
		 */
		@Override
		public void getItem(String key, int callback) {
			boolean supported = isSupported();
			String data = null;
			
			if (supported) {
				Storage s = Storage.getLocalStorageIfSupported();
				data = s.getItem(key);
			}
			
			if (callback > -1) {
				serverRpc.callLocalStorageItemCallback(callback, supported, key, null, data);
			}
		}

		/*
		 * (non-Javadoc)
		 * @see eu.maxschuster.vaadin.localstorage.client.LocalStorageClientRpc#setItem(java.lang.String, java.lang.String, eu.maxschuster.vaadin.localstorage.shared.LocalStorageItemCallback)
		 */
		@Override
		public void setItem(String key, String data,
				int callback) {
			boolean supported = isSupported();
			String oldData = null;
			
			if (supported) {
				Storage s = Storage.getLocalStorageIfSupported();
				oldData = s.getItem(key);
				if (data != null) {
					s.setItem(key, data);
				} else {
					s.removeItem(key);
				}
			}
			
			if (callback > -1) {
				serverRpc.callLocalStorageItemCallback(callback, supported, key, oldData, data);
			}
		}

		/*
		 * (non-Javadoc)
		 * @see eu.maxschuster.vaadin.localstorage.client.LocalStorageClientRpc#clear()
		 */
		@Override
		public void clear(int callback) {
			boolean supported = isSupported();
			
			if (supported) {
				Storage s = Storage.getLocalStorageIfSupported();
				s.clear();
			}
			
			if (callback > -1) {
				serverRpc.callLocalStorageItemCallback(callback, supported, null, null, null);
			}
		}
		
	};
	
	/**
	 * {@link ServerRpc} that allows to call server side methods.
	 */
	private final LocalStorageServerRpc serverRpc =
			RpcProxy.create(LocalStorageServerRpc.class, this);
	
	/**
	 * Handler for {@link StorageEvent}s
	 */
	private final LocalStorageHandler storageEventHandler = new LocalStorageHandler();

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.client.extensions.AbstractExtensionConnector#extend(com.vaadin.client.ServerConnector)
	 */
	@Override
	protected void extend(ServerConnector target) {
		registerRpc(LocalStorageClientRpc.class, clientRpc);
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.client.ui.AbstractConnector#getState()
	 */
	@Override
	public LocalStorageState getState() {
		return (LocalStorageState) super.getState();
	}
	
	/**
	 * @return localStorage is supported
	 */
	private boolean isSupported() {
		return !getState().simulateNotSupported && Storage.isLocalStorageSupported();
	}

	/* (non-Javadoc)
	 * @see com.vaadin.client.ui.AbstractConnector#onStateChanged(com.vaadin.client.communication.StateChangeEvent)
	 */
	@Override
	public void onStateChanged(StateChangeEvent stateChangeEvent) {
		super.onStateChanged(stateChangeEvent);
		if (stateChangeEvent.hasPropertyChanged("registeredEventListeners")) {
			Set<String> listeners = getState().registeredEventListeners;
			if (listeners != null) {
				// Fire update events when a listener is defined
				if (listeners.contains(ItemUpdateEvent.ITEM_UPDATE_EVENT_IDENTIFIER)) {
					if (!storageEventHandler.isAttached()) {
						Storage.addStorageEventHandler(storageEventHandler);
						storageEventHandler.setAttached(true);
					}
				} else {
					if (storageEventHandler.isAttached()) {
						Storage.removeStorageEventHandler(storageEventHandler);
						storageEventHandler.setAttached(true);
					}
				}
			}
		}
	}
	
	/**
	 * {@link Handler} implementation with additional
	 * {@link LocalStorageHandler#isAttached()} method.
	 * @author Max Schuster
	 */
	private class LocalStorageHandler implements StorageEvent.Handler {
		
		/**
		 * Is already attached
		 */
		private boolean attached = false;
		
		@Override
		public void onStorageChange(StorageEvent event) {
			serverRpc.triggerItemUpdateEvent(event.getKey(), event.getOldValue(), event.getNewValue());
		}

		/**
		 * @return Is already attached
		 */
		public boolean isAttached() {
			return attached;
		}

		/**
		 * @param attached Set the attached property
		 */
		public void setAttached(boolean attached) {
			this.attached = attached;
		}
		
	}

}
