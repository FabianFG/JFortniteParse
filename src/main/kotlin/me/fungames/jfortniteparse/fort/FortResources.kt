@file:Suppress("EXPERIMENTAL_API_USAGE")

package me.fungames.jfortniteparse.fort

import me.fungames.jfortniteparse.fort.enums.EFortRarity
import java.awt.Font
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

object FortResources {
    // Common Rarity
    val commonBackground : BufferedImage by lazy { ImageIO.read(FortResources.javaClass.getResourceAsStream("/icons/C512.png"))
        ?: throw IllegalStateException("Failed to load a resource") }
    val commonVariantsBackground : BufferedImage by lazy { ImageIO.read(FortResources.javaClass.getResourceAsStream("/icons/vC512.png"))
        ?: throw IllegalStateException("Failed to load a resource") }
    val commonDailyShopBackground : BufferedImage by lazy { ImageIO.read(FortResources.javaClass.getResourceAsStream("/icons/CDail.png"))
        ?: throw IllegalStateException("Failed to load a resource") }
    val commonFeaturedShopBackground : BufferedImage by lazy { ImageIO.read(FortResources.javaClass.getResourceAsStream("/icons/CFeat.png"))
        ?: throw IllegalStateException("Failed to load a resource") }

    // Uncommon Rarity
    val uncommonBackground : BufferedImage by lazy { ImageIO.read(FortResources.javaClass.getResourceAsStream("/icons/U512.png"))
        ?: throw IllegalStateException("Failed to load a resource") }
    val uncommonVariantsBackground : BufferedImage by lazy { ImageIO.read(FortResources.javaClass.getResourceAsStream("/icons/vU512.png"))
        ?: throw IllegalStateException("Failed to load a resource") }
    val uncommonDailyShopBackground : BufferedImage by lazy { ImageIO.read(FortResources.javaClass.getResourceAsStream("/icons/UDail.png"))
        ?: throw IllegalStateException("Failed to load a resource") }
    val uncommonFeaturedShopBackground : BufferedImage by lazy { ImageIO.read(FortResources.javaClass.getResourceAsStream("/icons/UFeat.png"))
        ?: throw IllegalStateException("Failed to load a resource") }

    // Rare Rarity
    val rareBackground : BufferedImage by lazy { ImageIO.read(FortResources.javaClass.getResourceAsStream("/icons/R512.png"))
        ?: throw IllegalStateException("Failed to load a resource") }
    val rareVariantsBackground : BufferedImage by lazy { ImageIO.read(FortResources.javaClass.getResourceAsStream("/icons/vR512.png"))
        ?: throw IllegalStateException("Failed to load a resource") }
    val rareDailyShopBackground : BufferedImage by lazy { ImageIO.read(FortResources.javaClass.getResourceAsStream("/icons/RDail.png"))
        ?: throw IllegalStateException("Failed to load a resource") }
    val rareFeaturedShopBackground : BufferedImage by lazy { ImageIO.read(FortResources.javaClass.getResourceAsStream("/icons/RFeat.png"))
        ?: throw IllegalStateException("Failed to load a resource") }

    // Epic Rarity
    val epicBackground : BufferedImage by lazy { ImageIO.read(FortResources.javaClass.getResourceAsStream("/icons/E512.png"))
        ?: throw IllegalStateException("Failed to load a resource") }
    val epicVariantsBackground : BufferedImage by lazy { ImageIO.read(FortResources.javaClass.getResourceAsStream("/icons/vE512.png"))
        ?: throw IllegalStateException("Failed to load a resource") }
    val epicDailyShopBackground : BufferedImage by lazy { ImageIO.read(FortResources.javaClass.getResourceAsStream("/icons/EDail.png"))
        ?: throw IllegalStateException("Failed to load a resource") }
    val epicFeaturedShopBackground : BufferedImage by lazy { ImageIO.read(FortResources.javaClass.getResourceAsStream("/icons/EFeat.png"))
        ?: throw IllegalStateException("Failed to load a resource") }

    // Legendary Rarity
    val legendaryBackground : BufferedImage by lazy { ImageIO.read(FortResources.javaClass.getResourceAsStream("/icons/L512.png"))
        ?: throw IllegalStateException("Failed to load a resource") }
    val legendaryVariantsBackground : BufferedImage by lazy { ImageIO.read(FortResources.javaClass.getResourceAsStream("/icons/vL512.png"))
        ?: throw IllegalStateException("Failed to load a resource") }
    val legendaryDailyShopBackground : BufferedImage by lazy { ImageIO.read(FortResources.javaClass.getResourceAsStream("/icons/LDail.png"))
        ?: throw IllegalStateException("Failed to load a resource") }
    val legendaryFeaturedShopBackground : BufferedImage by lazy { ImageIO.read(FortResources.javaClass.getResourceAsStream("/icons/LFeat.png"))
        ?: throw IllegalStateException("Failed to load a resource") }

    // Mythic Rarity
    val mythicBackground : BufferedImage by lazy { ImageIO.read(FortResources.javaClass.getResourceAsStream("/icons/M512.png"))
        ?: throw IllegalStateException("Failed to load a resource") }
    //val mythicVariantsBackground : BufferedImage by lazy { ImageIO.read(Resources.javaClass.getResourceAsStream("/icons/vM512.png"))
    //    ?: throw IllegalStateException("Failed to load a resource") }
    val mythicDailyShopBackground : BufferedImage by lazy { ImageIO.read(FortResources.javaClass.getResourceAsStream("/icons/MDail.png"))
        ?: throw IllegalStateException("Failed to load a resource") }
    val mythicFeaturedShopBackground : BufferedImage by lazy { ImageIO.read(FortResources.javaClass.getResourceAsStream("/icons/MFeat.png"))
        ?: throw IllegalStateException("Failed to load a resource") }

