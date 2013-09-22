/*
 * eu.maxschuster.vaadin.localstorage.shared.LocalStorageServerRpc.java
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

package eu.maxschuster.vaadin.localstorage.shared;

import java.util.Map;

import com.vaadin.shared.communication.ServerRpc;

/**
 * Defines server side methods that can get invoked from the client side. 
 * @author Max Schuster <dev@maxschutser.eu>
 */
public interface LocalStorageServerRpc extends ServerRpc {
	
	/**
	 * Ready callback if the browser does not support localStorage.
	 */
	public void readyNotSupported();
	
	/**
	 * Ready callback if the browser supports localStorage.
	 * @param storage {@link Map} that contains all items of localStorage.
	 */
	public void ready(Map<String, String> storage);
	
	/**
	 * Refresh callback.
	 * @param storage {@link Map} that contains all items of localStorage.
	 */
	public void refresh(Map<String, String> storage);
	
	/**
	 * Item update callback.
	 * @param key Key of the item that has changed.
	 * @param oldValue Old value of the item.
	 * @param newValue New value of the item.
	 */
	public void updateItem(String key, String oldValue, String newValue);

}
