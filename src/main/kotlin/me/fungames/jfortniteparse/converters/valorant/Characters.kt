@file:Suppress("EXPERIMENTAL_API_USAGE")
package me.fungames.jfortniteparse.converters.valorant

import me.fungames.jfortniteparse.converters.ue4.textures.toBufferedImage
import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.fileprovider.FileProvider
import me.fungames.jfortniteparse.resources.Resources
import me.fungames.jfortniteparse.ue4.assets.enums.valorant.ECharacterAbilitySlot
import me.fungames.jfortniteparse.ue4.assets.exports.tex.UTexture2D
import me.fungames.jfortniteparse.ue4.assets.exports.valorant.CharacterAbilityUIData
import me.fungames.jfortniteparse.ue4.assets.exports.valorant.CharacterDataAsset
import me.fungames.jfortniteparse.ue4.assets.exports.valorant.CharacterRoleDataAsset
import me.fungames.jfortniteparse.ue4.assets.exports.valorant.CharacterUIData
import me.fungames.jfortniteparse.util.cut
import me.fungames.jfortniteparse.util.scale
import me.fungames.jfortniteparse.util.wrap
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


class CharacterContainer(val uiData : CharacterUIData, val icon : BufferedImage, val iconType: IconType,
                         val abilities : Map<ECharacterAbilitySlot, CharacterAbilityContainer>, val role : RoleContainer) {
    fun getImage() = getImage(this)
}

fun CharacterDataAsset.createContainer(provider: FileProvider, iconType: IconType) : CharacterContainer {
    val uiData = provider.loadGameFile(this.uiData)?.getExportOfType<CharacterUIData>() ?: throw ParserException("Failed to load UI Data for character")
    val icon = when(iconType) {
        IconType.BustPortrait -> provider.loadGameFile(uiData.bustPortrait)
            ?.getExportOfType<UTexture2D>()
            ?.toBufferedImage()
            ?: throw ParserException("Failed to load bust portrait")
        IconType.FullPortrait -> provider.loadGameFile(uiData.fullPortrait)
            ?.getExportOfType<UTexture2D>()
            ?.toBufferedImage()
            ?: throw ParserException("Failed to load full portrait")
        IconType.DisplayIcon -> provider.loadGameFile(uiData.displayIcon)
            ?.getExportOfType<UTexture2D>()
            ?.toBufferedImage()
            ?: throw ParserException("Failed to load display icon")
        IconType.DisplayIconSmall -> provider.loadGameFile(uiData.displayIconSmall)
            ?.getExportOfType<UTexture2D>()
            ?.toBufferedImage()
            ?: throw ParserException("Failed to load small display icon")
    }
    val abilities = uiData.abilities.mapValues { en ->
        val abilityIcon = en.value.displayIcon?.let {
            provider.loadGameFile(it)
                ?.getExportOfType<UTexture2D>()
                ?.toBufferedImage()
        }
        CharacterAbilityContainer(en.value, abilityIcon)
    }
    val role = provider.loadGameFile(this.role)?.getExportOfType<CharacterRoleDataAsset>()?.createContainer(provider) ?: throw ParserException("Failed to load role data")
    return CharacterContainer(uiData, icon, iconType, abilities, role)
}

data class CharacterAbilityContainer(val ability : CharacterAbilityUIData, val icon : BufferedImage?)


private fun getImage(container: CharacterContainer) : BufferedImage {
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

    val dinLight = Resources.dinNextLight
    val dinBold = Resources.dinNextBold

    val beginX = icon.width
    val textX = beginX + (roleIconWidth / 2)
    var currentY = 50
    //Draw the header section with name and role
    val role = container.role
    val roleIcon = role.icon
    if (roleIcon != null) {
        val backupComposite = g.composite
        g.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f)
        g.drawImage(roleIcon, beginX, currentY, null)
        g.composite = backupComposite
    }

    currentY += (roleIconHeight * 0.4).roundToInt()

    //Draw the role name
    val roleName = role.role.displayName.text.toUpperCase()
    g.font = dinBold.deriveFont(25f)
    g.color = Color.WHITE
    g.drawString(roleName, textX, currentY)
    currentY += g.fontMetrics.height + 50

    //Draw the character name
    val charName = char.displayName.text.toUpperCase()
    g.font = dinBold.deriveFont(100f)
    g.color = Color(233, 233, 173)
    g.drawString(charName, textX, currentY)
    currentY += (g.fontMetrics.height / 2) - 10

    //Draw the character description
    val desc = char.description.text
    g.font = dinBold.deriveFont(20f)
    g.color = Color.WHITE
    val lines = desc.wrap(g.fontMetrics, result.width - beginX - 30)
    lines.forEach {
        g.drawString(it, beginX, currentY)
        currentY += g.fontMetrics.height
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
            g.drawImage(if (abilityIcon.width != abilityIconWidth || abilityIcon.height != abilityIconHeight)
                abilityIcon.scale(abilityIconWidth, abilityIconHeight)
                else abilityIcon, abilityX, currentY, null)
        }

        val atleastY = currentY + abilityIconHeight + 20

        //Draw the ability name
        val abilityName = ability.ability.displayName.text.toUpperCase()
        currentY += 15
        g.font = abilityTitleFont
        g.drawString(abilityName, abilityTextX, currentY)

        //Draw the ability slot next to name
        val slotX = abilityTextX + g.fontMetrics.stringWidth(abilityName) + 10
        val abilitySlot = slot.displayName.toUpperCase()
        g.font = abilitySlotFont
        g.drawString(abilitySlot, slotX, currentY - 3)


        //Draw the ability description
        val abilityDesc = ability.ability.description.text
        g.font = abilityDescFont
        currentY += g.fontMetrics.height + 5
        val abilityLines = abilityDesc.wrap(g.fontMetrics, result.width - abilityTextX - 50)
        abilityLines.forEach {
            g.drawString(it, abilityTextX, currentY)
            currentY += g.fontMetrics.height
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