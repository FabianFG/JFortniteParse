/**
 * 
 */
package Gltf;

import java.io.OutputStream;

import org.json.simple.JSONObject;

/**
 * @author FunGames
 *
 */
public class BufferView {
	private byte[] data;
	private int byteOffset;
	private int bufferIndex;
	private int bufferViewIndex;
	
	public byte[] getData() {
		return data;
	}

	public int getByteOffset() {
		return byteOffset;
	}

	public int getBufferIndex() {
		return bufferIndex;
	}

	public BufferView(byte[] data, int byteOffset, int bufferViewIndex, int bufferIndex) {
		this.data = data;
		this.byteOffset = byteOffset;
		this.bufferViewIndex = bufferViewIndex;
		this.bufferIndex = bufferIndex;
	}
	
	public int getBufferViewIndex() {
		return bufferViewIndex;
	}

	public JSONObject jsonify() {
		JSONObject res = new JSONObject();
		res.put("byteLength", data.length);
		res.put("byteOffset", byteOffset);
		res.put("buffer", bufferIndex);
		return res;
	}
}
