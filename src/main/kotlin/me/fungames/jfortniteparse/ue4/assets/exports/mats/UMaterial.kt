package me.fungames.jfortniteparse.ue4.assets.exports.mats

import me.fungames.jfortniteparse.ue4.assets.PakPackage
import me.fungames.jfortniteparse.ue4.assets.enums.EBlendMode
import me.fungames.jfortniteparse.ue4.assets.exports.tex.UTexture
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.converters.CMaterialParams
import me.fungames.jfortniteparse.ue4.objects.uobject.FObjectExport
import me.fungames.jfortniteparse.ue4.versions.GAME_UE4_BASE

@ExperimentalUnsignedTypes
class UMaterial : UMaterialInterface {
    val TwoSided = false
    val bDisableDepthTest = false
    val bIsMasked = false
    val BlendMode = EBlendMode.BLEND_Opaque
    val OpacityMaskClipValue = 0.333f
    val ReferencedTextures = emptyArray<UTexture>()
    var referencedTextures = mutableListOf<UTexture>()

    constructor() : super()
    constructor(exportObject: FObjectExport) : super(exportObject)

    override fun deserialize(Ar: FAssetArchive, validPos: Int) {
        super.deserialize(Ar, validPos)
        referencedTextures = ReferencedTextures.toMutableList()

        if (Ar.game >= GAME_UE4_BASE) {
            // UE4 has complex FMaterialResource format, so avoid reading anything here, but
            // scan package's imports for UTexture objects instead
            scanForTextures(Ar)
            Ar.seek(validPos)
        }
    }

    fun scanForTextures(Ar: FAssetArchive) {
        //!! NOTE: this code will not work when textures are located in the same package - they don't present in import table
        //!! but could be found in export table. That's true for Simplygon-generated materials.
        val owner = Ar.owner as PakPackage
        for (imp in owner.importMap) {
            if (imp.className.text.startsWith("Texture", true))
                owner.loadImport<UTexture>(imp)?.let { referencedTextures.add(it) }
        }
    }

    override fun getParams(params: CMaterialParams) {
        super.getParams(params)

        var diffWeight = 0
        var normWeight = 0
        var specWeight = 0
        var specPowWeight = 0
        var opWeight = 0
        var emWeight = 0

        fun diffuse(check: Boolean, weight: Int, tex: UTexture) {
            if (check && weight > diffWeight) {
                params.diffuse = tex
                diffWeight = weight
            }
        }

        fun normal(check: Boolean, weight: Int, tex: UTexture) {
            if (check && weight > normWeight) {
                params.normal = tex
                normWeight = weight
            }
        }

        fun specular(check: Boolean, weight: Int, tex: UTexture) {
            if (check && weight > specWeight) {
                params.specular = tex
                specWeight = weight
            }
        }

        fun specPow(check: Boolean, weight: Int, tex: UTexture) {
            if (check && weight > specPowWeight) {
                params.specPower = tex
                specPowWeight = weight
            }
        }

        fun opacity(check: Boolean, weight: Int, tex: UTexture) {
            if (check && weight > opWeight) {
                params.opacity = tex
                opWeight = weight
            }
        }

        fun emissive(check: Boolean, weight: Int, tex: UTexture) {
            if (check && weight > emWeight) {
                params.emissive = tex
                emWeight = weight
            }
        }

        for (i in 0 until referencedTextures.size) {
            val tex = referencedTextures[i]
            val name = tex.name
            if (name.contains("noise", true)) continue
            if (name.contains("detail", true)) continue

            diffuse(name.contains("diff", true), 100, tex)
            normal(name.contains("norm", true), 100, tex)
            diffuse(name.endsWith("_Tex", true), 80, tex)
            diffuse(name.contains("_Tex", true), 60, tex)
            diffuse(name.endsWith("_D", true), 20, tex)
            opacity(name.contains("_OM", true), 20, tex)

            diffuse(name.contains("_DI", true), 20, tex)
            diffuse(name.contains("_D", true), 11, tex)
            diffuse(name.contains("_Albedo", true), 19, tex)
            diffuse(name.endsWith("_C", true), 10, tex)
            diffuse(name.endsWith("_CM", true), 12, tex)
            normal(name.endsWith("_N", true), 20, tex)
            normal(name.endsWith("_NM", true), 20, tex)
            normal(name.contains("_N", true), 9, tex)

            specular(name.endsWith("_S", true), 20, tex)
            specular(name.contains("_S_", true), 15, tex)
            specPow(name.endsWith("_SP", true), 20, tex)
            specPow(name.endsWith("_SM", true), 20, tex)
            specPow(name.contains("_SP", true), 9, tex)
            emissive(name.endsWith("_E", true), 20, tex)
            emissive(name.endsWith("_EM", true), 21, tex)
            opacity(name.endsWith("_A", true), 20, tex)
            if (bIsMasked)
                opacity(name.endsWith("_Mask", true), 2, tex)
            diffuse(name.startsWith("df_", true), 20, tex)
            specular(name.startsWith("sp_", true), 20, tex)
            normal(name.startsWith("no_", true), 20, tex)

            normal(name.contains("Norm", true), 80, tex)
            emissive(name.contains("Emis", true), 80, tex)
            specular(name.contains("Specular", true), 80, tex)
            opacity(name.contains("Opac", true), 80, tex)
            opacity(name.contains("Alpha", true), 100, tex)

            diffuse(i == 0, 1, tex)                                     // 1st texture as lowest weight
        }

        // do not allow normal map became a diffuse
        if ((params.diffuse == params.normal && diffWeight < normWeight) ||
            (params.diffuse != null && params.diffuse!!.isTextureCube()))
            params.diffuse = null
    }

    override fun appendReferencedTextures(outTextures: MutableList<UUnrealMaterial>, onlyRendered: Boolean) {
        if (onlyRendered) {
            // default implementation does that
            super.appendReferencedTextures(outTextures, onlyRendered)
        } else {
            for (tex in referencedTextures) {
                if (!outTextures.contains(tex))
                    outTextures.add(tex)
            }
        }
    }
}