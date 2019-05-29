/**
 * 
 */
package UE4_Packages;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author FunGames
 *
 */
public class Float8 {
	
	public static float readFloat(byte[] buff, boolean littleEndian) {
		ByteBuffer bb = ByteBuffer.allocate(4).put(buff);
		bb.position(0);
		if (littleEndian)
			bb.order(ByteOrder.LITTLE_ENDIAN);
		return bb.getFloat();
	}
	public static byte[] floatToLittleEndian(float numero) {
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.putFloat(numero);
		return bb.array();
	}
}