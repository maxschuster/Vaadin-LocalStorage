# Vaadin LocalStorage
Vaadin 7 Add-on to access localStorage.

Developed as replacement for "keep me signed in" cookies
in Applications with Push enabled.

# License
Apache License, Version 2.0

# Compatibility
[Vaadin 7](https://vaadin.com/home "Vaadin Homepage")

#Usage

```java
LocalStorageItemCallback cb = new LocalStorageItemCallback() {

	@Override
	public void onSuccess(LocalStorageItem item) {
		System.out.println(item);
	}
	
	@Override
	public void onError(String key) {
		System.out.println("ERROR item = \"" + key + "\"");
	}
	
};

LocalStorage ls = LocalStorage.getCurrent();

ls.setItem("test", "Lorem ipsum dolor sit amet, consectetur adipiscing elit.", cb);

ls.setItem("test", "Duis commodo.", cb);

ls.getItem("test", cb);

ls.clear();
```
See also: *"eu.maxschuster.vaadin.localstorage.test.LocalStorageUI.java"*

# Installation

Export the Project as "Vaadin Add-on Package" or take a ".jar" file from the dist folder
and put it into your Projects WEB-INF/lib folder.

**Now recompile your widgetset.**

# Testing

## localStorage is not supported

If you set eu.maxschuster.vaadin.localstorage.LocalStorage.setSimulateNotSupported(boolean)
to true you can see how your application will behave when localStorage is not supported.