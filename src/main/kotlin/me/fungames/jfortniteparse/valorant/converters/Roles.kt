@file:Suppress("EXPERIMENTAL_API_USAGE")

package me.fungames.jfortniteparse.valorant.converters

import me.fungames.jfortniteparse.ue4.assets.exports.UExport
import me.fungames.jfortniteparse.ue4.assets.exports.tex.UTexture2D
import me.fungames.jfortniteparse.valorant.exports.CharacterRoleDataAsset
import me.fungames.jfortniteparse.valorant.exports.CharacterRoleUIData
import java.awt.image.BufferedImage

class RoleContainer(val role: CharacterRoleUIData, val icon: BufferedImage?)

fun CharacterRoleDataAsset.createContainer(): RoleContainer? {
    val uiData = this.UIData?.load<UExport>()?.owner?.getExportOfType<CharacterRoleUIData>() ?: return null // points to BlueprintGeneratedClass
    return RoleContainer(uiData, uiData.displayIcon?.load<UTexture2D>()?.toBufferedImage())
}