/**
 * 
 */
package UE4_Assets;

import java.util.ArrayList;
import java.util.List;

import UE4.FArchive;

/**
 * @author FunGames
 *
 */
public class FMultisizeIndexContainer {
	
	
	private byte dataSize;
	private List<?> indices; //Of either UInt16 or UInt32, depending on dataSize
	
	@SuppressWarnings("unused")
	public FMultisizeIndexContainer(FArchive Ar) throws ReadException {
		dataSize = Ar.readUInt8();
		int _elementSize = Ar.readInt32();
		
		switch(dataSize) {
		case 2:
			indices = new ArrayList<Integer>();
			indices = Ar.readTArrayOfUInt16();
			break;
		case 4:
			indices = new ArrayList<Long>();
			indices = Ar.readTArrayOfUInt32();
			break;
		default:
			throw new ReadException("Unkown MultisizeIndexContainer format: " + dataSize, Ar.Tell()-5);
		}
	}

	public byte getDataSize() {
		return dataSize;
	}

	public List<?> getIndices() {
		return indices;
	}
}
