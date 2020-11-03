package me.fungames.jfortniteparse.ue4.asyncloading2

class FAsyncPackageData {
    var exportCount = 0
    var exportBundleCount = 0
    lateinit var exportBundleHeaders: MutableList<FExportBundleHeader>
    lateinit var exportBundleEntries: MutableList<FExportBundleEntry>
    lateinit var exports: MutableList<FExportObject>
    lateinit var importedAsyncPackages: MutableList<FAsyncPackage2>
    lateinit var packageNodes: MutableList<FEventLoadNode2>
    lateinit var exportBundleNodes: MutableList<FEventLoadNode2>
}