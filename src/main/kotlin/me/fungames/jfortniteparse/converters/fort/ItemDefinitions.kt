@file:Suppress("EXPERIMENTAL_API_USAGE")

package me.fungames.jfortniteparse.converters.fort

import FortItemCategory
import me.fungames.jfortniteparse.converters.ue4.toBufferedImage
import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.fileprovider.FileProvider
import me.fungames.jfortniteparse.resources.*
import me.fungames.jfortniteparse.ue4.UEClass
import me.fungames.jfortniteparse.ue4.assets.FPackageIndex
import me.fungames.jfortniteparse.ue4.assets.FText
import me.fungames.jfortniteparse.ue4.assets.exports.UDataTable
import me.fungames.jfortniteparse.ue4.assets.exports.UTexture2D
import me.fungames.jfortniteparse.ue4.assets.exports.ItemDefinition
import me.fungames.jfortniteparse.ue4.assets.exports.fort.FortItemSeriesDefinition
import me.fungames.jfortniteparse.ue4.assets.exports.fort.FortMtxOfferData
import me.fungames.jfortniteparse.ue4.locres.Locres
import me.fungames.jfortniteparse.util.cut
import me.fungames.jfortniteparse.util.drawCenteredString
import me.fungames.jfortniteparse.util.scale
import java.awt.*
import java.awt.font.TextAttribute
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.text.NumberFormat
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.min
import kotlin.math.roundToInt

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
            loadSets(fileProvider)
            loadUserFacingFlags(fileProvider)
        } catch (e: Exception) {
            initialized.set(false)
        }
    }


    private const val setAssetPath = "FortniteGame/Content/Athena/Items/Cosmetics/Metadata/CosmeticSets.uasset"
    private fun loadSets(fileProvider: FileProvider) : Boolean {
        val setsPkg = fileProvider.loadGameFile(setAssetPath) ?: return false
        return try {
            val table = setsPkg.getExportOfType<UDataTable>()
            table.rows.forEach { (name, uObject) ->
                val displayName = uObject.getOrNull<FText>("DisplayName")
                if (displayName != null)
                    sets.putIfAbsent(name.text, displayName)
            }
            true
        } catch (e : Exception) {
            false
        }
    }
    private const val itemCategoriesAssetPath = "FortniteGame/Content/Items/ItemCategories.uasset"
    private fun loadUserFacingFlags(fileProvider: FileProvider) : Boolean {
        val itemsPkg = fileProvider.loadGameFile(itemCategoriesAssetPath) ?: return false
        return try {
            val itemCategory = itemsPkg.getExportOfType<FortItemCategory>()
            val requiredImgs = mutableMapOf<FPackageIndex, BufferedImage>()
            itemCategory.userFacingFlags.forEach { _, (_, index) ->
                if (!requiredImgs.containsKey(index)) {
                    val iconPkg = fileProvider.loadGameFile(index)
                    if (iconPkg != null)
                        requiredImgs[index] = iconPkg.getExportOfType<UTexture2D>().toBufferedImage()
                }
            }
            itemCategory.userFacingFlags.forEach { flag, (_, index) ->
                userFacingFlags.putIfAbsent(flag, requiredImgs[index]!!)
            }
            true
        } catch (e : Exception) {
            false
        }

    }
}