    // Fallback Icon
    val fallbackIcon : BufferedImage by lazy { ImageIO.read(FortResources.javaClass.getResourceAsStream("/icons/NoIcon.png"))
        ?: throw IllegalStateException("Failed to load a resource") }

    // VBucks Icon
    val vbucksIcon : BufferedImage by lazy { ImageIO.read(FortResources.javaClass.getResourceAsStream("/icons/vbucks.png"))
        ?: throw IllegalStateException("Failed to load a resource") }

    //Fonts
    val burbank : Font by lazy { Font.createFont(Font.TRUETYPE_FONT, FortResources.javaClass.getResourceAsStream("/fonts/BurbankBigCondensed-Black.ttf")) }
    val notoSans : Font by lazy { Font.createFont(Font.TRUETYPE_FONT, FortResources.javaClass.getResourceAsStream("/fonts/NotoSans-Regular.ttf")) }
    val notoSansBold : Font by lazy { Font.createFont(Font.TRUETYPE_FONT, FortResources.javaClass.getResourceAsStream("/fonts/NotoSans-Bold.ttf")) }
}

fun EFortRarity.getBackgroundImage() = when(this) {
    EFortRarity.Masterwork -> FortResources.mythicBackground
    EFortRarity.Transcendent -> FortResources.mythicBackground
    EFortRarity.Elegant -> FortResources.mythicBackground
    EFortRarity.Mythic -> FortResources.mythicBackground
    EFortRarity.Fine -> FortResources.legendaryBackground
    EFortRarity.Legendary -> FortResources.legendaryBackground
    EFortRarity.Quality -> FortResources.epicBackground
    EFortRarity.Epic -> FortResources.epicBackground
    EFortRarity.Sturdy -> FortResources.rareBackground
    EFortRarity.Rare -> FortResources.rareBackground
    EFortRarity.Uncommon -> FortResources.uncommonBackground
    EFortRarity.Handmade -> FortResources.commonBackground
    EFortRarity.Common -> FortResources.commonBackground
}
fun EFortRarity.getVariantsBackgroundImage() = when(this) {
    EFortRarity.Masterwork -> FortResources.legendaryVariantsBackground
    EFortRarity.Transcendent -> FortResources.legendaryVariantsBackground
    EFortRarity.Elegant -> FortResources.legendaryVariantsBackground
    EFortRarity.Mythic -> FortResources.legendaryVariantsBackground
    EFortRarity.Fine -> FortResources.legendaryVariantsBackground
    EFortRarity.Legendary -> FortResources.legendaryVariantsBackground
    EFortRarity.Quality -> FortResources.epicVariantsBackground
    EFortRarity.Epic -> FortResources.epicVariantsBackground
    EFortRarity.Sturdy -> FortResources.rareVariantsBackground
    EFortRarity.Rare -> FortResources.rareVariantsBackground
    EFortRarity.Uncommon -> FortResources.uncommonVariantsBackground
    EFortRarity.Handmade -> FortResources.commonVariantsBackground
    EFortRarity.Common -> FortResources.commonVariantsBackground
}
fun EFortRarity.getDailyShopBackgroundImage() = when(this) {
    EFortRarity.Masterwork -> FortResources.mythicDailyShopBackground
    EFortRarity.Transcendent -> FortResources.mythicDailyShopBackground
    EFortRarity.Elegant -> FortResources.mythicDailyShopBackground
    EFortRarity.Mythic -> FortResources.mythicDailyShopBackground
    EFortRarity.Fine -> FortResources.legendaryDailyShopBackground
    EFortRarity.Legendary -> FortResources.legendaryDailyShopBackground
    EFortRarity.Quality -> FortResources.epicDailyShopBackground
    EFortRarity.Epic -> FortResources.epicDailyShopBackground
    EFortRarity.Sturdy -> FortResources.rareDailyShopBackground
    EFortRarity.Rare -> FortResources.rareDailyShopBackground
    EFortRarity.Uncommon -> FortResources.uncommonDailyShopBackground
    EFortRarity.Handmade -> FortResources.commonDailyShopBackground
    EFortRarity.Common -> FortResources.commonDailyShopBackground
}
fun EFortRarity.getFeaturedShopBackgroundImage() = when(this) {
    EFortRarity.Masterwork -> FortResources.mythicFeaturedShopBackground
    EFortRarity.Transcendent -> FortResources.mythicFeaturedShopBackground
    EFortRarity.Elegant -> FortResources.mythicFeaturedShopBackground
    EFortRarity.Mythic -> FortResources.mythicFeaturedShopBackground
    EFortRarity.Fine -> FortResources.legendaryFeaturedShopBackground
    EFortRarity.Legendary -> FortResources.legendaryFeaturedShopBackground
    EFortRarity.Quality -> FortResources.epicFeaturedShopBackground
    EFortRarity.Epic -> FortResources.epicFeaturedShopBackground
    EFortRarity.Sturdy -> FortResources.rareFeaturedShopBackground
    EFortRarity.Rare -> FortResources.rareFeaturedShopBackground
    EFortRarity.Uncommon -> FortResources.uncommonFeaturedShopBackground
    EFortRarity.Handmade -> FortResources.commonFeaturedShopBackground
    EFortRarity.Common -> FortResources.commonFeaturedShopBackground
}