/**
 * 
 */
package UE4_Assets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author FunGames
 *
 */
public class FBinaryWriter {
	private	ByteArrayOutputStream os;
	
	public FBinaryWriter(ByteArrayOutputStream os) {
		this.os = os;
	}
	
	public void write(byte[] data) throws IOException {
		os.write(data);
	}
	public void writeInt32(int number) throws IOException {
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.putLong(number);
		os.write(bb.array());
	}
	public void writeFloat32(float number) throws IOException {
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.putFloat(number);
		os.write(bb.array());
	}
	public void writeUInt16(int number) throws IOException {
		ByteBuffer bb = ByteBuffer.allocate(2);
		bb.position(0);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.putShort((short) number); //Don't actually know how to process that
		os.write(bb.array());
	}
	public void writeUInt32(long number) throws IOException {
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.position(0);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.putInt((int) number); //Don't actually know how to process that
		os.write(bb.array());
	}
	
	public byte[] toByteArray() {
		return os.toByteArray();
	}
	
	public int size() {
		return os.size();
	}
}
