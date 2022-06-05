package me.fungames.jfortniteparse.ue4.registry.objects

import me.fungames.jfortniteparse.LOG_JFP
import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid
import me.fungames.jfortniteparse.ue4.reader.FArchive

class FAssetRegistryVersion(Ar: FArchive) : Comparable<FAssetRegistryVersion.Type> {
    companion object {
        val versionGuid = FGuid(0x717F9EE7u, 0xE9B0493Au, 0x88B39132u, 0x1B388107u)
    }

    enum class Type {
        /** From before file versioning was implemented */
        PreVersioning,
        /** The first version of the runtime asset registry to include file versioning. */
        HardSoftDependencies,
        /** Added FAssetRegistryState and support for piecemeal serialization */
        AddAssetRegistryState,
        /** AssetData serialization format changed, versions before this are not readable */
        ChangedAssetData,
        /** Removed MD5 hash from package data */
        RemovedMD5Hash,
        /** Added hard/soft manage references */
        AddedHardManage,
        /** Added MD5 hash of cooked package to package data */
        AddedCookedMD5Hash,
        /** Added UE::AssetRegistry::EDependencyProperty to each dependency */
        AddedDependencyFlags,
        /**
         * Major tag format change that replaces USE_COMPACT_ASSET_REGISTRY:
         * * Target tag INI settings cooked into tag data
         * * Instead of FString values are stored directly as one of:
         *      - Narrow / wide string
         *      - [Numberless] FName
         *      - [Numberless] export path
         *      - Localized string
         * * All value types are deduplicated
         * * All key-value maps are cooked into a single contiguous range
         * * Switched from FName table to seek-free and more optimized FName batch loading
         * * Removed global tag storage, a tag map reference-counts one store per asset registry
         * * All configs can mix fixed and loose tag maps
         */
        FixedTags,
        /** Added Version information to AssetPackageData */
        WorkspaceDomain,
        /** Added ImportedClasses to AssetPackageData */
        PackageImportedClasses,
        /** A new version number of UE5 was added to FPackageFileSummary */
        PackageFileSummaryVersionChange,
        /** Change to linker export/import resource serialization */
        ObjectResourceOptionalVersionChange,
        /** Added FIoHash for each FIoChunkId in the package to the AssetPackageData. */
        AddedChunkHashes,
    }

    val guid = FGuid(Ar)
    val version = if (guid == versionGuid) Type.values().getOrElse(Ar.readInt32(), ::fallback) else fallback(null)

    private fun fallback(ordinal: Int?): Type {
        val latest = Type.values().last()
        if (ordinal != null) LOG_JFP.warn("Unknown FAssetRegistryVersion::Type with ordinal $ordinal, defaulting to latest supported version ($latest)")
        return latest
    }

    override fun compareTo(other: Type) = version.ordinal.compareTo(other.ordinal)
}