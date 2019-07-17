/**
 * 
 */
package UE4_Assets;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Future;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import UE4.FArchive;
import UE4.PackageLogger;
import UE4_Localization.Locres;
import UE4_PakFile.GameFile;
import UE4_PakFile.PakFileReader;

/**
 * @author FunGames
 *
 */
public class Package {

	public static final int uassetMagic = 0x9E2A83C1;

	private String name;
	private FPackageFileSummary info;
	private NameMap nameMap;
	private ImportMap importMap;
	private String packagePath;

	public String getPackagePath() {
		if (packagePath != null) {
			return packagePath;
		} else {
			return name;
		}

	}

	public void setPackagePath(String packagePath) {
		this.packagePath = packagePath;
	}

	public FPackageFileSummary getInfo() {
		return info;
	}

	public NameMap getNameMap() {
		return nameMap;
	}

	public ImportMap getImportMap() {
		return importMap;
	}

	public ExportMap getExportMap() {
		return exportMap;
	}

	public List<Object> getExports() {
		return exports;
	}

	private ExportMap exportMap;
	private List<Object> exports;
	private List<UObject> uObjects;

	private byte[] uasset;
	private FArchive uassetAr;

	private byte[] uexp;
	private FArchive uexpAr;

	private byte[] ubulk;

	public static Package fromFiles(File uassetFile, File uexpFile, File ubulkFile) throws ReadException, IOException {
		if (uassetFile.exists()) {
			String name = uassetFile.getName().substring(0, uassetFile.getName().length() - 7);
			FileInputStream uassetIn = new FileInputStream(uassetFile);
			byte[] uasset = new byte[uassetIn.available()];
			uassetIn.read(uasset);
			FileInputStream uexpIn = new FileInputStream(uexpFile);
			byte[] uexp = new byte[uexpIn.available()];
			uexpIn.read(uexp);
			byte[] ubulk = null;
			if (ubulkFile != null) {
				FileInputStream ubulkIn = new FileInputStream(ubulkFile);
				ubulk = new byte[ubulkIn.available()];
				ubulkIn.read(ubulk);
			}
			return new Package(name, uasset, uexp, ubulk);
		} else {
			throw new ReadException("Uasset File: doesn't exist");
		}
	}

