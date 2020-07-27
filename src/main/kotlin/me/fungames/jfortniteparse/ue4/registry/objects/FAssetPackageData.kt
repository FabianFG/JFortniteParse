package me.fungames.jfortniteparse.ue4.registry.objects

import me.fungames.jfortniteparse.ue4.FGuid
import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.registry.reader.FNameTableArchive

@ExperimentalUnsignedTypes
class FAssetPackageData(Ar : FNameTableArchive, serializeHash : Boolean) : UClass() {

    val packageName = Ar.readFName()
    val diskSize = Ar.readInt64()
    val packageGuid = FGuid(Ar)
    val cookedHash = if(serializeHash)
        FMD5Hash(Ar)
    else null

}