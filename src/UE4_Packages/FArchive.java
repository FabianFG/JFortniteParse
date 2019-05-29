/**
 * 
 */
package UE4_Packages;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author FunGames
 *
 */
public class FArchive {
	public int ArVer;
	public int ArLicenseeVer;
	public boolean IsLoading;
	public boolean LittleEndian;

	private int ArPos;
	private int ArStopper;

	// game-specific flags
	public int Game; // EGame
	public int Platform; // EPlatform

	public byte[] data;
	public NameMap nameMap;
	public ImportMap importMap;
	public ExportMap exportMap;
	
	//if uexp reader
	private Map<String, FArchive> payloads;
	public int uassetSize;
	public int uexpSize;

	public FArchive() {
		this.ArPos = 0;
		this.ArStopper = 0;
		this.ArVer = 100000;
		this.ArLicenseeVer = 0;
		this.IsLoading = true;
		this.LittleEndian = true;
		this.Game = EGame.GAME_UNKNOWN;
		this.payloads = new HashMap<>();
	}
	
	public FArchive(byte[] data) {
		this.ArPos = 0;
		this.data = data;
		this.ArStopper = data.length;
		this.ArVer = 100000;
		this.ArLicenseeVer = 0;
		this.IsLoading = true;
		this.LittleEndian = true;
		this.Game = EGame.GAME_UNKNOWN;
		this.payloads = new HashMap<>();
	}

	public void SetupFrom(FArchive Other) {
		this.ArVer = Other.ArVer;
		this.ArLicenseeVer = Other.ArLicenseeVer;
		this.LittleEndian = Other.LittleEndian;
		this.IsLoading = Other.IsLoading;
		this.Game = Other.Game;
		this.Platform = Other.Platform;
	}

	public int Engine() {
		return (this.Game & EGame.GAME_ENGINE);
	}

	public boolean IsCompressed() {
		return false; // Compression not supported
	}

	public void Seek(int Pos) throws ReadException {
		if(Pos <= this.ArStopper) {
			this.ArPos = Pos;
		} else {
			throw new ReadException(
					String.format("Seeking behind stopper (%d > %d", Pos, this.ArStopper));
		}
	}
	
	public void addPayload(String tag, FArchive payload) {
		this.payloads.put(tag, payload);
	}
	public FArchive getPayload(String tag) {
		return this.payloads.get(tag);
	}

	public int Tell() {
		return this.ArPos;
	}

	public int GetFileSize() {
		return 0;
	}


	public void SetStopper(int Pos) {
		this.ArStopper = Pos;
	}

	public int GetStopper() {
		return this.ArStopper;
	}

	public boolean IsStopper() {
		int stopper = GetStopper();
		return (stopper != 0) && (Tell() == stopper);
	}

	public static String StaticGetName() {
		return "FArchive";
	}

	public String GetName() {
		return StaticGetName();
	}

	public boolean IsA(String type) {
		return "FArchive".equals(type);
	}
	
	public int getUexpOffset() {
		return this.uassetSize;
	}
	public int getUbulkOffset() {
		return this.uassetSize + this.uexpSize;
	}

	// int8
	public char readInt8() throws ReadException {
		return (char) serialize(1)[0];
	}

	// uint8
	public byte readUInt8() throws ReadException {
		return serialize(1)[0];
	}

	// int16
	public short readInt16() throws ReadException {
		byte[] d = serialize(2);
		ByteBuffer bb = ByteBuffer.wrap(d);
		if (this.LittleEndian)
			bb.order(ByteOrder.LITTLE_ENDIAN);
		return bb.getShort();
	}

	// uint16
	public int readUInt16() throws ReadException {
		byte[] d = serialize(2);
		;
		ByteBuffer bb = ByteBuffer.wrap(d);
		if (this.LittleEndian)
			bb.order(ByteOrder.LITTLE_ENDIAN);
		return bb.getShort() & 0xFFFF;
	}
	