@Throws(ParserException::class)
fun ItemDefinition.createContainer(
    fileProvider: FileProvider,
    loadVariants: Boolean = true, failOnNoIconLoaded: Boolean = false, overrideIcon: BufferedImage? = null
): ItemDefinitionContainer {
    ItemDefinitionInfo.init(fileProvider)
    var icon = overrideIcon
    var isFeatured = false
    if (icon == null) {
        icon = loadFeaturedIcon(this, fileProvider)
        if (icon != null)
            isFeatured = true
        else
            icon = loadNormalIcon(this, fileProvider)
        if (icon == null) {
            if (failOnNoIconLoaded)
                throw ParserException("Failed to load icon")
            else
                icon = Resources.fallbackIcon
        }
    }
    val seriesDef = series?.let { loadSeriesDef(it, fileProvider) }
    val seriesIcon = seriesDef?.backgroundTexture?.let { fileProvider.loadGameFile(it)?.getExportOfTypeOrNull<UTexture2D>()?.toBufferedImage() }
    if (loadVariants) {
        this.variants.forEach { variants ->
            variants.variants.forEach {
                if (it.previewImage != null) {
                    val iconPkg = fileProvider.loadGameFile(it.previewImage!!)
                    it.previewIcon = iconPkg?.getExportOfTypeOrNull<UTexture2D>()?.toBufferedImage()
                }
            }
        }
    }
    var setText : FText? = null
    val setName = this.set
    if (setName != null)
        setText = ItemDefinitionInfo.sets[setName.text]
    return ItemDefinitionContainer(this, icon, rarity.rarityName.copy(), isFeatured, setText, seriesIcon, seriesDef)
}
data class SetName(val set : FText, val wrapper : FText = FText("Fort.Cosmetics", "CosmeticItemDescription_SetMembership", "Part of the <SetName>{0}</> set.")) {
    fun applyLocres(locres: Locres?) {
        set.applyLocres(locres)
        wrapper.applyLocres(locres)
    }

    val finalText : String
        get() = wrapper.text.replace("<SetName>{0}</>", set.text)
}
class ItemDefinitionContainer(val itemDefinition: ItemDefinition, var icon: BufferedImage, var rarityText : FText, var isFeaturedIcon : Boolean, setText : FText?, var seriesIcon : BufferedImage?, var seriesDef : FortItemSeriesDefinition?) : Cloneable {

    var setName = setText?.let { SetName(setText) }

    val variantsLoaded: Boolean
        get() = itemDefinition.variants.firstOrNull { variant -> variant.variants.firstOrNull { it.previewIcon != null } != null } != null

    fun getImage() = getImage(this)
    fun getImageWithVariants() = getImageWithVariants(this)
    fun getImageNoVariants() = getImageNoVariants(this)
    fun getShopFeaturedImage(price: Int) = getShopFeaturedImage(this, price)
    fun getShopDailyImage(price: Int) = getShopDailyImage(this, price)

    fun applyLocres(locres : Locres?) {
        itemDefinition.applyLocres(locres)
        setName?.applyLocres(locres)
        rarityText.applyLocres(locres)
    }
}

fun getImage(container: ItemDefinitionContainer): BufferedImage {
    return if (container.variantsLoaded)
        getImageWithVariants(container)
    else
        getImageNoVariants(container)
}

