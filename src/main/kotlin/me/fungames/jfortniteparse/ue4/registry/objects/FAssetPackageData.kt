package me.fungames.jfortniteparse.ue4.registry.objects

import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid
import me.fungames.jfortniteparse.ue4.registry.reader.FNameTableArchive

class FAssetPackageData(Ar: FNameTableArchive, serializeHash: Boolean) {
    val packageName = Ar.readFName()
    val diskSize = Ar.readInt64()
    val packageGuid = FGuid(Ar)
    val cookedHash = if (serializeHash) FMD5Hash(Ar) else null
}