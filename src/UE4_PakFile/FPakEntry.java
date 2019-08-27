/**
 * 
 */
package UE4_PakFile;

import java.util.ArrayList;
import java.util.List;

import UE4.FArchive;
import UE4.deserialize.exception.DeserializationException;
import UE4_Assets.ReadException;
import annotation.CustomSerializable;
import lombok.Data;

/**
 * @author FunGames
 *
 */
@Data
@CustomSerializable
public class FPakEntry {
	public String name;
	public long pos;
	public long size;
	public long uncompressedSize;
	public int compressionMethod;
	public byte[] hash;
	public boolean bEncrypted; // replaced with 'Flags' in UE4.21
	public List<FPakCompressedBlock> compressionBlocks = new ArrayList<>();
	public int compressionBlockSize;

	public int binaryLength; // computed value
	private long timestamp;

	public FPakEntry(FArchive Ar, FPakInfo info, boolean inIndex) throws ReadException {
		try {
			if (inIndex) {
				name = Ar.readString();
			}
			pos = Ar.readInt64();
			size = Ar.readInt64();
			uncompressedSize = Ar.readInt64();
			compressionMethod = Ar.readInt32();
			if (info.version < PAK.PakVersion.PAK_NO_TIMESTAMPS.v) {
				// Value not really needed
				timestamp = Ar.readInt64();
			}
			hash = Ar.serialize(20);

			if (info.version >= PAK.PakVersion.PAK_COMPRESSION_ENCRYPTION.v) {
				if (compressionMethod != 0) {
					compressionBlocks = new ArrayList<>();
					int numCompressionBlocks = Ar.readInt32();
					for (int i = 0; i < numCompressionBlocks; i++) {
						compressionBlocks.add(Ar.read(FPakCompressedBlock.class));
					}
				}
				bEncrypted = Ar.readBooleanFromUInt8();
				compressionBlockSize = Ar.readInt32();
			}

			if (info.version >= PAK.PakVersion.PAK_RELATIVE_CHUNK_OFFSETS.v) {
				// Convert relative compressed offsets to absolute
				for (int i = 0; i < compressionBlocks.size(); i++) {
					FPakCompressedBlock b = compressionBlocks.get(i);
					b.setCompressedStart(b.getCompressedStart() + pos);
					b.setCompressedEnd(b.getCompressedEnd() + pos);
					compressionBlocks.set(i, b);
				}
			}
		} catch (DeserializationException e) {
			throw new ReadException("Failed to read PakEntry", e);
		}
	}
}
