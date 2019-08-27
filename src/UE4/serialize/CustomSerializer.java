/**
 * 
 */
package UE4.serialize;

import UE4.FArchiveWriter;
import UE4.serialize.exception.SerializationException;

/**
 * @author FunGames
 *
 */
public interface CustomSerializer {
	public void serialize(FArchiveWriter Ar) throws SerializationException;
}
