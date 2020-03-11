package com.app.cache;

import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class CustomCache implements Cache {

	private static final int eraseTime = 10;
	private final ConcurrentHashMap<String, CacheObject> cache = new ConcurrentHashMap<>();
	private int limit = 100;

//this is our private class for cache object
	private static class CacheObject implements Comparable<CacheObject> {

		public CacheObject(Object value, Long expiryTime) {
			this.value = value;
			this.expiryTime = expiryTime;
		}

		private Object value;
		private Long expiryTime;

		public CacheObject getInstance() {
			return this;
		}

		public Object getValue() {
			this.expiryTime = System.currentTimeMillis() + 6000;
			return value;
		}

		public void setValue(Object value) {
			this.value = value;
		}

		public Long getExpiryTime() {
			return expiryTime;
		}

		public void setExpiryTime(Long expiryTime) {
			this.expiryTime = expiryTime;
		}

		public boolean isExpired() {
			return System.currentTimeMillis() > expiryTime;
		}

		@Override
		public int compareTo(CacheObject o) {
			return o.getExpiryTime().compareTo(this.getExpiryTime());
		}
	}

	public CustomCache() {
		/*
		 * we have a thread running on initialization we sleep every 10 seconds then we
		 * check to remove instances of our expired cache objects
		 */
		Thread cleanerThread = new Thread(() -> {
			while (!Thread.currentThread().isInterrupted()) {
				try {
					Thread.sleep(eraseTime * 1000);
					// remove if time is up
					cache.entrySet().removeIf(entry -> Optional.ofNullable(entry.getValue())
							.map(CacheObject::getInstance).map(CacheObject::isExpired).orElse(false));

					// remove if size limit
					if (cache.size() > limit) {
						removeFirstItemInCache();
					}

					// remove the least used
					if (!cache.isEmpty()) {
						List<Entry<String, CacheObject>> keys = cache.entrySet().stream()
								.sorted(Map.Entry.comparingByValue()).collect(Collectors.toList());
						cache.remove(keys.get(0).getKey());
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		});
		cleanerThread.setDaemon(true);
		cleanerThread.start();
	}

	private void removeFirstItemInCache() {
		List<Entry<String, CacheObject>> keys = cache.entrySet().stream().sorted(Map.Entry.comparingByValue())
				.collect(Collectors.toList());
		cache.remove(keys.get(0).getKey());
	}

	public Enumeration<String> getKeys() {
		return cache.keys();
	}

	@Override
	public void addItem(String key, Object v) {

		if (key == null) {
			return;
		}
		if (v == null) {
			cache.remove(key);
		} else {
			// so we set the size to 100
			if (cache.size() > 100) {
				// if the map contains the key and the size of the map is greater than 100 we
				// will remove the key and add the new value
				if (cache.contains(key)) {
					cache.remove(key);
				} else {
					removeFirstItemInCache();
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
		// removes a key
		cache.remove(key);
	}

	@Override
	public Object get(String key) {
		// using streams we will use optional class to make sure no nulls/ then we get
		// an instance of our cache object
		// we filter any object that is expired. we map the value if something was not
		// expired and return it. if we have nothing we return null;
		return Optional.ofNullable(cache.get(key)).map(CacheObject::getInstance)
				.filter(cacheObject -> !cacheObject.isExpired()).map(CacheObject::getValue).orElse(null);
	}

	@Override
	public void empty() {
		// clear the whole map
		cache.clear();
	}

	@Override
	public long size() {
		// return the size of the map.
		return cache.size();
	}

}
