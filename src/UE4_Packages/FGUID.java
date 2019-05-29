package UE4_Packages;

import javax.xml.bind.DatatypeConverter;

/**
 * 
 */

/**
 * @author FunGames
 *
 */
public class FGUID {

	private String guid;
	private String hexString;

	public FGUID(String guid) {
		this.guid = guid;
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

	public FGUID(FArchive Ar) throws ReadException {
		this(Ar.serialize(16));
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