	public Package(String name, byte[] uasset, byte[] uexp, byte[] ubulk, Optional<Locres> locres)
			throws ReadException {
		this.uasset = uasset;
		this.uexp = uexp;
		this.ubulk = ubulk;
		this.name = name;

		// Load uasset
		this.uassetAr = new FArchive();
		this.uassetAr.data = uasset;

		this.uassetAr.SetStopper(uasset.length);
		this.uassetAr.Seek(0);

		this.info = new FPackageFileSummary(this.uassetAr);

		if (this.info.tag == this.uassetMagic) {
			// Read NameMap
			this.uassetAr.Seek(this.info.nameOffset);
			this.nameMap = new NameMap(this.uassetAr, this.info.nameCount);

			// Read ImportMap
			this.uassetAr.Seek(this.info.importOffset);
			this.importMap = new ImportMap(uassetAr, this.info.importCount, nameMap);

			// Read ExportMap
			this.uassetAr.Seek(this.info.exportOffset);
			this.exportMap = new ExportMap(this.uassetAr, this.info.exportCount, nameMap, importMap);

			int assetLength = info.totalHeaderSize;
			exports = new ArrayList<>();
			uObjects = new ArrayList<>();
			int index = 0;

			int exportSize = 0;
			for (FObjectExport export : exportMap.getEntrys()) {
				exportSize += export.getSerialSize();
			}

			this.uexpAr = new FArchive();
			this.uexpAr.data = uexp;
			this.uexpAr.SetStopper(exportSize);
			this.uexpAr.Seek(0);
			this.uexpAr.uassetSize = assetLength;
			this.uexpAr.uexpSize = exportSize;
			this.uexpAr.locres = locres;

			if (ubulk != null) {
				FArchive ubulkAr = new FArchive();
				ubulkAr.data = ubulk;
				ubulkAr.SetStopper(ubulk.length);
				ubulkAr.Seek(0);
				uexpAr.addPayload("UBULK", ubulkAr);
			}

			AthenaItemDefinition previousItemDef = null;

			for (FObjectExport export : exportMap.getEntrys()) {
				String exportType = export.getClassIndex().getImportName();
				if (exportType.equals("0")) {
					exports.add(null);
					break;
				}
				int position = (int) (export.getSerialOffset() - uexpAr.getUexpOffset());
				this.uexpAr.Seek(position);
				switch (exportType) {
				case "Texture2D":
					UTexture2D t = new UTexture2D(this.uexpAr, nameMap, importMap);
					exports.add(t);
					uObjects.add(t.getBaseObject());
					break;
				case "SoundWave":
					USoundWave s = new USoundWave(this.uexpAr, nameMap, importMap);
					exports.add(s);
					uObjects.add(s.getBaseObject());
					break;
				case "FortMtxOfferData":
					DisplayAssetPath p = new DisplayAssetPath(this.uexpAr, nameMap, importMap);
					exports.add(p);
					uObjects.add(p.getBaseObject());
					break;
				case "FortHeroType":
					FortHeroType ht = new FortHeroType(this.uexpAr, nameMap, importMap);
					exports.add(ht);
					uObjects.add(ht.getBaseObject());
					break;
				case "DataTable":
					UDataTable d = new UDataTable(this.uexpAr, nameMap, importMap);
					exports.add(d);
					uObjects.add(d.getBaseObject());
					break;
				case "FortWeaponMeleeItemDefinition":
					FortWeaponMeleeItemDefinition fD = new FortWeaponMeleeItemDefinition(this.uexpAr, nameMap,
							importMap);
					exports.add(fD);
					uObjects.add(fD.getBaseObject());
					break;
				case "SkeletalMesh":
					USkeletalMesh4 sm = new USkeletalMesh4(this.uexpAr, nameMap, importMap);
					exports.add(sm);
					uObjects.add(sm.getSuperObject());
					break;
				case "CatalogMessaging":
					FNCatalogMessaging cm = new FNCatalogMessaging(this.uexpAr, nameMap, importMap);
					exports.add(cm);
					uObjects.add(cm.getBaseObject());
					break;
				default:
					if (exportType.startsWith("Athena") && exportType.endsWith("Definition")) {
						// Cannot check that in the switch
						AthenaItemDefinition item = new AthenaItemDefinition(this.uexpAr, nameMap, importMap,
								exportType);
						exports.add(item);
						uObjects.add(item.getBaseObject());
						previousItemDef = item;
					} else if (exportType.startsWith("FortCosmetic") && exportType.endsWith("Variant")) {
						// Cannot check that in the switch
						FortCosmeticVariant charVariant = new FortCosmeticVariant(this.uexpAr, nameMap, importMap,
								exportType);
						if (previousItemDef != null)
							previousItemDef.addVariants(charVariant);
						exports.add(charVariant);
						uObjects.add(charVariant.getBaseObject());
					} else {

						UObject baseObject = new UObject(this.uexpAr, nameMap, importMap, exportType);
						int validPos = (int) (position + export.getSerialSize());
						if (validPos != export.getSerialSize()) {
							// System.out.println(String.format(
							// "WARNING: Unknown export has unread data beside the uobject, Read bytes: %d
							// Export Length: %d",
							// this.uexpAr.Tell() - position, export.getSerialSize()));
						} else {
							// System.out.println("INFO: Unknown Export has no more data beside the
							// uobject");
						}
						// System.err.println("Skipped unknown export '" + exportType + "' at " +
						// export.getSerialOffset()
						// + " with length " + export.getSerialSize());
						index += export.getSerialSize();
						exports.add(baseObject);
						uObjects.add(baseObject);
					}
				}
				int validPos = (int) (position + export.getSerialSize());
				if (this.uexpAr.Tell() != validPos) {
					// System.out.println(
					// String.format("Did not read '%s' correctly. Current Position: %d, Bytes
					// Remaining: %d",
					// exportType, this.uexpAr.Tell(), validPos - this.uexpAr.Tell()));
				} else {
					// System.out.println(String.format("Successfully read export '%s' at offset %d
					// with length %d",
					// exportType, export.getSerialOffset() - assetLength, export.getSerialSize()));
				}
			}
			PackageLogger.log.println("Successfully parsed package: " + name);

		} else {
			throw new ReadException("Given uasset file is not an uasset. Wrong package tag", 0);
		}
	}

	public <T> T getExportAs(Class<T> exportClass) throws ReadException {
		try {
			Optional<Object> ob = exports.stream().filter(o -> {
				return o.getClass().equals(exportClass);
			}).findFirst();
			T res = (T) ob.get();
			return res;
		} catch (Exception e) {
			throw new ReadException("Package does not have any export with class " + exportClass.getSimpleName());
		}

	}

	public Package(String name, byte[] uasset, byte[] uexp, byte[] ubulk) throws ReadException {
		this(name, uasset, uexp, ubulk, Optional.empty());

	}

	public byte[] getUasset() {
		return uasset;
	}

	public byte[] getUexp() {
		return uexp;
	}

	public byte[] getUbulk() {
		return ubulk;
	}

	public String getName() {
		return name;
	}

	public List<UObject> getuObjects() {
		return uObjects;
	}

