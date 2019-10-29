@file:Suppress("EXPERIMENTAL_API_USAGE")
package me.fungames.jfortniteparse.converters

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.fileprovider.FileProvider
import me.fungames.jfortniteparse.resources.Resources
import me.fungames.jfortniteparse.resources.getBackgroundImage
import me.fungames.jfortniteparse.resources.getDailyShopBackgroundImage
import me.fungames.jfortniteparse.resources.getFeaturedShopBackgroundImage
import me.fungames.jfortniteparse.ue4.UEClass
import me.fungames.jfortniteparse.ue4.assets.FText
import me.fungames.jfortniteparse.ue4.assets.exports.UTexture2D
import me.fungames.jfortniteparse.ue4.assets.exports.athena.AthenaItemDefinition
import me.fungames.jfortniteparse.ue4.assets.exports.fort.CosmeticVariant
import me.fungames.jfortniteparse.ue4.assets.exports.fort.FortHeroType
import me.fungames.jfortniteparse.ue4.assets.exports.fort.FortMtxOfferData
import me.fungames.jfortniteparse.ue4.assets.exports.fort.FortWeaponMeleeItemDefinition
import me.fungames.jfortniteparse.util.cut
import me.fungames.jfortniteparse.util.drawCenteredString
import me.fungames.jfortniteparse.util.scale
import java.awt.*
import java.awt.font.TextAttribute
import java.awt.image.BufferedImage
import java.text.NumberFormat
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

object ItemDefinitionInfo {
    val sets = ConcurrentHashMap<String, FText>()
    val userFacingFlags = ConcurrentHashMap<String, BufferedImage>()

    var initialized = AtomicBoolean(false)
        private set

    fun init(fileProvider: FileProvider) {
        if (initialized.get())
            return
        initialized.set(true)
        try {
            throw Exception("Not Implemented")
        } catch (e : Exception) {
            initialized.set(false)
        }
    }
}

@Throws(ParserException::class)
fun AthenaItemDefinition.createContainer(fileProvider: FileProvider,
                                         loadVariants : Boolean = true, failOnNoIconLoaded : Boolean = false, overrideIcon : BufferedImage? = null) : ItemDefinitionContainer {
    ItemDefinitionInfo.init(fileProvider)
    val icon = loadIcon(this, fileProvider)
        ?: if (failOnNoIconLoaded) throw ParserException("Failed to load icon") else Resources.fallbackIcon
    if (loadVariants && this.variants != null) {
        val map = mutableMapOf<CosmeticVariant, BufferedImage>()
        this.variants?.variants?.forEach {
            if (it.previewImage != null) {
                val iconPkg = fileProvider.loadGameFile(it.previewImage!!)
                it.previewIcon = iconPkg?.getExportOfTypeOrNull<UTexture2D>()?.toBufferedImage()
            }
        }
    }
    return ItemDefinitionContainer(this, icon)
}

data class ItemDefinitionContainer(val itemDefinition: AthenaItemDefinition, var icon : BufferedImage) : Cloneable {
    val variantsLoaded : Boolean
        get() = itemDefinition.variants?.variants?.firstOrNull { it.previewIcon != null } != null

    fun getImage() = getImage(this)
    fun getImageWithVariants() = getImageWithVariants(this)
    fun getImageNoVariants() = getImageNoVariants(this)
    fun getShopFeaturedImage(price: Int) = getShopFeaturedImage(this, price)
    fun getShopDailyImage(price: Int) = getShopDailyImage(this, price)
}

fun getImage(container: ItemDefinitionContainer) : BufferedImage {
    return if (container.variantsLoaded)
        getImageWithVariants(container)
    else
        getImageNoVariants(container)
}

fun getImageWithVariants(container: ItemDefinitionContainer) : BufferedImage {
    TODO("not implemented")
}

