package eu.maxschuster.vaadin.localstorage.shared;


public interface LocalStorageItemCallback {
	
	public void onError();

	public void onSussess(LocalStorageItem item);
	
}