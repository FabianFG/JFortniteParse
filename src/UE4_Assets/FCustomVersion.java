/**
 * 
 */
package UE4_Assets;

import annotation.Int32;
import annotation.Serializable;
import lombok.Data;

/**
 * @author FunGames
 *
 */
@Data
@Serializable
public class FCustomVersion {
	private FGUID key;
	@Int32 private int version;	
}
