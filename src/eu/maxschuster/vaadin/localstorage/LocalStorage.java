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

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.storage.client.Storage;
import com.vaadin.server.AbstractClientConnector;
import com.vaadin.server.AbstractExtension;
import com.vaadin.server.Extension;
import com.vaadin.shared.communication.ServerRpc;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.UI;

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
	
	private int itemCallbackKey = 0;
	private Map<Integer, LocalStorageItemCallback> itemCallbacks = new HashMap<Integer, LocalStorageItemCallback>();
	
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
				LocalStorageItemCallback cb = itemCallbacks.get(callback);
				if(cb != null) {
					if (success) {
						cb.onSuccess(new LocalStorageItem(key, oldData, data));
					} else {
						cb.onError();
					}
					itemCallbacks.remove(callback);
				}
			}
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
	public AbstractComponent getParent() {
		return (AbstractComponent) super.getParent();
	}
	
	/**
	 * Adds a callback and returns a callback id
	 * @param callback The callback
	 * @return Callback id
	 */
	private int addCallback(LocalStorageItemCallback callback) {
		if (callback == null)
			return -1;
		synchronized (itemCallbacks) {
			int id = itemCallbackKey++;
			itemCallbacks.put(id, callback);
			return id;
		}
	}
	
	/**
	 * Gets the items data from the {@link Storage} on the client-side.
	 * @param key Items key
	 * @param callback A callback
	 * @return void
	 */
	public void getItem(String key, LocalStorageItemCallback callback) {
		if (callback == null)
			throw new NullPointerException("Getting an item from LocalStorage doesn't make much sense...");
		getRpcProxy(LocalStorageClientRpc.class).getItem(key, addCallback(callback));
	}
	
	/**
	 * Sets the items data in the {@link Storage} on the
	 * client-side and calls the given callback.
	 * @param key Items key
	 * @param data Items new data
	 * @param callback A callback
	 * @return void
	 */
	public void setItem(String key, String data, LocalStorageItemCallback callback) {
		getRpcProxy(LocalStorageClientRpc.class).setItem(key, data, addCallback(callback));
	}
	
	/**
	 * Sets the items data in the {@link Storage} on the client-side
	 * @param key Items key
	 * @param data Items new data
	 * @return void
	 */
	public void setItem(String key, String data) {
		setItem(key, data, null);
	}
	
	/**
	 * Clears the {@link Storage} on the client-side and
	 * calls the given callback
	 * @param callback A callback
	 * @return void
	 */
	public void clear(LocalStorageItemCallback callback) {
		getRpcProxy(LocalStorageClientRpc.class).clear(addCallback(callback));
	}
	
	/**
	 * Clears the {@link Storage} on the client-side
	 * @return void
	 */
	public void clear() {
		clear(null);
	}

}
