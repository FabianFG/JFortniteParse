/**
 * 
 */
package UE4_PakFile;

import UE4.FArchive;
import UE4_Assets.ReadException;
import annotation.CustomSerializable;
import lombok.Data;

/**
 * @author FunGames
 *
 */
@Data
@CustomSerializable
public class FPakCompressionMethod {
	//This class does not exist in the UE4. Just here to handle it easier
	public static final int binaryLength = 32;
	
	private String compressionName;
	private byte[] compressionNameBytes;

	public FPakCompressionMethod(String name) {
		this.compressionNameBytes = new byte[32];
		this.compressionName = name;
	}
	
	public FPakCompressionMethod(FArchive Ar) throws ReadException {
		this.compressionNameBytes = Ar.serialize(32);
		this.compressionName = readNullTerminatedString(this.compressionNameBytes);		
	}

	private String readNullTerminatedString(byte[] compressionNameBytes) {
		String res = "";
		for(byte b : compressionNameBytes) {
			if(b!= 0x00) {
				res+= (char)b;
			}
			else {
				break;
			}
		}
		return res;
	}	
	
}
