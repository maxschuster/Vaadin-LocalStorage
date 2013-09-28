/*
 * eu.maxschuster.vaadin.localstorage.test.LocalStorageUI.java
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

/**
 * Callback for a {@link LocalStorage} item action
 * @author Max Schuster <dev@maxschuster.eu>
 */
public interface LocalStorageItemCallback {

	/**
	 * Gets called on success
	 * @param item The item
	 * @return void
	 */
	public void onSuccess(LocalStorageItem item);
	
	/**
	 * Gets called on error
	 * @param key Key of the failed item
	 * @return void
	 */
	public void onError(String key);
	
}