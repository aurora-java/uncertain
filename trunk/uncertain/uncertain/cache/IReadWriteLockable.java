package uncertain.cache;

import java.util.concurrent.locks.Lock;

public interface IReadWriteLockable {

	public Lock readLock();

	public Lock writeLock();
}
