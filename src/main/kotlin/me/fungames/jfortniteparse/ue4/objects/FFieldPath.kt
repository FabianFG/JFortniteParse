package me.fungames.jfortniteparse.ue4.objects

import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.objects.uobject.FName.Companion.NAME_None
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex

class FFieldPath {
    var path: MutableList<FName>
    var resolvedOwner: FPackageIndex /*UStruct*/

    constructor(Ar: FAssetArchive) {
        path = Ar.readArray { Ar.readFName() }
        // The old serialization format could save 'None' paths, they should be just empty
        if (path.size == 1 && path[0] == NAME_None) {
            path.clear()
        }
        //if (Ar.customVer(FFortniteMainBranchObjectVersion.GUID) >= FFortniteMainBranchObjectVersion.FFieldPathOwnerSerialization || Ar.customVer(FReleaseObjectVersion.GUID) >= FReleaseObjectVersion.FFieldPathOwnerSerialization) {
        resolvedOwner = FPackageIndex(Ar)
        //}
    }

    constructor() {
        path = mutableListOf()
        resolvedOwner = FPackageIndex()
    }

    constructor(path: MutableList<FName>, resolvedOwner: FPackageIndex) {
        this.path = path
        this.resolvedOwner = resolvedOwner
    }
}