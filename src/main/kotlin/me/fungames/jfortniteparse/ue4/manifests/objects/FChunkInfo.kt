package me.fungames.jfortniteparse.ue4.manifests.objects

import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid

@ExperimentalUnsignedTypes
data class FChunkInfo(
    var guid : FGuid = FGuid(0u, 0u, 0u, 0u),
    var hash : ULong = 0.toULong(),
    var shaHash : ByteArray = ByteArray(0),
    var groupNumber : UByte = 0.toUByte(),
    var windowSize : UInt = 0u,
    var fileSize : Long = 0L
)