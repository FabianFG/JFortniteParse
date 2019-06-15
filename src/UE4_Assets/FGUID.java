package UE4_Assets;

import javax.xml.bind.DatatypeConverter;

import UE4.FArchive;

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
	
	public FGUID(long part1, long part2, long part3, long part4) {
		guid = "";

		guid += (part1 + "-");
		guid += (part2 + "-");
		guid += (part3 + "-");
		guid += part4;
	}
	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((guid == null) ? 0 : guid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FGUID other = (FGUID) obj;
		if (guid == null) {
			if (other.guid != null)
				return false;
		} else if (!guid.equals(other.guid))
			return false;
		return true;
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