private const val variantsIconSize = 180
private const val variantsX = 500
//private const val variantsY = 350
private const val variantsSpaceBetween = 5
private const val variantsMaxPerRow = 7
private const val variantsBeginX = 11
private fun getImageWithVariants(container: ItemDefinitionContainer): BufferedImage {
    val itemDef = container.itemDefinition
    var icon = container.icon
    val vars = itemDef.variants
    if (icon.width != variantsIconSize || icon.height != variantsIconSize)
        icon = icon.scale(
            variantsIconSize,
            variantsIconSize
        )
    val rarity = itemDef.rarity.getVariantsBackgroundImage()
    val result = BufferedImage(rarity.width, rarity.height, BufferedImage.TYPE_INT_ARGB)
    val g = result.createGraphics()
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)

    if (!drawSeriesBackgroundColors(result, g, container, isFeaturedShop = false, isDailyShop = false)) {
        g.drawImage(rarity, 0, 0, null)
    }
    drawSeriesBackgroundImage(result, g, container, false)

    val burbank = Resources.burbank
    val notoSans = Resources.notoSans

    //Remove numeric and pattern channels (used for soccer skins, cannot be displayed properly)
    vars.removeIf { it.variantChannelTag?.text == "Cosmetics.Variant.Channel.Pattern" || it.variantChannelTag?.text == "Cosmetics.Variant.Channel.Numeric"
            || it.variantChannelTag?.text?.contains("PATTERN") == true || it.variantChannelTag?.text?.contains("NUMBER") == true}

    var numChannels = vars.size

    if (numChannels > 2) {
        numChannels = 2
        UEClass.logger.warn("Dropped ${numChannels - 2} cosmetic channel(s)")
    }
    val availableX = variantsX - (numChannels * g.fontMetrics.height)

    val totalRows = vars.sumBy {
        var count = it.variants.size
        if (count < variantsMaxPerRow)
            return@sumBy 1
        while (count % variantsMaxPerRow != 0)
            count++
        count / variantsMaxPerRow
    }
    val maxVarSize = (availableX - (numChannels - 1) * 10) / totalRows

    var cY = 35
    vars.forEach {
        val varCount = it.variants.size
        val channelName = it.variantChannelName?.text
        if (channelName != null) {
            g.font = burbank.deriveFont(25f)
            g.paint = Color.WHITE
            g.drawString(it.variantChannelName.text, variantsBeginX, cY + 20)
            cY += g.fontMetrics.height
        }
        var cX = variantsBeginX
        val perRow = min(variantsMaxPerRow, varCount)
        val varSize = if (maxVarSize * perRow + (perRow - 1) * variantsSpaceBetween > variantsX)
            (variantsX - (perRow - 1) * variantsSpaceBetween) / perRow
        else
            maxVarSize
        for (i in 0 until varCount) {
            val varContainer = it.variants[i]
            var varIcon = varContainer.previewIcon ?: continue
            if (varIcon.width != varSize || varIcon.height != varSize)
                varIcon = varIcon.scale(varSize, varSize)
            if (cX + varSize > variantsBeginX + variantsX) {
                cX = variantsBeginX
                cY += varSize + variantsSpaceBetween
            }
            //draw variant background with Color1
            val color1 = container.seriesDef?.colors?.get("Color1")
            if (color1 != null) {
                val r = color1.r.times(255).roundToInt()
                val gr = color1.g.times(255).roundToInt()
                val b = color1.b.times(255).roundToInt()
                g.paint = Color(r, gr, b, 70)
            }
            else g.paint = Color(255, 255, 255, 70)
            g.fillRect(cX, cY, varSize, varSize)
            g.drawImage(varIcon, cX, cY, null)

            val varName = varContainer.variantName?.text
            if (varName != null) {
                g.paint = Color(0, 0, 0, 100)
                g.fillRect(cX, cY + varSize - varSize / 4, varSize, varSize / 4)

                g.paint = Color.WHITE
                g.font = burbank.deriveFont((varSize / 4) / 1.2f)

                g.drawCenteredString(varName, cX + varSize / 2, cY + varSize - varSize / 4 + (varSize / 4 / 2 + g.fontMetrics.height) / 2)
                cX += varSize + variantsSpaceBetween
                if (cX > variantsX) {
                    cX = variantsBeginX
                    if (i + 1 < varCount)
                        cY += varSize + variantsSpaceBetween
                }
            }
        }
        cY += varSize + 10
    }

    g.drawImage(icon, result.width - 5 - variantsIconSize, result.height - 5 - variantsIconSize, null)

    val displayName = itemDef.displayName?.text?.toUpperCase()

    val rightX = result.width - 5 - variantsIconSize - 10
    val spaceForString = rightX - 5
    if (displayName != null) {
        g.color = Color.WHITE
        var fontSize = 50f
        g.font = burbank.deriveFont(Font.PLAIN, fontSize)
        var fm = g.fontMetrics
        while (fm.stringWidth(displayName) > spaceForString) {
            fontSize--
            g.font = burbank.deriveFont(Font.PLAIN, fontSize)
            fm = g.fontMetrics
        }
        g.drawString(displayName, rightX - fm.stringWidth(displayName), result.width - 52)
    }

    val description = itemDef.description?.text
    if (description != null) {
        g.color = Color.WHITE
        var fontSize: Float
        var fm: FontMetrics
        var y = result.height - 30
        var lines = description.split("\\r?\\n")
        val setText = container.setName?.finalText
        if (setText != null) {
            lines = lines.toMutableList().apply { this.add(setText) }
        }
        if (lines.size > 2) {
            lines = lines.subList(0, 2)
            UEClass.logger.warn("Dropped ${lines.size - 2} description line(s)")
        }

        if (lines.size == 1)
            y += 7
        lines.forEach {
            fontSize = 15f
            g.font = notoSans.deriveFont(Font.PLAIN, fontSize)
            fm = g.fontMetrics
            while (fm.stringWidth(it) > spaceForString) {
                fontSize--
                g.font = notoSans.deriveFont(Font.PLAIN, fontSize)
                fm = g.fontMetrics
            }
            g.drawString(it, rightX - fm.stringWidth(it), y)
            y += 18
        }
    }
    val userFacingFlag = itemDef.userFacingFlags?.text
    if (userFacingFlag != null && ItemDefinitionInfo.userFacingFlags.containsKey(userFacingFlag)) {
        var flagIcon = ItemDefinitionInfo.userFacingFlags[userFacingFlag]!!
        if (flagIcon.width != 45 || flagIcon.height != 45)
            flagIcon = flagIcon.scale(45, 45)
        g.drawImage(flagIcon, result.width - 10 - 45, 10, null)
    }
    return result
}

