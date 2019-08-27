/**
 * 
 */
package UE4_Assets;

import java.util.List;

import annotation.Int32;
import annotation.Serializable;
import annotation.UInt32;
import lombok.Data;

/**
 * @author FunGames
 *
 */
@Serializable
@Data
public class FPackageFileSummary {
	@Int32 private Integer tag;
	@Int32 private int legacyFileVersion;
	@Int32 private int legacyUE3Version;
	@Int32 private int fileVersionUE4;
	@Int32 private int fileVersionLicenseUE4;
	private List<FCustomVersion> customVersionContainer;
	@Int32 private int totalHeaderSize;
	private String folderName;
	@UInt32 private long packageFlags;
	@Int32 private int nameCount;
	@Int32 private int nameOffset;
	@Int32 private int gatherableTextDataCount;
	@Int32 private int gatherableTextDataOffset;
	@Int32 private int exportCount;
	@Int32 private int exportOffset;
	@Int32 private int importCount;
	@Int32 private int importOffset;
	@Int32 private int dependsOffset;
	@Int32 private int stringAssetReferencesCount;
	@Int32 private int stringAssetReferencesOffset;
	@Int32 private int searchableNamesOffset;
	@Int32 private int thumbnailTableOffset;
	private FGUID guid;
	private List<FGenerationInfo> generations;
	private FEngineVersion savedByEngineVersion;
	private FEngineVersion compatibleWithEngineVersion;
	@UInt32 private long compressionFlags;
	private List<FCompressedChunk> compressedChunks;
	@UInt32 private long packageSource;
	private List<String> additionalPackagesToCook;
	@Int32 private int assetRegistryDataOffset;
	@Int32 private int bulkDataStartOffset;
	@Int32 private int worldTileInfoDataOffset;
	@Int32 private List<Integer> chunk_ids;	
	@Int32 private int preloadDependencyCount;
	@Int32 private int preloadDependencyOffset;

}
