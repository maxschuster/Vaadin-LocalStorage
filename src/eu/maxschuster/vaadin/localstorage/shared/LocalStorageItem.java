package eu.maxschuster.vaadin.localstorage.shared;

import java.io.Serializable;

public class LocalStorageItem implements Serializable {
	
	private static final long serialVersionUID = 611655473348421371L;
	
	private String key = null;
	
	private String oldData = null;
	
	private String data = null;

	public LocalStorageItem() { }

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
