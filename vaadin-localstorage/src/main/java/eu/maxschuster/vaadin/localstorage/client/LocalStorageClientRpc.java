/*
 * eu.maxschuster.vaadin.localstorage.client.LocalStorageClientRpc.java
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

import com.google.gwt.storage.client.Storage;
import com.vaadin.shared.communication.ClientRpc;

/**
 * Defines client side methods that can get invoked from the server side. 
 * @author Max Schuster
 */
public interface LocalStorageClientRpc extends ClientRpc {
	
	/**
	 * Trigger {@link Storage#getItem(String)} on client-side
	 * @param key Items key
	 * @param callback Callback id. If &lt; 0 no callback will get triggered.
	 */
	public void getItem(String key, int callback);
	
	/**
	 * Trigger {@link Storage#setItem(String, String)} on client-side
	 * @param key Items key
	 * @param data Items data. If data is null the item will get removed.
	 * @param callback Callback id. If &lt; 0 no callback will get triggered.
	 */
	public void setItem(String key, String data, int callback);
	
	/**
	 * Trigger {@link Storage#clear()} on client-side
	 * @param callback Callback id. If &lt; 0 no callback will get triggered.
	 */
	public void clear(int callback);
	
}
