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

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.storage.client.Storage;
import com.google.gwt.storage.client.StorageEvent;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.shared.communication.ClientRpc;
import com.vaadin.shared.communication.ServerRpc;
import com.vaadin.shared.ui.Connect;

import eu.maxschuster.vaadin.localstorage.LocalStorage;
import eu.maxschuster.vaadin.localstorage.shared.LocalStorageServerRpc;
import eu.maxschuster.vaadin.localstorage.shared.LocalStorageState;

/**
 * Client side part of {@link LocalStorage}.
 * @author Max Schuster <dev@maxschutser.eu>
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
		 * @see eu.maxschuster.vaadin.localstorage.client.LocalStorageClientRpc#setItem(java.lang.String, java.lang.String)
		 */
		@Override
		public void setItem(String key, String data) {
			if (!Storage.isLocalStorageSupported())
				return;
			Storage.getLocalStorageIfSupported().setItem(key, data);
		}

		/*
		 * (non-Javadoc)
		 * @see eu.maxschuster.vaadin.localstorage.client.LocalStorageClientRpc#removeItem(java.lang.String)
		 */
		@Override
		public void removeItem(String key) {
			if (!Storage.isLocalStorageSupported())
				return;
			Storage.getLocalStorageIfSupported().removeItem(key);
		}

		/*
		 * (non-Javadoc)
		 * @see eu.maxschuster.vaadin.localstorage.client.LocalStorageClientRpc#clear()
		 */
		@Override
		public void clear() {
			if (!Storage.isLocalStorageSupported())
				return;
			Storage.getLocalStorageIfSupported().clear();
		}

		/*
		 * (non-Javadoc)
		 * @see eu.maxschuster.vaadin.localstorage.client.LocalStorageClientRpc#refresh()
		 */
		@Override
		public void refresh() {
			if (!Storage.isLocalStorageSupported())
				return;
			Storage storage = Storage.getLocalStorageIfSupported();
			LocalStorageConnector.this.serverRpc.refresh(storageToMap(storage));
		}
		
	};
	
	/**
	 * {@link ServerRpc} that allows to call server side methods.
	 */
	private final LocalStorageServerRpc serverRpc =
			RpcProxy.create(LocalStorageServerRpc.class, this);
	
	/**
	 * Constructor
	 */
	public LocalStorageConnector() {
		registerRpc(LocalStorageClientRpc.class, clientRpc);
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.client.extensions.AbstractExtensionConnector#extend(com.vaadin.client.ServerConnector)
	 */
	@Override
	protected void extend(ServerConnector target) {
		if (!Storage.isLocalStorageSupported()) {
			serverRpc.readyNotSupported();
			return;
		}
		Storage storage = Storage.getLocalStorageIfSupported();
		Storage.addStorageEventHandler(new StorageEvent.Handler() {

			@Override
			public void onStorageChange(StorageEvent event) {
				if (getState().liveUpdate) {
					serverRpc.updateItem(event.getKey(), event.getOldValue(), event.getNewValue());
				}
			}
			
		});
		serverRpc.ready(storageToMap(storage));
	}
	
	/**
	 * Creates a {@link Map} that contains all entries of the given {@link Storage}.
	 * @param storage {@link Storage} that should get turned into a {@link Map}.
	 * @return {@link Map} of all entries.
	 */
	private Map<String, String> storageToMap(Storage storage) {
		int length = storage.getLength();
		Map<String, String> map = new HashMap<String, String>(length);
		
		for (int i = 0; i < length; i++) {
			String key = storage.key(i);
			String value = storage.getItem(key);
			map.put(key, value);
		}
		
		return map;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.client.ui.AbstractConnector#getState()
	 */
	@Override
	public LocalStorageState getState() {
		return (LocalStorageState) super.getState();
	}

	/* (non-Javadoc)
	 * @see com.vaadin.client.ui.AbstractConnector#onStateChanged(com.vaadin.client.communication.StateChangeEvent)
	 */
	@Override
	public void onStateChanged(StateChangeEvent stateChangeEvent) {
		super.onStateChanged(stateChangeEvent);
	}

}