private val trackingAttr: Map<TextAttribute, Double> by lazy { mapOf(TextAttribute.TRACKING to 0.03) }
private const val barHeight = 160
fun getImageNoVariants(container: ItemDefinitionContainer) : BufferedImage {
    val itemDef = container.itemDefinition
    var icon = container.icon
    if (icon.width != 512 || icon.height != 512)
        icon = icon.scale(512, 512)
    val result = itemDef.rarity.getBackgroundImage()
    val g = result.createGraphics()
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
    g.drawImage(icon, 5, 5, null)

    val burbank = Resources.burbank
    val notoSans = Resources.notoSans

    g.paint = Color(0, 0, 0, 100)
    g.fillRect(5, 5 + 512 - barHeight, 512, barHeight)
    g.font = burbank
    val displayName = itemDef.displayName?.text?.toUpperCase()
    if (displayName != null) {
        g.color = Color.WHITE
        var fontSize = 60f
        g.font = burbank.deriveFont(Font.PLAIN, fontSize).deriveFont(trackingAttr)
        var fm = g.fontMetrics
        while (fm.stringWidth(displayName) > result.width - 10) {
            fontSize--
            g.font = burbank.deriveFont(Font.PLAIN, fontSize).deriveFont(trackingAttr)
            fm = g.fontMetrics
        }
        g.drawCenteredString(displayName, result.width / 2, result.height - 95)
    }
    val description = itemDef.description?.text
    if (description != null) {
        g.color = Color.LIGHT_GRAY
        var fontSize: Float
        var fm : FontMetrics
        var y = icon.height - 50
        val lines = description.split("\\r?\\n")
        // TODO Sets
        lines.forEach {
            fontSize = 25f
            g.font = notoSans.deriveFont(Font.PLAIN, fontSize)
            fm = g.fontMetrics
            while (fm.stringWidth(it) > result.width - 10) {
                fontSize--
                g.font = notoSans.deriveFont(Font.PLAIN, fontSize)
                fm = g.fontMetrics
            }
            g.drawCenteredString(it, result.width / 2, y)
            y += 35
        }
    }
    g.dispose()
    return result
}

private const val featuredAdditionalHeight = 200
private const val featuredBarHeight = 131
fun getShopFeaturedImage(container: ItemDefinitionContainer, price : Int) : BufferedImage {
    val itemDef = container.itemDefinition
    var icon = container.icon
    if (icon.width != 1024 || icon.height != 1024)
        icon = icon.scale(1024, 1024)
    val result = itemDef.rarity.getFeaturedShopBackgroundImage()
    val g = result.createGraphics()
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
    g.drawImage(icon.cut(result.width - 22), 11, 11, null)

    g.paint = Color(0,0,0, 100)
    g.fillRect(11, 11 + icon.height - featuredAdditionalHeight, result.width - 22, featuredAdditionalHeight)

    val burbank = Resources.burbank
    val notoSans = Resources.notoSans
    val notoSansBold = Resources.notoSansBold

    val displayName = itemDef.displayName?.text?.toUpperCase()
    if (displayName != null) {
        g.color = Color.WHITE
        var fontSize = 80f
        g.font = burbank.deriveFont(Font.PLAIN, fontSize).deriveFont(trackingAttr)
        var fm = g.fontMetrics
        while (fm.stringWidth(displayName) > result.width - 10) {
            fontSize--
            g.font = burbank.deriveFont(Font.PLAIN, fontSize).deriveFont(trackingAttr)
            fm = g.fontMetrics
        }
        g.drawCenteredString(displayName, result.width / 2, icon.height - 95)
    }
    val shortDesc = itemDef.shortDescription?.text
    if (shortDesc != null) {
        g.color = Color.LIGHT_GRAY
        var fontSize = 40f
        g.font = notoSans.deriveFont(Font.PLAIN, fontSize).deriveFont(trackingAttr)
        var fm = g.fontMetrics
        while (fm.stringWidth(shortDesc) > result.width - 10) {
            fontSize--
            g.font = notoSans.deriveFont(Font.PLAIN, fontSize).deriveFont(trackingAttr)
            fm = g.fontMetrics
        }
        g.drawCenteredString(shortDesc, result.width / 2, icon.height - 20)
    }

    g.font = notoSansBold.deriveFont(Font.PLAIN, 60f)
    g.color = Color.WHITE
    val fm = g.fontMetrics
    val vbucks = Resources.vbucksIcon

    val priceS = printPrice(price)

    val vbucksX = result.width / 2 - fm.stringWidth(priceS) / 2 - vbucks.width / 2
    val vbucksY = result.height - 11 - featuredBarHeight / 2 - vbucks.height / 2
    g.drawImage(vbucks, vbucksX, vbucksY, null)
    g.drawString(priceS, vbucksX + vbucks.width + 12, vbucksY + fm.height - 27)
    return result
}

