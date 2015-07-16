package uncertain.ocm;

public interface IObjectCreationListener {
	
	/**
	 * Called by IObjectCreator instance to notify a new instance is created
	 * @param instance
	 */
	public void onInstanceCreate( Object instance );

}