	// boolean
	public boolean readBoolean() throws ReadException {
		int b32 = this.readInt32();
		if(b32 == 0) {
			return false;
		} else if(b32 == 1) {
			return true;
		} else {
			//System.err.println("WARNING: Bool has no bool value at " + (this.Tell() - 4) + ", using 'true' as default");
			return true;
		}
	}
	// boolean
		public boolean readBooleanFromUInt8() throws ReadException {
			byte b8 = this.readUInt8();
			if(b8 == 0) {
				return false;
			} else if(b8 == 1) {
				return true;
			} else {
				//System.err.println("WARNING: Bool has no bool value at " + (this.Tell() - 1) + ", using 'true' as default");
				return true;
			}
		}
	// int32
	public int readInt32() throws ReadException {
		byte[] d = serialize(4);
		ByteBuffer bb = ByteBuffer.wrap(d);
		if (this.LittleEndian)
			bb.order(ByteOrder.LITTLE_ENDIAN);
		return bb.getInt();
	}

	// uint32
	public long readUInt32() throws ReadException {
		byte[] d = serialize(4);

		ByteBuffer bb = ByteBuffer.wrap(d);
		if (this.LittleEndian)
			bb.order(ByteOrder.LITTLE_ENDIAN);
		return bb.getInt() & 0xFFFFFFFFL;
	}

	// int64
	public long readInt64() throws ReadException {
		byte[] d = serialize(8);

		ByteBuffer bb = ByteBuffer.wrap(d);
		if (this.LittleEndian)
			bb.order(ByteOrder.LITTLE_ENDIAN);
		return bb.getLong();
	}

	// uint64
	public BigInteger readUInt64() throws ReadException {
		byte[] d = serialize(8);
		;
		return new BigInteger(1/* positive */, d);
	}

	// float32
	public float readFloat32() throws ReadException {
		byte[] d = serialize(4);
		ByteBuffer bb = ByteBuffer.wrap(d);
		if (this.LittleEndian)
			bb.order(ByteOrder.LITTLE_ENDIAN);
		return bb.getFloat();
	}

	// float16
	public float readFloat16() throws ReadException {
		int hbits = readUInt16();

		return toFloat(hbits);
	}

	// FString
	public String readString() throws ReadException {
		int length = this.readInt32();
		if(length > 65536 || length < -65536) {
			throw new ReadException("Invalid String length : " + length);
		}
		
		if(length == 0) {
			return "";
		}
		
		String string = "";
		if(length < 0) {
			int utf16length = length * -1;
			int[] stringData = new int[(utf16length -1) * 2];
			for(int i=0;i<stringData.length; i++) {
				stringData[i] = this.readUInt16();
			}
			
			string = new String(stringData, 0, utf16length);
		}
		else if (length > 0) {
			for (int i = 0; i < length - 1; i++) {
				string += this.readInt8();
			}
			byte nullTerminator = this.readUInt8();
			if (nullTerminator != 0x00) {
				throw new ReadException("Serialized FString is not null-terminated", this.ArPos);
			}
		}
		return string;

	}
	
	// FName
	public String readFName(NameMap nameMap) throws ReadException {
		int indexPos = this.Tell();
		int nameIndex = this.readInt32();
		this.readInt32(); // name number ?
		if(nameIndex >= 0 && nameIndex < nameMap.getLength()) {
			return nameMap.get(nameIndex).getName();
		} else {
			throw new ReadException(String.format("FName could not be read at offset %d Requested Index: %d, Name Map Size: %d", indexPos, nameIndex, nameMap.getLength()));
		}
	}

	// Array of FCustomVersion
	public List<FCustomVersion> readTArrayOfFCustomVersion() throws ReadException {

		int length = this.readInt32();

		List<FCustomVersion> list = new ArrayList<>();
		for (int i = 0; i < length; i++) {
			list.add(new FCustomVersion(this));
		}

		return list;
	}

