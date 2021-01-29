package me.fungames.jfortniteparse.ue4.asyncloading2

class FGlobalImportStore {
    // Temporary initial load data
    val scriptObjectEntries = arrayListOf<FScriptObjectEntry>()
    val scriptObjectEntriesMap = hashMapOf<FPackageObjectIndex, FScriptObjectEntry>()
}