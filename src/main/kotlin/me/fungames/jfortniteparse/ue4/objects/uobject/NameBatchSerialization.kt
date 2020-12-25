package me.fungames.jfortniteparse.ue4.objects.uobject

import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.util.isAligned

/**
 * Load a name blob with precalculated hashes.
 */
fun loadNameBatch(nameDataAr: FArchive, hashDataAr: FArchive): List<String> {
    val hashDataSize = hashDataAr.size() - hashDataAr.pos()
    check(isAligned(hashDataSize, 8 /*sizeof(uint64)*/))

    //val hashVersion = hashDataAr.readUInt64()
    val num = hashDataSize / 8 - 1
    return List(num) { loadNameHeader(nameDataAr).intern() }
}

/**
 * Load names and precalculated hashes from an archive
 */
fun loadNameBatch(Ar: FArchive): List<String> {
    val num = Ar.readInt32()
    if (num == 0) {
        return emptyList()
    }

    //val numStringBytes = Ar.readUInt32()
    //val hashVersion = Ar.readUInt64()
    Ar.skip(4 + 8)

    Ar.skip(8L * num) //val hashes = Array(num) { Ar.readUInt64() }
    val headers = Array(num) { FSerializedNameHeader(Ar) }

    return List(num) {
        val header = headers[it]
        val len = header.len().toInt()

        if (header.isUtf16()) {
            String(Ar.read(len * 2), Charsets.UTF_16)
        } else {
            String(Ar.read(len), Charsets.UTF_8)
        }.intern()
    }
}