private val trackingAttr: Map<TextAttribute, Double> by lazy { mapOf(TextAttribute.TRACKING to 0.03) }
private const val barHeight = 160
private fun getImageNoVariants(container: ItemDefinitionContainer): BufferedImage {
    val itemDef = container.itemDefinition
    var icon = container.icon
    if (icon.width != 512 || icon.height != 512)
        icon = icon.scale(512, 512)
    val rarity = itemDef.rarity.getBackgroundImage()
    val result = BufferedImage(rarity.width, rarity.height, BufferedImage.TYPE_INT_ARGB)
    val g = result.createGraphics()
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)

    if (!drawSeriesBackgroundColors(result, g, container, isFeaturedShop = false, isDailyShop = false)) {
        g.drawImage(rarity, 0, 0, null)
    }
    drawSeriesBackgroundImage(result, g, container, false)

    g.drawImage(icon, 5, 5, null)

    val burbank = Resources.burbank
    val notoSans = Resources.notoSans

    g.paint = Color(0, 0, 0, 100)
    g.fillRect(5, 5 + 512 - barHeight, 512,
        barHeight
    )
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
        var fm: FontMetrics
        var y = icon.height - 50
        var lines = description.split("\\r?\\n")
        val setText = container.setName?.finalText
        if (setText != null) {
            lines = lines.toMutableList().apply { this.add(setText) }
        }
        if (lines.size > 2) {
            lines = lines.subList(0, 2)
            UEClass.logger.warn("Dropped ${lines.size - 2} description line(s)")
        }

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

    val userFacingFlag = itemDef.userFacingFlags?.text
    if (userFacingFlag != null && ItemDefinitionInfo.userFacingFlags.containsKey(userFacingFlag)) {
        var flagIcon = ItemDefinitionInfo.userFacingFlags[userFacingFlag]!!
        if (flagIcon.width != 64 || flagIcon.height != 64)
            flagIcon = flagIcon.scale(64, 64)
        g.drawImage(flagIcon, 10, 10, null)
    }
    g.dispose()
    return result
}

