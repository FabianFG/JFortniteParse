/**
 * 
 */
package UE4_Assets;

import java.util.List;

import UE4.FArchive;

/**
 * @author FunGames
 *
 */
public class FPackageFileSummary {
	public int tag;
	public int legacyFileVersion;
	public int legacyUE3Version;
	public int fileVersionUE4;
	public int fileVersionLicenseUE4;
	public List<FCustomVersion> customVersionContainer;
	public int totalHeaderSize;
	public String folderName;
	public long packageFlags;
	public int nameCount;
	public int nameOffset;
	public int gatherableTextDataCount;
	public int gatherableTextDataOffset;
	public int exportCount;
	public int exportOffset;
	public int importCount;
	public int importOffset;
	public int dependsOffset;
	public int stringAssetReferencesCount;
	public int stringAssetReferencesOffset;
	public int searchableNamesOffset;
	public int thumbnailTableOffset;
	public FGUID guid;
	public List<FGenerationInfo> generations;
	public FEngineVersion savedByEngineVersion;
	public FEngineVersion compatibleWithEngineVersion;
	public long compressionFlags;
	public List<FCompressedChunk> compressedChunks;
	public long packageSource;
	public List<String> additionalPackagesToCook;
	public int assetRegistryDataOffset;
	public int bulkDataStartOffset;
	public int worldTileInfoDataOffset;
	public List<Integer> chunk_ids;	
	public int preloadDependencyCount;
	public int preloadDependencyOffset;
	
	public FPackageFileSummary(FArchive Ar) throws ReadException {
		tag = Ar.readInt32();		
		legacyFileVersion = Ar.readInt32();		
		legacyUE3Version = Ar.readInt32();		
		fileVersionUE4 = Ar.readInt32();		
		fileVersionLicenseUE4 = Ar.readInt32();		
		customVersionContainer = Ar.readTArrayOfFCustomVersion();		
		totalHeaderSize = Ar.readInt32();		
		folderName = Ar.readString();		
		packageFlags = Ar.readUInt32();		
		nameCount = Ar.readInt32();		
		nameOffset = Ar.readInt32();		
		gatherableTextDataCount = Ar.readInt32();
		gatherableTextDataOffset = Ar.readInt32();
		exportCount = Ar.readInt32();
		exportOffset = Ar.readInt32();
		importCount = Ar.readInt32();
		importOffset = Ar.readInt32();
		dependsOffset = Ar.readInt32();
		stringAssetReferencesCount = Ar.readInt32();
		stringAssetReferencesOffset = Ar.readInt32();
		searchableNamesOffset = Ar.readInt32();
		thumbnailTableOffset = Ar.readInt32();
		guid = new FGUID(Ar);
		generations = Ar.readTArrayOfFGenerationInfo();
		savedByEngineVersion = new FEngineVersion(Ar);
		compatibleWithEngineVersion = new FEngineVersion(Ar);
		compressionFlags = Ar.readUInt32();
		compressedChunks = Ar.readTArrayOfFCompressedChunk();
		packageSource = Ar.readUInt32();
		additionalPackagesToCook = Ar.readTArrayOfString();
		assetRegistryDataOffset = Ar.readInt32();
		bulkDataStartOffset = Ar.readInt32();
		worldTileInfoDataOffset = Ar.readInt32();
		chunk_ids = Ar.readTArrayOfInt32();
		preloadDependencyCount = Ar.readInt32();
		preloadDependencyOffset = Ar.readInt32();	
	}
}
