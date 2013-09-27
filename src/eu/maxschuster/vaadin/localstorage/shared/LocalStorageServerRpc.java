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

import com.vaadin.shared.communication.ServerRpc;

/**
 * Defines server side methods that can get invoked from the client side. 
 * @author Max Schuster <dev@maxschutser.eu>
 */
public interface LocalStorageServerRpc extends ServerRpc {

	/**
	 * @param callback Callback id. If < 0 no callback will get triggered.
	 * @param success Action was success full
	 * @param key Items key
	 * @param oldData Items old data
	 * @param data Items new data
	 * @return void
	 */
	public void callLocalStorageItemCallback(
			int callback, boolean success,
			String key, String oldData, String data);
	
}
