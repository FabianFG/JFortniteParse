/**
 * 
 */
package UE4.serialize.exception;

/**
 * @author FunGames
 *
 */
public class SerializationException extends Exception {

	/**
	 * @param string
	 */
	public SerializationException(String string) {
		super(string);
	}
	
	public SerializationException(String string, Throwable t) {
		super(string, t);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -977074239374057233L;

}
