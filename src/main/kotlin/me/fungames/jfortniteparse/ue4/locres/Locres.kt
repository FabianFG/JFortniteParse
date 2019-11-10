package me.fungames.jfortniteparse.ue4.locres

import me.fungames.jfortniteparse.ue4.UEClass
import me.fungames.jfortniteparse.ue4.reader.FByteArchive
import java.io.File

@Suppress("EXPERIMENTAL_API_USAGE")
class Locres(val locres : ByteArray, val fileName : String, val language: FnLanguage) {

    val texts : FTextLocalizationResource

    constructor(locresFile : File) : this(locresFile.readBytes(), locresFile.nameWithoutExtension, FnLanguage.UNKNOWN)

    init {
        val locresAr = FByteArchive(locres)
        texts = FTextLocalizationResource(locresAr)
        UEClass.logger.info("Successfully parsed locres package : $fileName")
    }
}