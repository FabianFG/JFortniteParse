@file:Suppress("EXPERIMENTAL_API_USAGE")

package me.fungames.jfortniteparse.converters.valorant

import me.fungames.jfortniteparse.converters.ue4.textures.toBufferedImage
import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.fileprovider.FileProvider
import me.fungames.jfortniteparse.ue4.assets.exports.tex.UTexture2D
import me.fungames.jfortniteparse.ue4.assets.exports.valorant.CharacterRoleDataAsset
import me.fungames.jfortniteparse.ue4.assets.exports.valorant.CharacterRoleUIData
import java.awt.image.BufferedImage

class RoleContainer(val role: CharacterRoleUIData, val icon : BufferedImage?)

fun CharacterRoleDataAsset.createContainer(provider: FileProvider): RoleContainer {
    val uiData = provider.loadGameFile(this.uiData)?.getExportOfType<CharacterRoleUIData>() ?: throw ParserException("Failed to load UI Data for role")
    val icon = uiData.displayIcon?.let {
        provider.loadGameFile(it)
            ?.getExportOfType<UTexture2D>()
            ?.toBufferedImage()
    }
    return RoleContainer(uiData, icon)
}