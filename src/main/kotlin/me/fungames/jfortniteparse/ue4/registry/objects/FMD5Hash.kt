package me.fungames.jfortniteparse.ue4.registry.objects

import me.fungames.jfortniteparse.ue4.reader.FArchive

class FMD5Hash(Ar: FArchive) {
    val hash = if (Ar.readInt32() != 0) Ar.read(16) else null
}