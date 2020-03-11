package com.app.cache;

import java.util.Enumeration;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CustomCache implements Cache {

	private static final int eraseTime = 10;
	private final ConcurrentHashMap<String, CacheObject> cache = new ConcurrentHashMap<>();
	private int limit = 100;

//this is our private class for cache object
	private static class CacheObject {

		public CacheObject(Object value, long expiryTime) {
			this.value = value;
			this.expiryTime = expiryTime;
		}

		private Object value;
		private long expiryTime;

		public CacheObject getInstance() {
			return this;
		}

		public Object getValue() {
			return value;
		}

		public void setValue(Object value) {
			this.value = value;
		}

		public long getExpiryTime() {
			return expiryTime;
		}

		public void setExpiryTime(long expiryTime) {
			this.expiryTime = expiryTime;
		}

		public boolean isExpired() {
			return System.currentTimeMillis() > expiryTime;
		}
	}

	public CustomCache() {
		/*
		 * we have a thread running on initialization
		 * we sleep every 10 seconds then we check to remove instances of our expired cache objects
		 */
		Thread cleanerThread = new Thread(() -> {
			while (!Thread.currentThread().isInterrupted()) {
				try {
					Thread.sleep(eraseTime * 1000);
					cache.entrySet().removeIf(entry -> Optional.ofNullable(entry.getValue())
							.map(CacheObject::getInstance).map(CacheObject::isExpired).orElse(false));
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		});
		cleanerThread.setDaemon(true);
		cleanerThread.start();
	}

	@Override
	public void addItem(String key, Object v) {

		if (key == null) {
			return;
		}
		if (v == null) {
			cache.remove(key);
		} else {
			//so we set the size to 100
			if (cache.size() > 100) {
				//if the map contains the key and the size of the map is greater than 100 we will remove the key and add the new value
				if (cache.contains(key)) {
					cache.remove(key);
				} else {
					//so the cache is full and we dont have the inserted key. we will simply evicted the first object in the cache array.
					//why the first object? the map is unorganized but it we know that the first object in the cache map is the oldest simply because it was in there first...so logically
					// we remove that one and add our new one to the map. we dont use a replace ever to keep this rule true.
					Enumeration<String> entry = cache.keys();
					cache.remove(entry.nextElement());
				}
				long expiryTime = System.currentTimeMillis() + 6000;
				cache.put(key, new CacheObject(v, expiryTime));
			} else {
				long expiryTime = System.currentTimeMillis() + 6000;
				cache.put(key, new CacheObject(v, expiryTime));
			}
		}
	}

	@Override
	public void removeItem(String key) {
		//removes a key
		cache.remove(key);
	}

	@Override
	public Object get(String key) {
		//using streams we will use optional class to make sure no nulls/ then we get an instance of our cache object
		//we filter any object that is expired. we map the value if something was not expired and return it. if we have nothing we return null;
		return Optional.ofNullable(cache.get(key)).map(CacheObject::getInstance)
				.filter(cacheObject -> !cacheObject.isExpired()).map(CacheObject::getValue).orElse(null);
	}

	@Override
	public void empty() {
		//clear the whole map
		cache.clear();
	}

	@Override
	public long size() {
		//return the size of the map.
		return cache.size();
	}

}
