package me.fungames.jfortniteparse.ue4.assets.exports.mats

import me.fungames.jfortniteparse.ue4.assets.exports.tex.UTexture
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.converters.CMaterialParams
import me.fungames.jfortniteparse.ue4.objects.core.math.FLinearColor
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex

class UMaterialInstanceConstant : UMaterialInstance() {
    @JvmField var PhysMaterialMask: FPackageIndex? = null /*PhysicalMaterialMask*/

    override fun deserialize(Ar: FAssetArchive, validPos: Int) {
        super.deserialize(Ar, validPos)
        Ar.seek(validPos)
    }

    override fun getParams(params: CMaterialParams) {
        // get params from linked UMaterial3
        val parent = Parent?.value
        if (parent != null && parent != this)
            parent.getParams(params)

        super.getParams(params)

        // get local parameters
        var diffWeight = 0
        var normWeight = 0
        var specWeight = 0
        var specPowWeight = 0
        var opWeight = 0
        var emWeight = 0
        var emcWeight = 0
        var cubeWeight = 0
        var maskWeight = 0

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

        fun cubeMap(check: Boolean, weight: Int, tex: UTexture) {
            if (check && weight > cubeWeight) {
                params.cube = tex
                cubeWeight = weight
            }
        }

        fun bakedMask(check: Boolean, weight: Int, tex: UTexture) {
            if (check && weight > maskWeight) {
                params.mask = tex
                maskWeight = weight
            }
        }

        fun emissiveColor(check: Boolean, weight: Int, color: FLinearColor) {
            if (check && weight > emcWeight) {
                params.emissiveColor = color
                emcWeight = weight
            }
        }

        if (TextureParameterValues.isNotEmpty())
            params.opacity = null                   // it's better to disable opacity mask from parent material

        for (p in TextureParameterValues) {
            val name = p.ParameterInfo.Name.text
            val tex = p.ParameterValue?.value ?: continue

            if (name.contains("detail", true)) continue     // details normal etc

            diffuse(name.contains("dif", true), 100, tex)
            diffuse(name.contains("albedo", true), 100, tex)
            diffuse(name.contains("color", true), 80, tex)
            normal(name.contains("norm", true) && !name.contains("fx", true), 100, tex)
            specPow(name.contains("specpow", true), 100, tex)
            specular(name.contains("spec", true), 100, tex)
            emissive(name.contains("emiss", true), 100, tex)
            cubeMap(name.contains("cube", true), 100, tex)
            cubeMap(name.contains("refl", true), 90, tex)
            opacity(name.contains("opac", true), 90, tex)
            opacity(name.contains("trans", true) && !name.contains("transm", true), 80, tex)
            opacity(name.contains("opacity", true), 100, tex)
            opacity(name.contains("alpha", true), 100, tex)
        }

        for (p in VectorParameterValues) {
            val name = p.ParameterInfo.Name.text
            val color = p.ParameterValue ?: continue
            emissiveColor(name.contains("Emissive", true), 100, color)
        }

        // try to get diffuse texture when nothing found
        if (params.diffuse == null && TextureParameterValues.size == 1)
            params.diffuse = TextureParameterValues[0].ParameterValue?.value
    }

    override fun appendReferencedTextures(outTextures: MutableList<UUnrealMaterial>, onlyRendered: Boolean) {
        if (onlyRendered) {
            // default implementation does that
            super.appendReferencedTextures(outTextures, onlyRendered)
        } else {
            for (value in TextureParameterValues) {
                val tex = value.ParameterValue?.value
                if (tex != null && !outTextures.contains(tex))
                    outTextures.add(tex)
            }
            val parent = Parent?.value
            if (parent != null && parent != this) parent.appendReferencedTextures(outTextures, onlyRendered)
        }
    }
}