private const val dailyAdditionalHeight = 160
private const val dailyBarHeight = 83
fun getShopDailyImage(container: ItemDefinitionContainer, price: Int): BufferedImage {
    val itemDef = container.itemDefinition
    var icon = container.icon
    if (icon.width != 512 || icon.height != 512)
        icon = icon.scale(512, 512)
    val result = itemDef.rarity.getDailyShopBackgroundImage()
    val g = result.createGraphics()
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
    g.drawImage(icon, 5, 5, null)

    val burbank = Resources.burbank
    val notoSans = Resources.notoSans
    val notoSansBold = Resources.notoSansBold

    g.paint = Color(0, 0, 0, 100)
    g.fillRect(5, 5 + 512 - dailyAdditionalHeight, 512, dailyAdditionalHeight)

    val displayName = itemDef.displayName?.text?.toUpperCase()
    if (displayName != null) {
        g.color = Color.WHITE
        var fontSize = 60f
        g.font = burbank.deriveFont(Font.PLAIN, fontSize).deriveFont(trackingAttr)
        var fm = g.fontMetrics
        while (fm.stringWidth(displayName) > result.width - 10) {
            fontSize--
            g.font = burbank.deriveFont(Font.PLAIN, fontSize).deriveFont(trackingAttr)
            fm = g.fontMetrics
        }
        g.drawCenteredString(displayName, result.width / 2, icon.height - 85)
    }

    val shortDesc = itemDef.shortDescription?.text
    if (shortDesc != null) {
        g.color = Color.LIGHT_GRAY
        var fontSize = 40f
        g.font = notoSans.deriveFont(Font.PLAIN, fontSize).deriveFont(trackingAttr)
        var fm = g.fontMetrics
        while (fm.stringWidth(shortDesc) > result.width - 10) {
            fontSize--
            g.font = notoSans.deriveFont(Font.PLAIN, fontSize).deriveFont(trackingAttr)
            fm = g.fontMetrics
        }
        g.drawCenteredString(shortDesc, result.width / 2, icon.height - 20)
    }

    g.font = notoSansBold.deriveFont(Font.PLAIN, 45f)
    g.color = Color.WHITE
    val fm = g.fontMetrics
    val vbucks = Resources.vbucksIcon

    val priceS = printPrice(price)

    val vbucksX = result.width / 2 - fm.stringWidth(priceS) / 2 - vbucks.width / 2 - 20
    val vbucksY = result.height - 11 - dailyBarHeight / 2 - vbucks.height / 2 + 5
    g.drawImage(vbucks, vbucksX, vbucksY, null)
    g.drawString(priceS, vbucksX + vbucks.width + 12, vbucksY + fm.height - 10)

    return result
}

private val numberFormatter : NumberFormat by lazy { NumberFormat.getNumberInstance(Locale.US) }
private fun printPrice(price : Int) = numberFormatter.format(price)

private fun loadFeaturedIcon(itemDefinition: AthenaItemDefinition, fileProvider: FileProvider) : BufferedImage? {
    if (itemDefinition.usesDisplayAssetPath) {
        val pkg = fileProvider.loadGameFile(itemDefinition.displayAssetPath!!) ?: return null
        val offerData = pkg.getExportOfTypeOrNull<FortMtxOfferData>() ?: return null
        val texturePath = offerData.detailsImage.outerImportObject?.objectName?.text ?: return null
        if (texturePath.contains("Athena/Prototype/Textures") || texturePath.contains("Placeholder"))
            return null
        val iconPkg = fileProvider.loadGameFile(offerData.detailsImage) ?: return null
        return iconPkg.getExportOfTypeOrNull<UTexture2D>()?.toBufferedImage()
    } else
        return null
}

private fun loadNormalIcon(itemDefinition: AthenaItemDefinition, fileProvider: FileProvider) : BufferedImage? {
    if (itemDefinition.hasIcons) {
        val iconPkg = fileProvider.loadGameFile(itemDefinition.largePreviewImage!!)
        val icon = iconPkg?.getExportOfType<UTexture2D>()?.toBufferedImage()
        if (icon != null)
            return icon
    }
    if (itemDefinition.usesHeroDefinition) {
        val heroDefPkg = fileProvider.loadGameFile(itemDefinition.heroDefinitionPackage!!)
        val hero = heroDefPkg?.getExportOfTypeOrNull<FortHeroType>()
        if (hero != null) {
            val iconPkg = fileProvider.loadGameFile(hero.largePreviewImage)
            val icon = iconPkg?.getExportOfType<UTexture2D>()?.toBufferedImage()
            if (icon != null)
                return  icon
        }
    }
    if (itemDefinition.usesWeaponDefinition) {
        val weaponDefPkg = fileProvider.loadGameFile(itemDefinition.weaponDefinitionPackage!!)
        val weapon = weaponDefPkg?.getExportOfTypeOrNull<FortWeaponMeleeItemDefinition>()
        if (weapon != null) {
            val iconPkg = fileProvider.loadGameFile(weapon.largePreviewImage)
            val icon = iconPkg?.getExportOfType<UTexture2D>()?.toBufferedImage()
            if (icon != null)
                return  icon
        }
    }
    return null
}

private fun loadIcon(itemDefinition: AthenaItemDefinition, fileProvider: FileProvider) =
    loadFeaturedIcon(itemDefinition, fileProvider) ?: loadNormalIcon(itemDefinition, fileProvider)