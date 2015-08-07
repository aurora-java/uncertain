package uncertain.data;

public interface IDataPublisher {
	
	public void insert( String data_name, String primary_key, Object inserted_data );
	
	public void update( String data_name, String primary_key, Object updated_data );
	
	public void delete( String data_name, String primary_key, Object delete_data);
	
}
