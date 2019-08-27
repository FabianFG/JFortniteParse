/**
 * 
 */
package UE4_PakFile;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import UE4.deserialize.exception.DeserializationException;
import UE4_Assets.FGUID;
import UE4_Assets.FPakArchive;
import UE4_Assets.ReadException;

/**
 * @author FunGames
 *
 */
public class FPakInfo {

	public long offsetInFile;
	// When new fields are added to FPakInfo, they're serialized before 'Magic' to
	// keep compatibility
	// with older pak file versions. At the same time, structure size grows.
	public FGUID encryptionKeyGuid;
	public boolean bEncryptedIndex;
	public int magic;
	public int version;
	public long indexOffset;
	public long indexSize;
	public byte[] indexHash;

	// Pak v.8 fields
	public int maxNumCompressionMethods;
	public List<FPakCompressionMethod> compressionMethods;

	public static int size() {
		return 4 * 2 + 8 * 2 + 20 + /* new fields */ 1 + 16;
	}

	@SuppressWarnings("unused")
	public static void main(String[] args) throws IOException, ReadException {
		RandomAccessFile f = new RandomAccessFile("D:\\Fabian\\Desktop\\Mobile v8\\pakchunk2-Android_ASTCClient.pak",
				"r");
		FPakInfo headerOffset = readPakInfo(new FPakArchive(f));
		System.out.println();
	}

	public static FPakInfo readPakInfo(FPakArchive Ar) throws ReadException {
		long offset = Ar.GetStopper64() - size();
		long terminator = Ar.GetStopper64() - size() - 300; // Dont run into endless loop if the file is no pak file
		int maxNumCompressionMethods = 0;
		FPakInfo testInfo;
		do {
			Ar.Seek64(offset);
			testInfo = new FPakInfo(Ar, maxNumCompressionMethods);
			offset -= 32; // One compression method is 32 bytes long
			maxNumCompressionMethods++;
		} while (testInfo.magic != PAK.PAK_FILE_MAGIC && offset > terminator);
		if (testInfo.magic == PAK.PAK_FILE_MAGIC) {
			return testInfo;
		} else {
			return null;
		}

	}

	public FPakInfo(FPakArchive Ar, int maxNumCompressionMethods) throws ReadException {
		try {
			offsetInFile = Ar.Tell64();

			// New FPakInfo fields
			encryptionKeyGuid = Ar.read(FGUID.class);
			bEncryptedIndex = Ar.readBooleanFromUInt8();

			// Old FPakInfo fields
			magic = Ar.readInt32();
			version = Ar.readInt32();
			indexOffset = Ar.readInt64();
			indexSize = Ar.readInt64();

			indexHash = Ar.serialize(20);

			// Reset new fields to their default states when seralizing older pak format.
			if (this.version < PAK.PakVersion.PAK_INDEX_ENCRYPTION.v) {
				bEncryptedIndex = false;
			}
			if (this.version < PAK.PakVersion.PAK_ENCRYPTION_KEY_GUID.v) {
				encryptionKeyGuid = new FGUID("0-0-0-0");
			}

			// Read compression methods names if pak 8
			if (this.version >= PAK.PakVersion.PAK_FNAME_BASED_COMPRESSION_METHOD.v) {
				this.maxNumCompressionMethods = maxNumCompressionMethods;
				this.compressionMethods = new ArrayList<>();
				// UE4: we always put in a NAME_None entry as index 0, so that an uncompressed
				// PakEntry will have CompressionMethodIndex of 0 and can early out easily
				compressionMethods.add(new FPakCompressionMethod("None"));
				for (int i = 1; i <= maxNumCompressionMethods; i++) {
					FPakCompressionMethod b = Ar.read(FPakCompressionMethod.class);
					if (b.getCompressionName().equals("")) {
						// Empty Compression Method name
						break;
					}
					compressionMethods.add(b);
				}

			}
		} catch (DeserializationException e) {
			throw new ReadException("Failed to read FPakInfo", e);
		}
	}
}
