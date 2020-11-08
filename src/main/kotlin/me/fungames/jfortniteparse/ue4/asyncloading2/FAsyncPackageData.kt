package me.fungames.jfortniteparse.ue4.asyncloading2

class FAsyncPackageData {
    var exportCount = 0
    var exportBundleCount = 0
    lateinit var exportBundleHeaders: Array<FExportBundleHeader>
    lateinit var exportBundleEntries: Array<FExportBundleEntry>
    lateinit var exports: Array<FExportObject>
    lateinit var importedAsyncPackages: MutableList<FAsyncPackage2>
    lateinit var packageNodes: Array<FEventLoadNode2?>
    lateinit var exportBundleNodes: Array<FEventLoadNode2?>
}