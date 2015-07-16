package uncertain.data;

public interface IDataDistributor {

	void setData(String key, Object data);

	void registerInstance(Object instance);

}