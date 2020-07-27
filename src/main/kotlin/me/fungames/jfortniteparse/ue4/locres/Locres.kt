package me.fungames.jfortniteparse.ue4.locres

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FTextLocalizationResource
import me.fungames.jfortniteparse.ue4.reader.FByteArchive
import java.io.File

@Suppress("EXPERIMENTAL_API_USAGE")
class Locres(val locres: ByteArray, val fileName: String, val language: FnLanguage) {

    val texts: FTextLocalizationResource

    constructor(locresFile: File) : this(locresFile.readBytes(), locresFile.nameWithoutExtension, FnLanguage.UNKNOWN)

    init {
        val locresAr = FByteArchive(locres)
        texts = FTextLocalizationResource(locresAr)
        UClass.logger.info("Successfully parsed locres package : $fileName")
    }

    fun mergeInto(target: Locres) {
        texts.stringData.forEach { (namespace, content) ->
            val targetNamespace = target.texts.stringData[namespace] ?: run {
                val newNamespace = mutableMapOf<String, String>()
                target.texts.stringData[namespace] = newNamespace
                return@run newNamespace
            }
            content.forEach { (key, value) ->
                if (!targetNamespace.contains(key))
                    targetNamespace[key] = value
            }
        }
    }
}