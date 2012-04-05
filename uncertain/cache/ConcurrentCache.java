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

	@Override
	public Object getValue(Object key) {
		read.lock();
		try {
			return cache.getValue(key);
		} finally {
			read.unlock();
		}
	}

	@Override
	public boolean setValue(Object key, Object value) {
		return cache.setValue(key, value);
	}

	@Override
	public boolean setValue(Object key, int timeout, Object value) {
		return cache.setValue(key, timeout, value);
	}

	@Override
	public void remove(Object key) {
		cache.remove(key);
	}

	@Override
	public void clear() {
		cache.clear();
	}

	@Override
	public Lock readLock() {
		return read;
	}

	@Override
	public Lock writeLock() {
		return write;
	}

}
