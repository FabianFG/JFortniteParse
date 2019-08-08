package UE4_PakFile;

import java.util.List;

public class GameFile {

	// private FPakEntry indexEntry;
	private int indexCount;
	private long offset;
	private int length;
	private int uncompressedlength;
	private String name;
	private byte[] checksum;
	private boolean encrypted;
	private long encryptionBooleanOffset;
	private long indexOffset;
	// private UE4Package ue4Package;
	private String pakFilePath;
	private boolean compressed;
	private List<FPakCompressedBlock> compressionBlocks;
	private int compressionBlockSize;
	private int compressionMethod;

	//private Map<String, GameFile> payloads;
	private GameFile uexp;
	private GameFile ubulk;

	private boolean isUE4Package;

	// public UE4Package getUe4Package() {
	// return ue4Package;
	// }

	// public void setUe4Package(UE4Package ue4Package) {
	// this.ue4Package = ue4Package;
	// }
	
	public String getNameWithoutExtension() {
		if(this.isUE4Package) {
			return name.substring(0, name.length() - ".uasset".length());
		} else {
			return name;
		}
	}

	public void addUexp(GameFile uexp) {
		this.uexp = uexp;
	}

	public void addUbulk(GameFile ubulk) {
		this.ubulk = ubulk;
	}

	public GameFile getUexp() {
		return this.uexp;
	}

	public GameFile getUbulk() {
		return this.ubulk;
	}

	public boolean isUE4Package() {
		return this.isUE4Package;
	}
	
	public int getTotalLength() {
		int totalLength = this.length;
		if(this.hasUexp()) {
			totalLength += this.getUexp().length;
		}
		if(this.hasUbulk()) {
			totalLength += this.getUbulk().length;
		}
		return totalLength;
	}

	public boolean hasUexp() {
		return uexp != null ? true : false;
	}
	
	public boolean hasUbulk() {
		return ubulk != null ? true : false;
	}

	public long getIndexOffset() {
		return indexOffset;
	}

	public void setIndexOffset(long indexOffset) {
		this.indexOffset = indexOffset;
	}

	// public FPakEntry getIndexEntry() {
	// return indexEntry;
	// }

	// public void setIndexEntry(FPakEntry indexEntry) {
	// this.indexEntry = indexEntry;
	// }

	public int getUncompressedlength() {
		return uncompressedlength;
	}

	public void setUncompressedlength(int uncompressedlength) {
		this.uncompressedlength = uncompressedlength;
	}

	public boolean isCompressed() {
		return compressed;
	}

	public void setCompressed(boolean compressed) {
		this.compressed = compressed;
	}

	public List<FPakCompressedBlock> getCompressionBlocks() {
		return compressionBlocks;
	}

	public void setCompressionBlocks(List<FPakCompressedBlock> compressionBlocks) {
		this.compressionBlocks = compressionBlocks;
	}

	public int getCompressionBlockSize() {
		return compressionBlockSize;
	}

	public void setCompressionBlockSize(int compressionBlockSize) {
		this.compressionBlockSize = compressionBlockSize;
	}

	public void setChecksum(byte[] checksum) {
		this.checksum = checksum;
	}

	public void setPakFilePath(String pakFilePath) {
		this.pakFilePath = pakFilePath;
	}

	public int getIndexCount() {
		return indexCount;
	}

	public void setIndexCount(int indexCount) {
		this.indexCount = indexCount;
	}

	public long getEncryptionBooleanOffset() {
		return encryptionBooleanOffset;
	}

	public void setEncryptionBooleanOffset(long encryptionBooleanOffset) {
		this.encryptionBooleanOffset = encryptionBooleanOffset;
	}

	public boolean isEncrypted() {
		return encrypted;
	}

	public void setEncrypted(boolean encrypted) {
		this.encrypted = encrypted;
	}

	public GameFile(FPakEntry indexEntry, String mountPrefix, int indexCount, String pakFilePath) {
		// this.indexEntry = indexEntry;
		this.offset = indexEntry.pos;
		this.length = (int) indexEntry.size;
		this.uncompressedlength = (int) indexEntry.uncompressedSize;
		this.encrypted = indexEntry.bEncrypted;
		this.compressionBlockSize = indexEntry.compressionBlockSize;
		this.compressionBlocks = indexEntry.compressionBlocks;
		this.compressed = indexEntry.compressionMethod == 0 ? false : true;
		this.compressionMethod = indexEntry.compressionMethod;
		this.name = mountPrefix + indexEntry.name;
		this.checksum = indexEntry.hash;
		this.indexCount = indexCount;
		this.pakFilePath = pakFilePath;

		this.isUE4Package = this.name.endsWith(".uasset") ? true : false;
	}

	public byte[] getChecksum() {
		return checksum;
	}

	public String getActualName() {
		return name.split("/")[name.split("/").length - 1];
	}
	
	public String getActualNameWithoutExtension() {
		String name = getActualName();
		return name.split("\\.")[0];
	}

	public String getPakFilePath() {
		return pakFilePath;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getOffset() {
		return offset;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getCompressionMethod() {
		return compressionMethod;
	}

	public void setCompressionMethod(int compressionMethod) {
		this.compressionMethod = compressionMethod;
	}
	
	@Override
	public String toString() {
		return "\"" + this.name + "\"";
	}

}
