package me.fungames.jfortniteparse.ue4.registry.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.registry.enums.EAssetRegistryDependencyType

@ExperimentalUnsignedTypes
class FDependsNode : UClass() {
    lateinit var identifier: FAssetIdentifier
    lateinit var hardDependencies : MutableList<Int>
        private set
    lateinit var softDependencies : MutableList<Int>
        private set
    lateinit var nameDependencies : MutableList<Int>
        private set
    lateinit var softManageDependencies : MutableList<Int>
        private set
    lateinit var hardManageDependencies : MutableList<Int>
        private set
    lateinit var referencers : MutableList<Int>
        private set

    fun reserve(depCounts : Map<EAssetRegistryDependencyType, Int>) {
        for (kv in depCounts) {
            when(kv.key) {
                EAssetRegistryDependencyType.Soft -> softDependencies = ArrayList(kv.value)
                EAssetRegistryDependencyType.Hard -> hardDependencies = ArrayList(kv.value)
                EAssetRegistryDependencyType.SearchableName -> nameDependencies = ArrayList(kv.value)
                EAssetRegistryDependencyType.SoftManage -> softManageDependencies = ArrayList(kv.value)
                EAssetRegistryDependencyType.HardManage -> hardManageDependencies = ArrayList(kv.value)
                else -> referencers = ArrayList(kv.value)
            }
        }
    }

    fun add(node : Int, type : EAssetRegistryDependencyType) {
        when(type) {
            EAssetRegistryDependencyType.Soft -> softDependencies.add(node)
            EAssetRegistryDependencyType.Hard -> hardDependencies.add(node)
            EAssetRegistryDependencyType.SearchableName -> nameDependencies.add(node)
            EAssetRegistryDependencyType.SoftManage -> softManageDependencies.add(node)
            EAssetRegistryDependencyType.HardManage -> hardManageDependencies.add(node)
            else -> referencers.add(node)
        }
    }
}