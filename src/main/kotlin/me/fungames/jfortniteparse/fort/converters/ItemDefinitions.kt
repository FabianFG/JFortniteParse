package me.fungames.jfortniteparse.fort.converters

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.fileprovider.FileProvider
import me.fungames.jfortniteparse.fort.*
import me.fungames.jfortniteparse.fort.exports.*
import me.fungames.jfortniteparse.fort.exports.FortMtxOfferData.EFortMtxOfferDisplaySize
import me.fungames.jfortniteparse.fort.exports.variants.FortCosmeticVariantBackedByArray
import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.exports.UDataTable
import me.fungames.jfortniteparse.ue4.assets.exports.tex.UTexture2D
import me.fungames.jfortniteparse.ue4.converters.textures.toBufferedImage
import me.fungames.jfortniteparse.ue4.locres.Locres
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex
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

    private const val setObjectPath = "/Game/Athena/Items/Cosmetics/Metadata/CosmeticSets.CosmeticSets"
    private fun loadSets(fileProvider: FileProvider): Boolean {
        val table = fileProvider.loadObject<UDataTable>(setObjectPath) ?: return false
        return try {
            table.rows.forEach { (name, row) ->
                row.getOrNull<FText>("DisplayName")?.apply { sets.putIfAbsent(name.text, this) }
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    private const val itemCategoriesObjectPath = "/Game/Items/ItemCategories.ItemCategories"
    private fun loadUserFacingFlags(fileProvider: FileProvider): Boolean {
        val itemCategory = fileProvider.loadObject<FortItemCategory>(itemCategoriesObjectPath) ?: return false
        return try {
            val userFacingFlags = mutableMapOf<String, Pair<FText, FPackageIndex>>()
            for (category in itemCategory.TertiaryCategories!!) {
                val tags = category.TagContainer?.gameplayTags
                if (tags != null && tags.any { it.text.startsWith("Cosmetics.UserFacingFlags") }) {
                    val name = category.CategoryName
                    val brush = category.CategoryBrush
                    val slateBrush = brush?.run { Brush_XL ?: Brush_L ?: Brush_M ?: Brush_S ?: Brush_XS ?: Brush_XXS } ?: continue
                    val pair: Pair<FText, FPackageIndex> = name!! to slateBrush.ResourceObject!!
                    tags.forEach {
                        val flag: String = it.text
                        if (flag.startsWith("Cosmetics.UserFacingFlags"))
                            userFacingFlags[flag] = pair
                    }
                }
            }
            val requiredImgs = mutableMapOf<FPackageIndex, BufferedImage>()
            userFacingFlags.forEach { _, (_, index) ->
                if (!requiredImgs.containsKey(index)) {
                    index.load<UTexture2D>()?.apply { requiredImgs[index] = toBufferedImage() }
                }
            }
            userFacingFlags.forEach { flag, (_, index) ->
                this.userFacingFlags.putIfAbsent(flag, requiredImgs[index]!!)
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}

@Throws(ParserException::class)
fun FortItemDefinition.createContainer(
    fileProvider: FileProvider = owner?.provider!!,
    loadVariants: Boolean = true, failOnNoIconLoaded: Boolean = false, overrideIcon: BufferedImage? = null
): ItemDefinitionContainer {
    ItemDefinitionInfo.init(fileProvider)
    var icon = overrideIcon
    var isFeatured = false
    if (icon == null) {
        icon = loadFeaturedIcon(this)
        if (icon != null)
            isFeatured = true
        else
            icon = loadNormalIcon(this)
        if (icon == null) {
            if (failOnNoIconLoaded)
                throw ParserException("Failed to load icon")
            else
                icon = FortResources.fallbackIcon
        }
    }
    val seriesDef = Series?.load<FortItemSeriesDefinition>()
    val seriesIcon = seriesDef?.BackgroundTexture?.load<UTexture2D>()?.toBufferedImage()
    return ItemDefinitionContainer(this, icon, Rarity.rarityName.copy(), isFeatured, set?.run { ItemDefinitionInfo.sets[text] }?.run { SetName(this) }, seriesIcon, seriesDef)
}

open class SetName(
    val set: FText,
    val wrapper: FText = FText("Fort.Cosmetics", "CosmeticItemDescription_SetMembership", "Part of the <SetName>{0}</> set.")
) {
    open fun finalTextForLocres(locres: Locres?) = wrapper.textForLocres(locres).replace("<SetName>{0}</>", set.textForLocres(locres))
}

class ItemDefinitionContainer(val itemDefinition: FortItemDefinition,
                              var icon: BufferedImage,
                              var rarityText: FText,
                              var isFeaturedIcon: Boolean,
                              val setName: SetName?,
                              var seriesIcon: BufferedImage?,
                              var seriesDef: FortItemSeriesDefinition?) : Cloneable {
    val variantsLoaded: Boolean
        get() = itemDefinition is AthenaCosmeticItemDefinition && itemDefinition.ItemVariants.firstOrNull { channel ->
            channel is FortCosmeticVariantBackedByArray && channel.variants?.firstOrNull { variantDef ->
                variantDef.PreviewImage != null && !variantDef.PreviewImage.assetPathName.isNone()
            } != null
        } != null

    fun getImage(locres: Locres? = null) = getImage(this, locres)
    fun getImageWithVariants(locres: Locres? = null) = getImageWithVariants(this, locres)
    fun getImageNoVariants(locres: Locres? = null) = getImageNoVariants(this, locres)
    fun getShopFeaturedImage(price: Int, locres: Locres? = null) = getShopImage(this, EFortMtxOfferDisplaySize.Medium, price, locres)
    fun getShopDailyImage(price: Int, locres: Locres? = null) = getShopImage(this, EFortMtxOfferDisplaySize.Small, price, locres)
}

fun getImage(container: ItemDefinitionContainer, locres: Locres?): BufferedImage {
    return if (container.variantsLoaded)
        getImageWithVariants(container, locres)
    else
        getImageNoVariants(container, locres)
}

private const val variantsIconSize = 180
private const val variantsX = 500
//private const val variantsY = 350
private const val variantsSpaceBetween = 5
private const val variantsMaxPerRow = 7
private const val variantsBeginX = 11
private fun getImageWithVariants(container: ItemDefinitionContainer, locres: Locres?): BufferedImage {
    val itemDef = container.itemDefinition as AthenaCosmeticItemDefinition
    var icon = container.icon
    //Don't include numeric and pattern channels (used for soccer skins, cannot be displayed properly)
    val vars = itemDef.ItemVariants.filterIsInstance<FortCosmeticVariantBackedByArray>()
    if (icon.width != variantsIconSize || icon.height != variantsIconSize)
        icon = icon.scale(
            variantsIconSize,
            variantsIconSize
        )
    val rarity = itemDef.Rarity.getVariantsBackgroundImage()
    val result = BufferedImage(rarity.width, rarity.height, BufferedImage.TYPE_INT_RGB)
    val g = result.createGraphics()
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)

    if (!drawSeriesBackgroundColors(result, g, container, isFeaturedShop = false, isDailyShop = false)) {
        g.drawImage(rarity, 0, 0, null)
    }
    drawSeriesBackgroundImage(result, g, container, false)

    val burbank = FortResources.burbank
    val notoSans = FortResources.notoSans

    var numChannels = vars.size

    if (numChannels > 2) {
        numChannels = 2
        UClass.logger.warn("Dropped ${numChannels - 2} cosmetic channel(s)")
    }
    val availableX = variantsX - (numChannels * g.fontMetrics.height)

    val totalRows = vars.sumBy {
        var count = it.variants?.size ?: 0
        if (count < variantsMaxPerRow)
            return@sumBy 1
        while (count % variantsMaxPerRow != 0)
            count++
        count / variantsMaxPerRow
    }.coerceAtLeast(1)
    val maxVarSize = (availableX - (numChannels - 1) * 10) / totalRows

    var cY = 35
    vars.forEach {
        val varCount = it.variants?.size ?: 0
        val channelName = it.VariantChannelName?.textForLocres(locres)
        if (channelName != null) {
            g.font = burbank.deriveFont(25f)
            g.paint = Color.WHITE
            g.drawString(channelName, variantsBeginX, cY + 20)
            cY += g.fontMetrics.height
        }
        var cX = variantsBeginX
        val perRow = min(variantsMaxPerRow, varCount)
        val varSize = if (maxVarSize * perRow + (perRow - 1) * variantsSpaceBetween > variantsX)
            (variantsX - (perRow - 1) * variantsSpaceBetween) / perRow
        else
            maxVarSize
        for (i in 0 until varCount) {
            val varContainer = it.variants?.get(i)
            var varIcon = varContainer?.PreviewImage?.load<UTexture2D>()?.toBufferedImage() ?: continue
            if (varIcon.width != varSize || varIcon.height != varSize)
                varIcon = varIcon.scale(varSize, varSize)
            if (cX + varSize > variantsBeginX + variantsX) {
                cX = variantsBeginX
                cY += varSize + variantsSpaceBetween
            }
            //draw variant background with Color1
            val color1 = container.seriesDef?.Colors?.Color1?.toColor()
            if (color1 != null)
                g.paint = Color(color1.red, color1.green, color1.blue, 70)
            else g.paint = Color(255, 255, 255, 70)
            g.fillRect(cX, cY, varSize, varSize)
            g.drawImage(varIcon, cX, cY, null)

            val varName = varContainer.VariantName?.textForLocres(locres)
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

    val displayName = itemDef.DisplayName?.textForLocres(locres)?.toUpperCase()

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

    val description = itemDef.Description?.textForLocres(locres)
    if (description != null) {
        g.color = Color.WHITE
        var fontSize: Float
        var fm: FontMetrics
        var y = result.height - 30
        var lines = description.split("\\r?\\n")
        val setText = container.setName?.finalTextForLocres(locres)
        if (setText != null) {
            lines = lines.toMutableList().apply { this.add(setText) }
        }
        if (lines.size > 2) {
            lines = lines.subList(0, 2)
            UClass.logger.warn("Dropped ${lines.size - 2} description line(s)")
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
private fun getImageNoVariants(container: ItemDefinitionContainer, locres: Locres?): BufferedImage {
    val itemDef = container.itemDefinition
    var icon = container.icon
    if (icon.width != 512 || icon.height != 512)
        icon = icon.scale(512, 512)
    val rarity = itemDef.Rarity.getBackgroundImage()
    val result = BufferedImage(rarity.width, rarity.height, BufferedImage.TYPE_INT_RGB)
    val g = result.createGraphics()
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)

    if (!drawSeriesBackgroundColors(result, g, container, isFeaturedShop = false, isDailyShop = false)) {
        g.drawImage(rarity, 0, 0, null)
    }
    drawSeriesBackgroundImage(result, g, container, false)

    g.drawImage(icon, 5, 5, null)

    val burbank = FortResources.burbank
    val notoSans = FortResources.notoSans

    g.paint = Color(0, 0, 0, 100)
    g.fillRect(5, 5 + 512 - barHeight, 512, barHeight)
    g.font = burbank
    val displayName = itemDef.DisplayName?.textForLocres(locres)?.toUpperCase()
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
    val description = itemDef.Description?.textForLocres(locres)
    if (description != null) {
        g.color = Color.LIGHT_GRAY
        var fontSize: Float
        var fm: FontMetrics
        var y = icon.height - 50
        var lines = description.split("\\r?\\n")
        val setText = container.setName?.finalTextForLocres(locres)
        if (setText != null) {
            lines = lines.toMutableList().apply { this.add(setText) }
        }
        if (lines.size > 2) {
            lines = lines.subList(0, 2)
            UClass.logger.warn("Dropped ${lines.size - 2} description line(s)")
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
private const val dailyAdditionalHeight = 160
private const val dailyBarHeight = 83
private fun getShopImage(container: ItemDefinitionContainer, size: EFortMtxOfferDisplaySize, price: Int, locres: Locres?): BufferedImage {
    val large = size != EFortMtxOfferDisplaySize.Small
    val itemDef = container.itemDefinition
    var icon = container.icon
    val iconSize = if (large) 1024 else 512
    if (icon.width != iconSize || icon.height != iconSize)
        icon = icon.scale(iconSize, iconSize)
    val rarity = if (large) itemDef.Rarity.getFeaturedShopBackgroundImage() else itemDef.Rarity.getDailyShopBackgroundImage()
    val result = BufferedImage(rarity.width, rarity.height, BufferedImage.TYPE_INT_RGB)
    val g = result.createGraphics()
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
    if (!drawSeriesBackgroundColors(result, g, container, large, !large)) {
        g.drawImage(rarity, 0, 0, null)
    }
    drawSeriesBackgroundImage(result, g, container, true)
    if (large)
        g.drawImage(icon.cut(result.width - 22), 11, 11, null)
    else
        g.drawImage(icon, 5, 5, null)

    g.paint = Color(0, 0, 0, 100)
    if (large)
        g.fillRect(11, 11 + icon.height - featuredAdditionalHeight, result.width - 22, featuredAdditionalHeight)
    else
        g.fillRect(5, 5 + 512 - dailyAdditionalHeight, 512, dailyAdditionalHeight)

    val burbank = FortResources.burbank
    val notoSans = FortResources.notoSans
    val notoSansBold = FortResources.notoSansBold

    val displayName = itemDef.DisplayName?.textForLocres(locres)?.toUpperCase()
    if (displayName != null) {
        g.color = Color.WHITE
        var fontSize = if (large) 80f else 60f
        g.font = burbank.deriveFont(Font.PLAIN, fontSize).deriveFont(trackingAttr)
        var fm = g.fontMetrics
        while (fm.stringWidth(displayName) > result.width - 10) {
            fontSize--
            g.font = burbank.deriveFont(Font.PLAIN, fontSize).deriveFont(trackingAttr)
            fm = g.fontMetrics
        }
        g.drawCenteredString(displayName, result.width / 2, icon.height - (if (large) 95 else 85))
    }

    val shortDesc = itemDef.ShortDescription?.textForLocres(locres)
    if (shortDesc != null) {
        g.color = Color.LIGHT_GRAY
        var fontSize = if (large) 40f else 30f
        g.font = notoSans.deriveFont(Font.PLAIN, fontSize).deriveFont(trackingAttr)
        var fm = g.fontMetrics
        while (fm.stringWidth(shortDesc) > result.width - 10) {
            fontSize--
            g.font = notoSans.deriveFont(Font.PLAIN, fontSize).deriveFont(trackingAttr)
            fm = g.fontMetrics
        }

        //draw series name with Color1
        val seriesDisplayName = container.seriesDef?.DisplayName?.textForLocres(locres)?.toUpperCase()
        if (seriesDisplayName != null) {
            g.color = container.seriesDef?.Colors?.Color1?.toColor() ?: Color.WHITE
            g.font = notoSansBold.deriveFont(Font.PLAIN, fontSize)
            val stringWidth = g.fontMetrics.stringWidth(seriesDisplayName)
            val serieX = (result.width / 2 - (stringWidth / 2)) + 20
            g.drawCenteredString(seriesDisplayName, serieX, icon.height - 20)
            g.color = Color.LIGHT_GRAY
            g.font = notoSans.deriveFont(Font.PLAIN, fontSize).deriveFont(trackingAttr)
            g.drawString(shortDesc, serieX + (if (large) 60 else 40) + (stringWidth / 2), icon.height - 20)
        } else g.drawCenteredString(shortDesc, result.width / 2, icon.height - 20)
    }

    g.font = notoSansBold.deriveFont(Font.PLAIN, if (large) 60f else 45f)
    g.color = Color.WHITE
    val fm = g.fontMetrics
    val vbucks = FortResources.vbucksIcon
    val priceS = printPrice(price, locres)

    if (large) {
        val vbucksX = result.width / 2 - fm.stringWidth(priceS) / 2 - vbucks.width / 2
        val vbucksY = result.height - 11 - featuredBarHeight / 2 - vbucks.height / 2
        g.drawImage(vbucks, vbucksX, vbucksY, null)
        g.drawString(priceS, vbucksX + vbucks.width + 12, vbucksY + fm.height - 27)
    } else {
        val vbucksX = result.width / 2 - fm.stringWidth(priceS) / 2 - vbucks.width / 2 - 20
        val vbucksY = result.height - 11 - dailyBarHeight / 2 - vbucks.height / 2 + 5
        g.drawImage(vbucks, vbucksX, vbucksY, null)
        g.drawString(priceS, vbucksX + vbucks.width + 12, vbucksY + fm.height - 10)
    }

    return result
}

private fun printPrice(price: Int, locres: Locres?) = NumberFormat.getNumberInstance(locres?.language?.run { Locale(languageCode) } ?: Locale.US).format(price)

fun loadFeaturedIcon(itemDefinition: FortItemDefinition): BufferedImage? =
    itemDefinition.DisplayAssetPath?.load<FortMtxOfferData>()?.run {
        val resource = DetailsImage?.ResourceObject
        if (resource == null || resource.isNull())
            return null
        /*resource.outerImportObject?.objectName?.text?.apply {
            if (contains("Athena/Prototype/Textures") || contains("Placeholder"))
                return null
        }*/
        // Some display assets use MaterialInstanceConstant for DetailsImage, so we only load it if it's Texture2D
        return resource.load<UTexture2D>()?.toBufferedImage()
    }

fun loadNormalIcon(itemDefinition: FortItemDefinition): BufferedImage? {
    (itemDefinition.LargePreviewImage ?: (itemDefinition as? AthenaEmojiItemDefinition)?.SpriteSheet)?.load<UTexture2D>()?.apply { return toBufferedImage() }
    (itemDefinition as? AthenaCharacterItemDefinition)?.HeroDefinition?.load<FortItemDefinition>()?.LargePreviewImage?.load<UTexture2D>()?.apply { return toBufferedImage() }
    (itemDefinition as? AthenaPickaxeItemDefinition)?.WeaponDefinition?.load<FortItemDefinition>()?.LargePreviewImage?.load<UTexture2D>()?.apply { return toBufferedImage() }
    return null
}

private fun drawSeriesBackgroundColors(result: BufferedImage, g: Graphics2D, container: ItemDefinitionContainer, isFeaturedShop: Boolean, isDailyShop: Boolean): Boolean {
    val seriesDef = container.seriesDef ?: return false
    val color1 = seriesDef.Colors?.Color1?.toColor() ?: return false
    val color2 = seriesDef.Colors?.Color2?.toColor() ?: return false
    val color3 = seriesDef.Colors?.Color3?.toColor() ?: return false

    //gradient borders
    val baseT = g.transform
    val tf = AffineTransform.getTranslateInstance((-result.width / 2).toDouble(), (-result.height / 2).toDouble())
    tf.preConcatenate(AffineTransform.getRotateInstance(Math.toRadians(180.0 + 45.0)))
    tf.preConcatenate(AffineTransform.getTranslateInstance((result.width / 2).toDouble(), (result.height / 2).toDouble()))
    g.transform = tf
    g.paint = GradientPaint(0F, 0F, color2, 0F, (result.height + result.width).toFloat(), color1, false)
    g.fillRect(-result.width / 2, -result.height / 2, result.width * 2, result.height * 2)
    g.transform = baseT

    //actual background (you see it with the marvel rarity)
    g.paint = color3
    if (isFeaturedShop)
        g.fillRect(10, 10, result.width - 20, result.height - 20)
    else
        g.fillRect(5, 5, result.width - 10, result.height - 10)

    if (isFeaturedShop) {
        g.paint = Color(0, 7, 36, 255)
        g.fillRect(10, result.height - 20 - 122, result.width - 20, 132)
    } else if (isDailyShop) {
        g.paint = Color(0, 7, 36, 255)
        g.fillRect(5, result.height - 10 - 78, result.width - 10, 83)
    }
    return true
}

private fun drawSeriesBackgroundImage(result: BufferedImage, g: Graphics2D, container: ItemDefinitionContainer, isFeatured: Boolean): Boolean {
    val seriesIcon = container.seriesIcon ?: return false
    if (isFeatured)
        g.drawImage(seriesIcon.scale(result.width - 20, result.height - 20 - 132), 10, 10, null)
    else
        g.drawImage(seriesIcon, 5, 5, null)
    return true
}
