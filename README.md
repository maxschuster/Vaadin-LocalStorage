# License
Apache License, Version 2.0

#Usage

```java
LocalStorageItemCallback cb = new LocalStorageItemCallback() {

	@Override
	public void onSuccess(LocalStorageItem item) {
		System.out.println(item);
	}
	
	@Override
	public void onError() {
		System.out.println("ERROR");
	}
	
};

LocalStorage ls = LocalStorage.getCurrent();

ls.setItem("test", "Lorem ipsum dolor sit amet, consectetur adipiscing elit.", cb);

ls.setItem("test", "Duis commodo.", cb);

ls.getItem("test", cb);

ls.clear();
```