	// Array of FGenerationInfo
	public List<FGenerationInfo> readTArrayOfFGenerationInfo() throws ReadException {

		int length = this.readInt32();

		List<FGenerationInfo> list = new ArrayList<>();
		for (int i = 0; i < length; i++) {
			list.add(new FGenerationInfo(this));
		}

		return list;
	}

	// Array of FCompressedChunk
	public List<FCompressedChunk> readTArrayOfFCompressedChunk() throws ReadException {

		int length = this.readInt32();

		List<FCompressedChunk> list = new ArrayList<>();
		for (int i = 0; i < length; i++) {
			list.add(new FCompressedChunk(this));
		}

		return list;
	}

	// Array of String
	public List<String> readTArrayOfString() throws ReadException {

		int length = this.readInt32();

		List<String> list = new ArrayList<>();
		for (int i = 0; i < length; i++) {
			list.add(this.readString());
		}

		return list;
	}

	// Array of Int32
	public List<Integer> readTArrayOfInt32() throws ReadException {

		int length = this.readInt32();

		List<Integer> list = new ArrayList<>();
		for (int i = 0; i < length; i++) {
			list.add(this.readInt32());
		}

		return list;
	}
	// Array of UInt32
		public List<Long> readTArrayOfUInt32() throws ReadException {

			int length = this.readInt32();

			List<Long> list = new ArrayList<>();
			for (int i = 0; i < length; i++) {
				list.add(this.readUInt32());
			}

			return list;
		}
	// Array of UInt16
	public List<Integer> readTArrayOfUInt16() throws ReadException {

		int length = this.readInt32();

		List<Integer> list = new ArrayList<>();
		for (int i = 0; i < length; i++) {
			list.add(this.readUInt16());
		}

		return list;
	}
	// Array of Int16
	public List<Short> readTArrayOfInt16() throws ReadException {

		int length = this.readInt32();

		List<Short> list = new ArrayList<>();
		for (int i = 0; i < length; i++) {
			list.add(this.readInt16());
		}

		return list;
	}
	// Array of Float32
	public List<Float> readTArrayOfFloat32() throws ReadException {

		int length = this.readInt32();

		List<Float> list = new ArrayList<>();
		for (int i = 0; i < length; i++) {
			list.add(this.readFloat32());
		}

		return list;
	}

	// converts uint16 to float16
	private static float toFloat(int hbits) {
		int mant = hbits & 0x03ff; // 10 bits mantissa
		int exp = hbits & 0x7c00; // 5 bits exponent
		if (exp == 0x7c00) // NaN/Inf
			exp = 0x3fc00; // -> NaN/Inf
		else if (exp != 0) // normalized value
		{
			exp += 0x1c000; // exp - 15 + 127
			if (mant == 0 && exp > 0x1c400) // smooth transition
				return Float.intBitsToFloat((hbits & 0x8000) << 16 | exp << 13 | 0x3ff);
		} else if (mant != 0) // && exp==0 -> subnormal
		{
			exp = 0x1c400; // make it normal
			do {
				mant <<= 1; // mantissa * 2
				exp -= 0x400; // decrease exp by 1
			} while ((mant & 0x400) == 0); // while not normal
			mant &= 0x3ff; // discard subnormal bit
		} // else +/-0 -> +/-0
		return Float.intBitsToFloat( // combine all parts
				(hbits & 0x8000) << 16 // sign << ( 31 - 15 )
						| (exp | mant) << 13); // value << ( 23 - 10 )
	}

	public byte[] serialize(int size) throws ReadException {
		byte[] res = new byte[size];
		if ((this.ArPos + size) <= this.ArStopper) {
			res = Arrays.copyOfRange(this.data, this.ArPos, this.ArPos + size);
			this.ArPos += size;
			return res;
		} else {
			throw new ReadException(
					String.format("Serializing behind stopper (%d + %d > %d", this.ArPos, size, this.ArStopper));
		}
	}
}
