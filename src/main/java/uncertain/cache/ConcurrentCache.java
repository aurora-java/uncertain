package uncertain.cache;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

public class ConcurrentCache implements ICache,IReadWriteLockable {

	ICache cache;
	ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	WriteLock write = lock.writeLock();
	ReadLock read = lock.readLock();

	public ConcurrentCache(ICache cache) {
		this.cache = cache;
	}

	public Object getValue(Object key) {
		read.lock();
		try {
			return cache.getValue(key);
		} finally {
			read.unlock();
		}
	}

	public boolean setValue(Object key, Object value) {
		return cache.setValue(key, value);
	}

	public boolean setValue(Object key, int timeout, Object value) {
		return cache.setValue(key, timeout, value);
	}

	public void remove(Object key) {
		cache.remove(key);
	}

	public void clear() {
		cache.clear();
	}

	public Lock readLock() {
		return read;
	}

	public Lock writeLock() {
		return write;
	}

}
