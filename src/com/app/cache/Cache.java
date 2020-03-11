package com.app.cache;

public interface Cache {
	public void addItem(String key, Object v);
	public void removeItem(String key);
	public Object get(String key);
	public void empty();
	public long size();
	
}