	@SuppressWarnings("unchecked")
	public String toJSON() {
		// Create root object
		JSONObject root = new JSONObject();

		// Add NameMap as JSONArray
		JSONArray nameMapData = new JSONArray();
		for (FNameEntry nameMapEntry : nameMap.getEntrys()) {
			nameMapData.add(nameMapEntry.getName());
		}
		root.put("name_map", nameMapData);

		// Add ImportMap as JSONArray
		JSONArray importMapData = new JSONArray();
		for (FObjectImport oImport : importMap.getEntrys()) {
			JSONObject importO = new JSONObject();
			importO.put("class_name", oImport.getClassName());
			importO.put("class_package", oImport.getClassPackage());
			importO.put("object_name", oImport.getObjectName());
			importMapData.add(importO);
		}
		root.put("import_map", importMapData);

		// Add ExportMap as JSONArray
		int assetLength = info.totalHeaderSize;
		JSONArray exportMapData = new JSONArray();
		for (FObjectExport oExport : exportMap.getEntrys()) {
			JSONObject exportO = new JSONObject();
			exportO.put("export_type", oExport.getClassIndex().getImportName());
			exportO.put("export_offset", oExport.getSerialOffset() - assetLength);
			exportO.put("export_length", oExport.getSerialSize());
			exportMapData.add(exportO);
		}
		root.put("export_map", exportMapData);

		// Add all UObjects data
		JSONArray exportProperties = new JSONArray();
		for (UObject uobject : uObjects) {
			JSONObject uobjectO = new JSONObject();
			uobjectO.put("export_type", uobject.getExportType());
			JSONArray propertyTags = new JSONArray();
			for (FPropertyTag propertyTag : uobject.getProperties()) {
				propertyTags.add(propertyTag.jsonify());
			}
			uobjectO.put("property_tags", propertyTags);
			exportProperties.add(uobjectO);
		}
		root.put("export_properties", exportProperties);

		return root.toJSONString();

	}

	public static Package loadPackageByName(String gameFileName, Map<String, Integer> pakFileReaderIndices,
			PakFileReader[] loadedPaks, Map<String, GameFile> gameFiles) throws ReadException {
		GameFile gameFile = gameFiles.get(gameFileName);
		if (gameFile != null) {
			if (gameFile.isUE4Package()) {
				PakFileReader pFR = loadedPaks[pakFileReaderIndices.get(gameFile.getPakFilePath())];
				if (pFR != null) {
					try {
						Future<byte[]> uasset = pFR.extractSelectedToBufferAsync(gameFile);
						Future<byte[]> uexp = null;
						if (gameFile.hasUexp()) {
							uexp = pFR.extractSelectedToBufferAsync(gameFile.getUexp());
						}
						Future<byte[]> ubulk = null;
						if (gameFile.hasUbulk()) {
							ubulk = pFR.extractSelectedToBufferAsync(gameFile.getUbulk());
						}

						byte[] uassetA = uasset.get();
						byte[] uexpA = null;
						if (gameFile.hasUexp()) {
							uexpA = uexp.get();
						}
						byte[] ubulkA = null;
						if (gameFile.hasUbulk()) {
							ubulkA = ubulk.get();
						}

						Package uePackage = new Package(gameFile.getNameWithoutExtension(), uassetA, uexpA, ubulkA);
						return uePackage;
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						System.err.println("Failed to load Package: " + gameFile.getNameWithoutExtension());
						return null;
					}
				} else {
					// Unable to find PakFileReader
					return null;
				}
			} else {
				// Is no UE Package
				return null;
			}

		} else {
			return null;
		}
	}

	public static String toGameSpecificName(String name, String mountPrefix) {
		String res = name;
		if (res != null) {
			if (res.startsWith("/")) {
				res = res.substring(1);
			}
			if (res.startsWith("Game/")) {
				if (!mountPrefix.contains("Content/")) {
					res = res.replaceFirst("Game", mountPrefix + "Content");
				} else {
					res = res.replaceFirst("Game/", mountPrefix);
				}

			}
			if (!res.endsWith(".uasset")) {
				if (res.contains(".")) {
					String[] parts = res.split("\\.");
					if (parts.length == 2) {
						res = parts[0];
					}
				}
				res += ".uasset";
			}
			return res;
		} else {
			return null;
		}
	}

	public Object getExport(FObjectExport export) {
		int exportIndex = -1;
		for (int i = 0; i < exportMap.getEntrys().size(); i++) {
			if (export.equals(exportMap.get(i))) {
				exportIndex = i;
				break;
			}
		}
		if (exportIndex >= 0) {
			Object d = exports.get(exportIndex);
			return d;
		} else {
			return null;
		}
	}
}
