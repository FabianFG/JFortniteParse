@file:Suppress("EXPERIMENTAL_API_USAGE")

package me.fungames.jfortniteparse.ue4.io.al2

import me.fungames.jfortniteparse.ue4.reader.FArchive

class FScriptObjectEntry {
    var objectName: FMinimalName
    var globalIndex: FPackageObjectIndex
    var outerIndex: FPackageObjectIndex
    var cdoClassIndex: FPackageObjectIndex

    constructor(Ar: FArchive) {
        objectName = FMinimalName(Ar)
        globalIndex = FPackageObjectIndex(Ar)
        outerIndex = FPackageObjectIndex(Ar)
        cdoClassIndex = FPackageObjectIndex(Ar)
    }
}