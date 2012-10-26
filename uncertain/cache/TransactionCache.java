package uncertain.cache;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TransactionCache implements ITransactionCache {

	ICache mainCache;
	ICache assistCache;
	boolean isICacheClone = false;

//	protected AtomicBoolean can_not_read = new AtomicBoolean(false);

	private ReentrantReadWriteLock rwlock = new ReentrantReadWriteLock();
	private Lock writeLock = rwlock.writeLock();
	private Lock readLock = rwlock.readLock();

	private ReentrantLock trxLock = new ReentrantLock();

	public TransactionCache(ICache cache) {
		this.mainCache = cache;
		isICacheClone = isICacheClone(cache);
	}

	public Object getValue(Object key) {
//		while (can_not_read) {
//			try {
//				Thread.sleep(1);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
		readLock.lock();
		try{
			return mainCache.getValue(key);
		}finally{
			readLock.unlock();
		}
	}

	public boolean setValue(Object key, Object value) {
		checkTrxStatus();
		assistCache.setValue(key, value);
		return true;
	}

	public boolean setValue(Object key, int timeout, Object value) {
		checkTrxStatus();
		assistCache.setValue(key, timeout, value);
		return true;
	}

	public void remove(Object key) {
		checkTrxStatus();
		assistCache.remove(key);
	}

	public void clear() {
		checkTrxStatus();
		assistCache.clear();
	}

	public void beginTransaction() {
		trxLock.lock();
		if (!isICacheClone)
			assistCache = mainCache;
		else {
			if (assistCache != null)
				assistCache.clear();
			assistCache = (ICache) ((ICacheClone) mainCache).cacheClone();
		}
	}

	public void commit() {
		checkTrxStatus();
		try {
			if (isICacheClone) {
				// can_not_read = true;
				writeLock.lock();
				try {
					mainCache.clear();
					((ICacheClone) mainCache).cacheCopy((ICacheClone) assistCache);
				} finally {
					// can_not_read = false;
					writeLock.unlock();
				}
				assistCache.clear();
				assistCache = null;
			}
		} finally {
			trxLock.unlock();
		}
	}

	public void rollback() {
		checkTrxStatus();
		try {
			if (isICacheClone) {
				assistCache.clear();
				assistCache = null;
			}
		} finally {
			trxLock.unlock();
		}
	}

	private boolean checkTrxStatus() {
		if (!isICacheClone)
			return true;
		if (!trxLock.isHeldByCurrentThread()) {
			throw new IllegalStateException("call beginTransaction first, and remeber call commit or rollback after.");
		}
		return true;
	}

	private boolean isICacheClone(ICache cache) {
		return cache instanceof ICacheClone;
	}
}
