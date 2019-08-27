/**
 * 
 */
package UE4_Assets;

import UE4.deserialize.exception.DeserializationException;

/**
 * @author FunGames
 *
 */
public class ReadException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8799624181803794536L;
	public ReadException(String errorMessage, int offset) {
			super(errorMessage + " ; Offset: " + offset);  
    }
	public ReadException(String errorMessage) {
		super(errorMessage);  
}
	/**
	 * @param errorMessage
	 * @param e
	 */
	public ReadException(String errorMessage, DeserializationException e) {
		super(errorMessage, e);
	}
	
}
