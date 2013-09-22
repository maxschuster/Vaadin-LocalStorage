/*
 * eu.maxschuster.vaadin.localstorage.shared.LocalStorageState.java
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

import com.vaadin.server.Extension;
import com.vaadin.shared.communication.SharedState;

import eu.maxschuster.vaadin.localstorage.LocalStorage;

/**
 * {@link SharedState} for the {@link LocalStorage} {@link Extension}.
 * @author Max Schuster <dev@maxschutser.eu>
 */
@SuppressWarnings("serial")
public class LocalStorageState extends SharedState {

	/**
	 * Live updates are enabled.
	 */
	public boolean liveUpdate = true;
	
	/**
	 * Browser supports localStorage.
	 */
	public Boolean supported = null;
	
	/**
	 * {@link LocalStorage} is ready for use.
	 */
	public boolean ready = false;
	
}
