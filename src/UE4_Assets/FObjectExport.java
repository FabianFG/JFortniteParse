/**
 * 
 */
package UE4_Assets;

import annotation.BooleanZ;
import annotation.FName;
import annotation.Int32;
import annotation.Int64;
import annotation.Serializable;
import annotation.UInt32;
import lombok.Data;

/**
 * @author FunGames
 *
 */
@Data
@Serializable
public class FObjectExport {
	private FPackageIndex classIndex;
	private FPackageIndex superIndex;
	private FPackageIndex templateIndex;
	private FPackageIndex outerIndex;
	@FName private String objectName;
	@UInt32 private long save;
	@Int64 private long serialSize;
	@Int64 private long serialOffset;
	@BooleanZ private boolean forcedExport;
	@BooleanZ private boolean notForClient;
	@BooleanZ private boolean notForServer;
	private FGUID packageGUID;
	@UInt32 private long packageFlags;
	@BooleanZ private boolean notAlwaysLoadedForEditorGame;
	@BooleanZ private boolean isAsset;
	@Int32 private int firstExportDependency;
	@BooleanZ private boolean serializationBeforeSerializationDependencies;
	@BooleanZ private boolean createBeforeSerializationDependencies;
	@BooleanZ private boolean serializationBeforeCreateDependencies;
	@BooleanZ private boolean createBeforeCreateDependencies;

}
