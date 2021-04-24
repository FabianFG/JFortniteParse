@file:Suppress("EXPERIMENTAL_API_USAGE")

package me.fungames.jfortniteparse.valorant.converters

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.assets.exports.UObject
import me.fungames.jfortniteparse.ue4.assets.exports.tex.UTexture2D
import me.fungames.jfortniteparse.ue4.converters.textures.toBufferedImage
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex
import me.fungames.jfortniteparse.util.cut
import me.fungames.jfortniteparse.util.scale
import me.fungames.jfortniteparse.util.wrap
import me.fungames.jfortniteparse.valorant.ValorantResources
import me.fungames.jfortniteparse.valorant.enums.ECharacterAbilitySlot
import me.fungames.jfortniteparse.valorant.exports.CharacterAbilityUIData
import me.fungames.jfortniteparse.valorant.exports.CharacterDataAsset
import me.fungames.jfortniteparse.valorant.exports.CharacterRoleDataAsset
import me.fungames.jfortniteparse.valorant.exports.CharacterUIData
import java.awt.AlphaComposite
import java.awt.Color
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import kotlin.math.roundToInt

enum class IconType {
    BustPortrait,
    FullPortrait,
    DisplayIcon,
    DisplayIconSmall
}

class CharacterContainer(
    val uiData: CharacterUIData, val icon: BufferedImage, val iconType: IconType,
    val abilities: Map<ECharacterAbilitySlot, CharacterAbilityContainer>, val role: RoleContainer?
) {
    fun getImage() = getImage(this)
}

fun CharacterDataAsset.createContainer(iconType: IconType): CharacterContainer {
    val uiData = UIData?.load<UObject>()?.owner?.getExportOfType<CharacterUIData>() // uiData is a BlueprintGeneratedClass
        ?: throw ParserException("Failed to load UI Data for character")
    val icon = when (iconType) {
        IconType.BustPortrait -> uiData.BustPortrait
        IconType.FullPortrait -> uiData.FullPortrait
        IconType.DisplayIcon -> uiData.DisplayIcon
        IconType.DisplayIconSmall -> uiData.DisplayIconSmall
    }?.load<UTexture2D>()?.toBufferedImage() ?: throw ParserException("Failed to load $iconType")
    val abilities = mutableMapOf<ECharacterAbilitySlot, CharacterAbilityContainer>()
    if (uiData.Abilities != null)
        for ((slotEnumRaw, abilityPkgIndexRaw) in uiData.Abilities!!.entries) {
            val data = (abilityPkgIndexRaw.getTagTypeValueLegacy() as FPackageIndex).load<CharacterAbilityUIData>() ?: continue
            abilities[ECharacterAbilitySlot.valueOf((slotEnumRaw.getTagTypeValueLegacy() as FName).text.substringAfter("ECharacterAbilitySlot::"))] =
                CharacterAbilityContainer(data, data.DisplayIcon?.load<UTexture2D>()?.toBufferedImage())
        }
    val role = Role?.load<UObject>()?.owner?.getExportOfType<CharacterRoleDataAsset>()?.createContainer() // role is a BlueprintGeneratedClass
    return CharacterContainer(uiData, icon, iconType, abilities, role)
}

data class CharacterAbilityContainer(val ability: CharacterAbilityUIData, val icon: BufferedImage?)

private fun getImage(container: CharacterContainer): BufferedImage {
    val char = container.uiData
    val icon = if (container.icon.width != iconWidth || container.icon.height != iconHeight)
        container.icon.scale(iconWidth, iconHeight).cut(652)
    else container.icon
    val result = BufferedImage(2 * icon.width + 30, icon.height, BufferedImage.TYPE_INT_ARGB)
    val g = result.createGraphics()
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)

    //TODO Actual Background
    g.color = Color.BLACK
    g.fillRect(0, 0, result.width, result.height)

    g.drawImage(icon, 0, 0, null)

    val dinLight = ValorantResources.dinNextLight
    val dinBold = ValorantResources.dinNextBold

    val beginX = icon.width
    val textX = beginX + (roleIconWidth / 2)
    var currentY = 50
    //Draw the header section with name and role
    val role = container.role
    if (role?.icon != null) {
        val backupComposite = g.composite
        g.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f)
        g.drawImage(role.icon, beginX, currentY, null)
        g.composite = backupComposite
    }

    currentY += (roleIconHeight * 0.4).roundToInt()

    //Draw the role name
    if (role?.role != null) {
        val roleName = (role.role.DisplayName?.text ?: role.role.name).toUpperCase()
        g.font = dinBold.deriveFont(25f)
        g.color = Color.WHITE
        g.drawString(roleName, textX, currentY)
    }

    currentY += g.fontMetrics.height + 50

    //Draw the character name
    val charName = (char.DisplayName?.text ?: char.name).toUpperCase()
    g.font = dinBold.deriveFont(100f)
    g.color = Color(233, 233, 173)
    g.drawString(charName, textX, currentY)
    currentY += (g.fontMetrics.height / 2) - 10

    //Draw the character description
    char.Description?.apply {
        g.font = dinBold.deriveFont(20f)
        g.color = Color.WHITE
        text.wrap(g.fontMetrics, result.width - beginX - 30).forEach {
            g.drawString(it, beginX, currentY)
            currentY += g.fontMetrics.height
        }
    }

    currentY += 20

    //Draw the abilities
    val abilityTitleFont = dinBold.deriveFont(25f)
    val abilitySlotFont = dinLight.deriveFont(18f)
    val abilityDescFont = dinLight.deriveFont(15f)
    val abilityX = beginX + 20
    val abilityTextX = abilityX + abilityIconWidth + 30
    container.abilities.forEach { (slot, ability) ->
        //Draw the ability icon
        val abilityIcon = ability.icon
        if (abilityIcon != null) {
            g.drawImage(
                if (abilityIcon.width != abilityIconWidth || abilityIcon.height != abilityIconHeight)
                    abilityIcon.scale(abilityIconWidth, abilityIconHeight)
                else abilityIcon, abilityX, currentY, null
            )
        }

        val atleastY = currentY + abilityIconHeight + 20

        //Draw the ability name
        val abilityName = (ability.ability.DisplayName?.text ?: ability.ability.name).toUpperCase()
        currentY += 15
        g.font = abilityTitleFont
        g.drawString(abilityName, abilityTextX, currentY)

        //Draw the ability slot next to name
        val slotX = abilityTextX + g.fontMetrics.stringWidth(abilityName) + 10
        val abilitySlot = slot.displayName.toUpperCase()
        g.font = abilitySlotFont
        g.drawString(abilitySlot, slotX, currentY - 3)

        //Draw the ability description
        ability.ability.Description?.apply {
            g.font = abilityDescFont
            currentY += g.fontMetrics.height + 5
            text.wrap(g.fontMetrics, result.width - abilityTextX - 50).forEach {
                g.drawString(it, abilityTextX, currentY)
                currentY += g.fontMetrics.height
            }
        }
        currentY += 20
        if (atleastY > currentY)
            currentY = atleastY
    }

    return result
}

private const val iconWidth = 1028
private const val iconHeight = 1028
private const val roleIconWidth = 128
private const val roleIconHeight = 128
private const val abilityIconHeight = 90
private const val abilityIconWidth = 90