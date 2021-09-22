package me.fungames.jfortniteparse.ue4.converters.meshes.psk.psk

import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

class VBone(
    val name: String,
    val flags: UInt,
    val numChildren: Int,
    val parentIndex: Int,
    val bonePos: VJointPosPsk
) {
    fun serialize(Ar: FArchiveWriter) {
        Ar.write(name.toByteArray().copyOf(64))
        Ar.writeUInt32(flags)
        Ar.writeInt32(numChildren)
        Ar.writeInt32(parentIndex)
        bonePos.serialize(Ar)
    }
}