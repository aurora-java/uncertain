package uncertain.cache;

public interface ITransactionCache extends ICache{

	public void beginTransaction();
	
	public void commit();
	
	public void rollback();
}
