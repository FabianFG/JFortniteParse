@file:Suppress("EXPERIMENTAL_API_USAGE")

package me.fungames.jfortniteparse.converters.ue4

import kotlinx.coroutines.*
import me.fungames.jfortniteparse.converters.ue4.textures.toBufferedImage
import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.enums.EMobileSpecularMask
import me.fungames.jfortniteparse.ue4.assets.enums.ETextureChannel
import me.fungames.jfortniteparse.ue4.assets.exports.mats.UMaterialInstanceConstant
import me.fungames.jfortniteparse.ue4.assets.exports.mats.UUnrealMaterial
import me.fungames.jfortniteparse.ue4.assets.exports.tex.UTexture
import me.fungames.jfortniteparse.ue4.assets.objects.FLinearColor
import me.fungames.jfortniteparse.util.toPngArray
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.imageio.ImageIO

class CMaterialParams(
    // textures
    var diffuse : UUnrealMaterial? = null,
    var normal : UUnrealMaterial? = null,
    var specular : UUnrealMaterial? = null,
    var specPower : UUnrealMaterial? = null,
    var opacity : UUnrealMaterial? = null,
    var emissive : UUnrealMaterial? = null,
    var cube : UUnrealMaterial? = null,
    var mask : UUnrealMaterial? = null,         // multiple mask textures baked into a single one
    // channels (used with Mask texture)
    var emissiveChannel: ETextureChannel = ETextureChannel.TC_NONE,
    var specularMaskChannel: ETextureChannel = ETextureChannel.TC_NONE,
    var specularPowerChannel: ETextureChannel = ETextureChannel.TC_NONE,
    var cubemapMaskChannel: ETextureChannel = ETextureChannel.TC_NONE,
    // colors
    var emissiveColor : FLinearColor = FLinearColor(0.5f, 0.5f, 1.0f, 1f),       // light-blue color
    // mobile
    var useMobileSpecular : Boolean = false,
    var mobileSpecularPower : Float = 0.0f,
    var mobileSpecularMask : EMobileSpecularMask = EMobileSpecularMask.MSM_Constant,        // EMobileSpecularMask
    // tweaks
    var specularFromAlpha : Boolean = false,
    var opacityFromAlpha : Boolean = false
) {
    fun appendAllTextures(outTextures : MutableList<UUnrealMaterial>) {
        if (diffuse != null) outTextures.add(diffuse!!)
        if (normal != null) outTextures.add(normal!!)
        if (specular != null) outTextures.add(specular!!)
        if (specPower != null) outTextures.add(specPower!!)
        if (opacity != null) outTextures.add(opacity!!)
        if (emissive != null) outTextures.add(emissive!!)
        if (cube != null) outTextures.add(cube!!)
        if (mask != null) outTextures.add(mask!!)
    }

    fun isNull() = diffuse == null
            && normal == null
            && specular == null
            && specPower == null
            && opacity == null
            && emissive == null
            && cube == null
            && mask == null
}

class MaterialExport(val matFileName : String, val matFile : String, val textures : Map<String, BufferedImage>, val parentExport : MaterialExport?) {

    fun writeToDir(dir : File) {
        dir.mkdirs()
        val exportJobs = mutableListOf<Deferred<Any?>>()
        val scope = object : CoroutineScope {
            private val job = Job()
            override val coroutineContext = job + Dispatchers.IO
        }
        File(dir.absolutePath + "/$matFileName").writeText(matFile)
        textures.forEach { (name, img) ->
            exportJobs.add(scope.async { ImageIO.write(img, "png", File(dir.absolutePath + "/$name.png")) })
        }
        parentExport?.writeToDir(dir)

        runBlocking { exportJobs.awaitAll() }
        scope.cancel()
    }

    fun appendToZip(zos : ZipOutputStream) {
        runCatching {
            val mat = ZipEntry(matFileName)
            zos.putNextEntry(mat)
            zos.write(matFile.toByteArray())
            zos.flush()
            zos.closeEntry()
        }
        val scope = object : CoroutineScope {
            private val job = Job()
            override val coroutineContext = job + Dispatchers.IO
        }
        val exports = textures.mapValues { scope.async { it.value.toPngArray() } }
        for ((key, value) in exports) {
            runCatching { val e = ZipEntry("$key.png")
                zos.putNextEntry(e)
                zos.write(runBlocking { value.await() })
                zos.flush()
                zos.closeEntry() }
        }
        parentExport?.appendToZip(zos)
    }

    fun toZip() : ByteArray {
        val bos = ByteArrayOutputStream()
        val zos = ZipOutputStream(bos)
        zos.setMethod(ZipOutputStream.STORED)
        appendToZip(zos)
        zos.close()
        return bos.toByteArray()
    }
}

fun UUnrealMaterial.export() : MaterialExport {
    val allTextures = mutableListOf<UUnrealMaterial>()
    this.appendReferencedTextures(allTextures, false)

    val params = CMaterialParams()
    this.getParams(params)
    if ((params.isNull() || params.diffuse == this) && allTextures.size == 0) {
        // empty/unknown material, or material itself is a texture
        return MaterialExport("","", emptyMap(), null)
    }

    val toExport = mutableListOf<UUnrealMaterial>()

    val sb = StringBuilder()
    fun proc(name : String, arg : UUnrealMaterial?) {
        if (arg != null) {
            sb.appendln("$name=${arg.name}")
            toExport.add(arg)
        }
    }

    proc("Diffuse", params.diffuse)
    proc("Normal", params.normal)
    proc("Specular", params.specular)
    proc("SpecPower", params.specPower)
    proc("Opacity", params.opacity)
    proc("Emissive", params.emissive)
    proc("Cube", params.cube)
    proc("Mask", params.mask)

    val matFile = sb.toString()

    //TODO create a props file like umodel?

    val exportJobs = mutableListOf<Deferred<Any?>>()

    val scope = object : CoroutineScope {
        private val job = Job()
        override val coroutineContext = job + Dispatchers.IO
    }

    val textures = ConcurrentHashMap<String, BufferedImage>()
    for (obj in toExport) {
        if (obj is UTexture && obj != this) //TODO might also work with non-textures, not sure whether that can happen
            exportJobs.add(scope.async {
                runCatching { obj.toBufferedImage() }.onSuccess { textures[obj.name] = it }.onFailure { UClass.logger.warn(it) { "Conversion of texture ${obj.name} failed" } }
            })
        else
            UClass.logger.error { "Material Export contained an toExport that was not an texture, please report this" }
    }

    val parentExport = if (this is UMaterialInstanceConstant) {
        parent?.export()
    } else null
    //TODO TextureCube3 ???

    // Wait for all texture exporters to finish
    runBlocking { exportJobs.awaitAll() }

    return MaterialExport("$name.mat", matFile, textures, parentExport)
}