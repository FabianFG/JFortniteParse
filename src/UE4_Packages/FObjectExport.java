/**
 * 
 */
package UE4_Packages;

/**
 * @author FunGames
 *
 */
public class FObjectExport {
	private FPackageIndex classIndex;
	private FPackageIndex superIndex;
	private FPackageIndex templateIndex;
	private FPackageIndex outerIndex;
	private String objectName;
	private long save;
	private long serialSize;
	private long serialOffset;
	private boolean forcedExport;
	private boolean notForClient;
	private boolean notForServer;
	private FGUID packageGUID;
	private long packageFlags;
	private boolean notAlwaysLoadedForEditorGame;
	private boolean isAsset;
	private int firstExportDependency;
	private boolean serializationBeforeSerializationDependencies;
	private boolean createBeforeSerializationDependencies;
	private boolean serializationBeforeCreateDependencies;
	private boolean createBeforeCreateDependencies;

	public FPackageIndex getClassIndex() {
		return classIndex;
	}

	public FPackageIndex getSuperIndex() {
		return superIndex;
	}

	public FPackageIndex getTemplateIndex() {
		return templateIndex;
	}

	public FPackageIndex getOuterIndex() {
		return outerIndex;
	}

	public String getObjectName() {
		return objectName;
	}

	public long getSave() {
		return save;
	}

	public long getSerialSize() {
		return serialSize;
	}

	public long getSerialOffset() {
		return serialOffset;
	}

	public boolean getForcedExport() {
		return forcedExport;
	}

	public boolean getNotForClient() {
		return notForClient;
	}

	public boolean getNotForServer() {
		return notForServer;
	}

	public FGUID getPackageGUID() {
		return packageGUID;
	}

	public long getPackageFlags() {
		return packageFlags;
	}

	public boolean getNotAlwaysLoadedForEditorGame() {
		return notAlwaysLoadedForEditorGame;
	}

	public boolean getIsAsset() {
		return isAsset;
	}

	public int getFirstExportDependency() {
		return firstExportDependency;
	}

	public boolean getSerializationBeforeSerializationDependencies() {
		return serializationBeforeSerializationDependencies;
	}

	public boolean getCreateBeforeSerializationDependencies() {
		return createBeforeSerializationDependencies;
	}

	public boolean getSerializationBeforeCreateDependencies() {
		return serializationBeforeCreateDependencies;
	}

	public boolean getCreateBeforeCreateDependencies() {
		return createBeforeCreateDependencies;
	}

	
	public FObjectExport(FArchive Ar, ImportMap importMap, NameMap nameMap) throws ReadException {
		classIndex = new FPackageIndex(Ar, importMap);
		superIndex = new FPackageIndex(Ar, importMap);
		templateIndex = new FPackageIndex(Ar, importMap);
		outerIndex = new FPackageIndex(Ar, importMap);
		objectName = Ar.readFName(nameMap);
		save = Ar.readUInt32();
		serialSize = Ar.readInt64();
		serialOffset = Ar.readInt64();
		forcedExport = Ar.readBoolean();
		notForClient = Ar.readBoolean();
		notForServer = Ar.readBoolean();
		packageGUID = new FGUID(Ar);
		packageFlags = Ar.readUInt32();
		notAlwaysLoadedForEditorGame = Ar.readBoolean();
		isAsset = Ar.readBoolean();
		firstExportDependency = Ar.readInt32();
		serializationBeforeSerializationDependencies = Ar.readBoolean();
		createBeforeSerializationDependencies = Ar.readBoolean();
		serializationBeforeCreateDependencies = Ar.readBoolean();
		createBeforeCreateDependencies = Ar.readBoolean();
	}
}
