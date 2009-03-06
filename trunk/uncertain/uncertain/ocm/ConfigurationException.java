/**
 * Created on: 2004-9-9 15:05:51
 * Author:     zhoufan
 */
package uncertain.ocm;

/**
 * 
 */
public class ConfigurationException extends Error {

	/**
	 * Constructor for ConfigurationException.
	 */
	public ConfigurationException() {
		super();
	}

	/**
	 * Constructor for ConfigurationException.
	 * @param message
	 */
	public ConfigurationException(String message) {
		super(message);
	}

	/**
	 * Constructor for ConfigurationException.
	 * @param message
	 * @param cause
	 */
	public ConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor for ConfigurationException.
	 * @param cause
	 */
	public ConfigurationException(Throwable cause) {
		super(cause);
	}

}
