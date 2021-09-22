package me.fungames.jfortniteparse.ue4.converters.meshes.psk.psk

import me.fungames.jfortniteparse.ue4.objects.core.math.FQuat
import me.fungames.jfortniteparse.ue4.objects.core.math.FVector
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

class VJointPosPsk(
    val orientation: FQuat,
    val position: FVector,
    val length: Float,
    val size: FVector
) {
    fun serialize(Ar: FArchiveWriter) {
        orientation.serialize(Ar)
        position.serialize(Ar)
        Ar.writeFloat32(length)
        size.serialize(Ar)
    }
}