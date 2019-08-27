/**
 * 
 */
package UE4_Assets;

import annotation.Serializable;
import annotation.UInt32;
import lombok.Data;

/**
 * @author FunGames
 *
 */
@Data
@Serializable
public class UInterfaceProperty {
	@UInt32 private long interfaceNumber;
	
}
