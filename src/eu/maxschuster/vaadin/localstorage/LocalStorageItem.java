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

import java.io.Serializable;

/**
 * {@link LocalStorage} item
 * @author Max Schuster <dev@maxschuster.eu>
 */
public class LocalStorageItem implements Serializable {
	
	private static final long serialVersionUID = 611655473348421371L;
	
	/**
	 * Items key
	 */
	private String key = null;

	/**
	 * Items old data
	 */
	private String oldData = null;
	
	/**
	 * Items current data
	 */
	private String data = null;

	public LocalStorageItem() { }

	/**
	 * @param key Items key
	 * @param oldData Items old data
	 * @param data Items current data
	 */
	public LocalStorageItem(String key, String oldData, String data) {
		super();
		this.key = key;
		this.oldData = oldData;
		this.data = data;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LocalStorageItem [key=" + key + ", oldData=" + oldData
				+ ", data=" + data + "]";
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the oldData
	 */
	public String getOldData() {
		return oldData;
	}

	/**
	 * @param oldData the oldData to set
	 */
	public void setOldData(String oldData) {
		this.oldData = oldData;
	}

	/**
	 * @return the data
	 */
	public String getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(String data) {
		this.data = data;
	}

}
