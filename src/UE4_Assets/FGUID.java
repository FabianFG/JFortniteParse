package UE4_Assets;

import javax.xml.bind.DatatypeConverter;

import UE4.FArchive;
import annotation.CustomSerializable;
import lombok.Data;

/**
 * 
 */

/**
 * @author FunGames
 *
 */
@Data
@CustomSerializable
public class FGUID {

	private long part1;
	private long part2;
	private long part3;
	private long part4;
	
	private String guid;
	private String hexString;

	public FGUID(String guid) {
		this.guid = guid;
	}
	
	public FGUID(FArchive Ar) throws ReadException {
		this(Ar.serialize(16));
	}

	public FGUID(byte[] bytes) {
		try {
			this.hexString = DatatypeConverter.printHexBinary(bytes);
			FArchive temp = new FArchive(bytes);
			temp.Seek(0);
			guid = "";

			long part1 = temp.readUInt32();
			guid += (part1 + "-");
			long part2 = temp.readUInt32();
			guid += (part2 + "-");
			long part3 = temp.readUInt32();
			guid += (part3 + "-");
			long part4 = temp.readUInt32();
			guid += part4;
		} catch (ReadException e) {

		}
	}
	
	public FGUID(long part1, long part2, long part3, long part4) {
		guid = "";

		guid += (part1 + "-");
		guid += (part2 + "-");
		guid += (part3 + "-");
		guid += part4;
	}
	

	public String getString() {
		return guid;
	}
	
	public String getHexString() {
		return hexString;
	}
	public boolean isMainGUID() {
		return guid.equals("0-0-0-0");
	}
}
