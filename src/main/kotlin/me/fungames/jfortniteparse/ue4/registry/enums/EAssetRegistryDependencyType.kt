package me.fungames.jfortniteparse.ue4.registry.enums

class EAssetRegistryDependencyType private constructor(val int : Int) {

    companion object {

        // Doesn't really exist
        val Referencers = EAssetRegistryDependencyType(0x00)

        // Dependencies which don't need to be loaded for the object to be used (i.e. soft object paths)
        val Soft = EAssetRegistryDependencyType(0x01)

        // Dependencies which are required for correct usage of the source asset, and must be loaded at the same time
        val Hard = EAssetRegistryDependencyType(0x02)

        // References to specific SearchableNames inside a package
        val SearchableName = EAssetRegistryDependencyType(0x04)

        // Indirect management references, these are set through recursion for Primary Assets that manage packages or other primary assets
        val SoftManage = EAssetRegistryDependencyType(0x08)

        // Reference that says one object directly manages another object, set when Primary Assets manage things explicitly
        val HardManage = EAssetRegistryDependencyType(0x10)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EAssetRegistryDependencyType

        if (int != other.int) return false

        return true
    }

    override fun hashCode(): Int {
        return int
    }

    override fun toString(): String {
        return "EAssetRegistryDependencyType(int=$int)"
    }
}