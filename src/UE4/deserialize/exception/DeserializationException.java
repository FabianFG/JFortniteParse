/**
 * 
 */
package UE4.deserialize.exception;

/**
 * @author FunGames
 *
 */
public class DeserializationException extends Exception {

	/**
	 * @param string
	 */
	public DeserializationException(String string) {
		super(string);
	}
	
	public DeserializationException(String string, Throwable t) {
		super(string, t);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -977074239374057233L;

}
