package me.fungames.jfortniteparse.ue4.registry.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid
import me.fungames.jfortniteparse.ue4.reader.FArchive

@ExperimentalUnsignedTypes
class FAssetRegistryVersion(Ar: FArchive) : UClass(), Comparable<FAssetRegistryVersion.Type> {
    companion object {
        val versionGuid = FGuid(0x717F9EE7u, 0xE9B0493Au, 0x88B39132u, 0x1B388107u)
    }

    enum class Type {
        PreVersioning,		    // From before file versioning was implemented
        HardSoftDependencies,	// The first version of the runtime asset registry to include file versioning.
        AddAssetRegistryState,	// Added FAssetRegistryState and support for piecemeal serialization
        ChangedAssetData,		// AssetData serialization format changed, versions before this are not readable
        RemovedMD5Hash,			// Removed MD5 hash from package data
        AddedHardManage,		// Added hard/soft manage references
        AddedCookedMD5Hash;		// Added MD5 hash of cooked package to package data

        companion object {
            fun latest() = values().last()
            fun safeValue(ordinal: Int) = runCatching { values()[ordinal] }.getOrDefault(latest())
        }
    }

    val guid = FGuid(Ar)
    val version = if (guid == versionGuid) Type.safeValue(Ar.readInt32()) else Type.latest()

    override fun compareTo(other: Type) = version.ordinal.compareTo(other.ordinal)
}