private const val featuredAdditionalHeight = 200
private const val featuredBarHeight = 131
private fun getShopFeaturedImage(container: ItemDefinitionContainer, price: Int): BufferedImage {
    val itemDef = container.itemDefinition
    var icon = container.icon
    if (icon.width != 1024 || icon.height != 1024)
        icon = icon.scale(1024, 1024)
    val rarity = itemDef.rarity.getFeaturedShopBackgroundImage()
    val result = BufferedImage(rarity.width, rarity.height, BufferedImage.TYPE_INT_ARGB)
    val g = result.createGraphics()
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)

    if (!drawSeriesBackgroundColors(result, g, container, isFeaturedShop = true, isDailyShop = false)) {
        g.drawImage(rarity, 0, 0, null)
    }

    drawSeriesBackgroundImage(result, g, container, true)
    g.drawImage(icon.cut(result.width - 22), 11, 11, null)

    g.paint = Color(0, 0, 0, 100)
    g.fillRect(11, 11 + icon.height - featuredAdditionalHeight, result.width - 22,
        featuredAdditionalHeight
    )

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
        //draw series name with Color1
        val color1 = container.seriesDef?.colors?.get("Color1")
        val seriesDisplayName = container.seriesDef?.displayName
        if (color1 != null && seriesDisplayName != null) {
            val r = color1.r.times(255).roundToInt()
            val gr = color1.g.times(255).roundToInt()
            val b = color1.b.times(255).roundToInt()
            g.color = Color(r, gr,b)
            g.font = notoSans.deriveFont(Font.BOLD, fontSize).deriveFont(trackingAttr)
            val stringWidth = g.fontMetrics.stringWidth(seriesDisplayName.text)
            val serieX = (result.width / 2 - (stringWidth / 2)) + 20
            g.drawCenteredString(seriesDisplayName.text.toUpperCase(), serieX, icon.height - 20)
            g.color = Color.LIGHT_GRAY
            g.font = notoSans.deriveFont(Font.PLAIN, fontSize).deriveFont(trackingAttr)
            g.drawString(shortDesc, serieX + 60 + (stringWidth / 2), icon.height - 20)
        } else g.drawCenteredString(shortDesc, result.width / 2, icon.height - 20)
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
private fun getShopDailyImage(container: ItemDefinitionContainer, price: Int): BufferedImage {
    val itemDef = container.itemDefinition
    var icon = container.icon
    if (icon.width != 512 || icon.height != 512)
        icon = icon.scale(512, 512)
    val rarity = itemDef.rarity.getDailyShopBackgroundImage()
    val result = BufferedImage(rarity.width, rarity.height, BufferedImage.TYPE_INT_ARGB)
    val g = result.createGraphics()
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
    if (!drawSeriesBackgroundColors(result, g, container, isFeaturedShop = false, isDailyShop = true)) {
        g.drawImage(rarity, 0, 0, null)
    }
    drawSeriesBackgroundImage(result, g, container, false)
    g.drawImage(icon, 5, 5, null)

    val burbank = Resources.burbank
    val notoSans = Resources.notoSans
    val notoSansBold = Resources.notoSansBold

    g.paint = Color(0, 0, 0, 100)
    g.fillRect(5, 5 + 512 - dailyAdditionalHeight, 512,
        dailyAdditionalHeight
    )

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
        var fontSize = 30f
        g.font = notoSans.deriveFont(Font.PLAIN, fontSize).deriveFont(trackingAttr)
        var fm = g.fontMetrics
        while (fm.stringWidth(shortDesc) > result.width - 10) {
            fontSize--
            g.font = notoSans.deriveFont(Font.PLAIN, fontSize).deriveFont(trackingAttr)
            fm = g.fontMetrics
        }


        //draw series name with Color1
        val color1 = container.seriesDef?.colors?.get("Color1")
        val seriesDisplayName = container.seriesDef?.displayName
        if (color1 != null && seriesDisplayName != null) {
            val r = color1.r.times(255).roundToInt()
            val gr = color1.g.times(255).roundToInt()
            val b = color1.b.times(255).roundToInt()
            g.color = Color(r, gr,b)
            g.font = notoSans.deriveFont(Font.BOLD, fontSize).deriveFont(trackingAttr)
            val stringWidth = g.fontMetrics.stringWidth(seriesDisplayName.text)
            val serieX = (result.width / 2 - (stringWidth / 2)) + 20
            g.drawCenteredString(seriesDisplayName.text.toUpperCase(), serieX, icon.height - 20)
            g.color = Color.LIGHT_GRAY
            g.font = notoSans.deriveFont(Font.PLAIN, fontSize).deriveFont(trackingAttr)
            g.drawString(shortDesc, serieX + 40 + (stringWidth / 2), icon.height - 20)
        } else g.drawCenteredString(shortDesc, result.width / 2, icon.height - 20)
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

private val numberFormatter: NumberFormat by lazy { NumberFormat.getNumberInstance(Locale.US) }
private fun printPrice(price: Int) = numberFormatter.format(price)

fun loadFeaturedIcon(itemDefinition: ItemDefinition, fileProvider: FileProvider): BufferedImage? {
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

fun loadNormalIcon(itemDefinition: ItemDefinition, fileProvider: FileProvider): BufferedImage? {
    if (itemDefinition.hasIcons) {
        val iconPkg = fileProvider.loadGameFile(itemDefinition.largePreviewImage!!)
        val icon = iconPkg?.getExportOfType<UTexture2D>()?.toBufferedImage()
        if (icon != null)
            return icon
    }
    if (itemDefinition.usesHeroDefinition) {
        val heroDefPkg = fileProvider.loadGameFile(itemDefinition.heroDefinitionPackage!!)
        val largePreviewImage = heroDefPkg?.getExportOfTypeOrNull<ItemDefinition>()?.largePreviewImage
        if (largePreviewImage != null) {
            val iconPkg = fileProvider.loadGameFile(largePreviewImage)
            val icon = iconPkg?.getExportOfType<UTexture2D>()?.toBufferedImage()
            if (icon != null)
                return icon
        }
    }
    if (itemDefinition.usesWeaponDefinition) {
        val weaponDefPkg = fileProvider.loadGameFile(itemDefinition.weaponDefinitionPackage!!)
        val largePreviewImage = weaponDefPkg?.getExportOfTypeOrNull<ItemDefinition>()?.largePreviewImage
        if (largePreviewImage != null) {
            val iconPkg = fileProvider.loadGameFile(largePreviewImage)
            val icon = iconPkg?.getExportOfType<UTexture2D>()?.toBufferedImage()
            if (icon != null)
                return icon
        }
    }
    return null
}

private fun loadSeriesDef(index : FPackageIndex, fileProvider: FileProvider) = fileProvider.loadGameFile(index)?.getExportOfTypeOrNull<FortItemSeriesDefinition>()

private fun drawSeriesBackgroundColors(result : BufferedImage, g : Graphics2D, container: ItemDefinitionContainer, isFeaturedShop : Boolean, isDailyShop : Boolean) : Boolean {
    val seriesDef = container.seriesDef ?: return false
    val color1 = seriesDef.colors["Color1"] ?: return false
    val color2 = seriesDef.colors["Color2"] ?: return false
    val color3 = seriesDef.colors["Color3"] ?: return false

    val r = color1.r.times(255).roundToInt()
    val gr = color1.g.times(255).roundToInt()
    val b = color1.b.times(255).roundToInt()
    val r2 = color2.r.times(255).roundToInt()
    val gr2 = color2.g.times(255).roundToInt()
    val b2 = color2.b.times(255).roundToInt()
    val r3 = color3.r.times(255).roundToInt()
    val gr3 = color3.g.times(255).roundToInt()
    val b3 = color3.b.times(255).roundToInt()

    //gradient borders
    val baseT = g.transform
    val tf = AffineTransform.getTranslateInstance((-result.width / 2).toDouble(), (-result.height / 2).toDouble())
    tf.preConcatenate(AffineTransform.getRotateInstance(Math.toRadians(180.0 + 45.0)))
    tf.preConcatenate(AffineTransform.getTranslateInstance((result.width / 2).toDouble(), (result.height / 2).toDouble()))
    g.transform = tf
    g.paint = GradientPaint(0F, 0F, Color(r2, gr2, b2), 0F, (result.height + result.width).toFloat(), Color(r, gr, b), false)
    g.fillRect(-result.width / 2, -result.height / 2, result.width * 2, result.height * 2)
    g.transform = baseT

    //actual background (you see it with the marvel rarity)
    g.paint = Color(r3, gr3, b3)
    if (isFeaturedShop)
        g.fillRect(10, 10, result.width - 20, result.height - 20)
    else
        g.fillRect(5, 5, result.width - 10, result.height - 10)

    if (isFeaturedShop) {
        g.paint = Color(0, 7, 36, 255)
        g.fillRect(10, result.height - 20 - 122, result.width - 20, 132)
    } else if(isDailyShop) {
        g.paint = Color(0, 7, 36, 255)
        g.fillRect(5, result.height - 10 - 78, result.width - 10, 83)
    }
    return true
}

private fun drawSeriesBackgroundImage(result: BufferedImage, g: Graphics2D, container: ItemDefinitionContainer, isFeatured : Boolean) : Boolean {
    val seriesIcon = container.seriesIcon ?: return false
    if (isFeatured)
        g.drawImage(seriesIcon.scale(result.width - 20, result.height - 20 - 132), 10, 10, null)
    else
        g.drawImage(seriesIcon, 5, 5, null)
    return true
}
