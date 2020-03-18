package me.fungames.jfortniteparse.resources

import me.fungames.jfortniteparse.ue4.assets.exports.fort.EFortRarity
import java.awt.Font
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

object Resources {
    // Common Rarity
    val commonBackground : BufferedImage by lazy { ImageIO.read(Resources.javaClass.getResourceAsStream("/icons/C512.png"))
        ?: throw IllegalStateException("Failed to load a resource") }
    val commonVariantsBackground : BufferedImage by lazy { ImageIO.read(Resources.javaClass.getResourceAsStream("/icons/vC512.png"))
        ?: throw IllegalStateException("Failed to load a resource") }
    val commonDailyShopBackground : BufferedImage by lazy { ImageIO.read(Resources.javaClass.getResourceAsStream("/icons/CDail.png"))
        ?: throw IllegalStateException("Failed to load a resource") }
    val commonFeaturedShopBackground : BufferedImage by lazy { ImageIO.read(Resources.javaClass.getResourceAsStream("/icons/CFeat.png"))
        ?: throw IllegalStateException("Failed to load a resource") }

    // Uncommon Rarity
    val uncommonBackground : BufferedImage by lazy { ImageIO.read(Resources.javaClass.getResourceAsStream("/icons/U512.png"))
        ?: throw IllegalStateException("Failed to load a resource") }
    val uncommonVariantsBackground : BufferedImage by lazy { ImageIO.read(Resources.javaClass.getResourceAsStream("/icons/vU512.png"))
        ?: throw IllegalStateException("Failed to load a resource") }
    val uncommonDailyShopBackground : BufferedImage by lazy { ImageIO.read(Resources.javaClass.getResourceAsStream("/icons/UDail.png"))
        ?: throw IllegalStateException("Failed to load a resource") }
    val uncommonFeaturedShopBackground : BufferedImage by lazy { ImageIO.read(Resources.javaClass.getResourceAsStream("/icons/UFeat.png"))
        ?: throw IllegalStateException("Failed to load a resource") }

    // Rare Rarity
    val rareBackground : BufferedImage by lazy { ImageIO.read(Resources.javaClass.getResourceAsStream("/icons/R512.png"))
        ?: throw IllegalStateException("Failed to load a resource") }
    val rareVariantsBackground : BufferedImage by lazy { ImageIO.read(Resources.javaClass.getResourceAsStream("/icons/vR512.png"))
        ?: throw IllegalStateException("Failed to load a resource") }
    val rareDailyShopBackground : BufferedImage by lazy { ImageIO.read(Resources.javaClass.getResourceAsStream("/icons/RDail.png"))
        ?: throw IllegalStateException("Failed to load a resource") }
    val rareFeaturedShopBackground : BufferedImage by lazy { ImageIO.read(Resources.javaClass.getResourceAsStream("/icons/RFeat.png"))
        ?: throw IllegalStateException("Failed to load a resource") }

    // Epic Rarity
    val epicBackground : BufferedImage by lazy { ImageIO.read(Resources.javaClass.getResourceAsStream("/icons/E512.png"))
        ?: throw IllegalStateException("Failed to load a resource") }
    val epicVariantsBackground : BufferedImage by lazy { ImageIO.read(Resources.javaClass.getResourceAsStream("/icons/vE512.png"))
        ?: throw IllegalStateException("Failed to load a resource") }
    val epicDailyShopBackground : BufferedImage by lazy { ImageIO.read(Resources.javaClass.getResourceAsStream("/icons/EDail.png"))
        ?: throw IllegalStateException("Failed to load a resource") }
    val epicFeaturedShopBackground : BufferedImage by lazy { ImageIO.read(Resources.javaClass.getResourceAsStream("/icons/EFeat.png"))
        ?: throw IllegalStateException("Failed to load a resource") }

    // Legendary Rarity
    val legendaryBackground : BufferedImage by lazy { ImageIO.read(Resources.javaClass.getResourceAsStream("/icons/L512.png"))
        ?: throw IllegalStateException("Failed to load a resource") }
    val legendaryVariantsBackground : BufferedImage by lazy { ImageIO.read(Resources.javaClass.getResourceAsStream("/icons/vL512.png"))
        ?: throw IllegalStateException("Failed to load a resource") }
    val legendaryDailyShopBackground : BufferedImage by lazy { ImageIO.read(Resources.javaClass.getResourceAsStream("/icons/LDail.png"))
        ?: throw IllegalStateException("Failed to load a resource") }
    val legendaryFeaturedShopBackground : BufferedImage by lazy { ImageIO.read(Resources.javaClass.getResourceAsStream("/icons/LFeat.png"))
        ?: throw IllegalStateException("Failed to load a resource") }

    // Mythic Rarity
    val mythicBackground : BufferedImage by lazy { ImageIO.read(Resources.javaClass.getResourceAsStream("/icons/M512.png"))
        ?: throw IllegalStateException("Failed to load a resource") }
    //val mythicVariantsBackground : BufferedImage by lazy { ImageIO.read(Resources.javaClass.getResourceAsStream("/icons/vM512.png"))
    //    ?: throw IllegalStateException("Failed to load a resource") }
    val mythicDailyShopBackground : BufferedImage by lazy { ImageIO.read(Resources.javaClass.getResourceAsStream("/icons/MDail.png"))
        ?: throw IllegalStateException("Failed to load a resource") }
    val mythicFeaturedShopBackground : BufferedImage by lazy { ImageIO.read(Resources.javaClass.getResourceAsStream("/icons/MFeat.png"))
        ?: throw IllegalStateException("Failed to load a resource") }

    // Fallback Icon
    val fallbackIcon : BufferedImage by lazy { ImageIO.read(Resources.javaClass.getResourceAsStream("/icons/NoIcon.png"))
        ?: throw IllegalStateException("Failed to load a resource") }

    // VBucks Icon
    val vbucksIcon : BufferedImage by lazy { ImageIO.read(Resources.javaClass.getResourceAsStream("/icons/vbucks.png"))
        ?: throw IllegalStateException("Failed to load a resource") }

    //Fonts
    val burbank : Font by lazy { Font.createFont(Font.TRUETYPE_FONT, Resources.javaClass.getResourceAsStream("/fonts/BurbankBigCondensed-Black.ttf")) }
    val notoSans : Font by lazy { Font.createFont(Font.TRUETYPE_FONT, Resources.javaClass.getResourceAsStream("/fonts/NotoSans-Regular.ttf")) }
    val notoSansBold : Font by lazy { Font.createFont(Font.TRUETYPE_FONT, Resources.javaClass.getResourceAsStream("/fonts/NotoSans-Bold.ttf")) }
}

fun EFortRarity.getBackgroundImage() = when(this) {
    EFortRarity.Masterwork -> Resources.mythicBackground
    EFortRarity.Transcendent -> Resources.mythicBackground
    EFortRarity.Elegant -> Resources.mythicBackground
    EFortRarity.Mythic -> Resources.mythicBackground
    EFortRarity.Fine -> Resources.legendaryBackground
    EFortRarity.Legendary -> Resources.legendaryBackground
    EFortRarity.Quality -> Resources.epicBackground
    EFortRarity.Epic -> Resources.epicBackground
    EFortRarity.Sturdy -> Resources.rareBackground
    EFortRarity.Rare -> Resources.rareBackground
    EFortRarity.Uncommon -> Resources.uncommonBackground
    EFortRarity.Handmade -> Resources.commonBackground
    EFortRarity.Common -> Resources.commonBackground
}
fun EFortRarity.getVariantsBackgroundImage() = when(this) {
    EFortRarity.Masterwork -> Resources.legendaryVariantsBackground
    EFortRarity.Transcendent -> Resources.legendaryVariantsBackground
    EFortRarity.Elegant -> Resources.legendaryVariantsBackground
    EFortRarity.Mythic -> Resources.legendaryVariantsBackground
    EFortRarity.Fine -> Resources.legendaryVariantsBackground
    EFortRarity.Legendary -> Resources.legendaryVariantsBackground
    EFortRarity.Quality -> Resources.epicVariantsBackground
    EFortRarity.Epic -> Resources.epicVariantsBackground
    EFortRarity.Sturdy -> Resources.rareVariantsBackground
    EFortRarity.Rare -> Resources.rareVariantsBackground
    EFortRarity.Uncommon -> Resources.uncommonVariantsBackground
    EFortRarity.Handmade -> Resources.commonVariantsBackground
    EFortRarity.Common -> Resources.commonVariantsBackground
}
fun EFortRarity.getDailyShopBackgroundImage() = when(this) {
    EFortRarity.Masterwork -> Resources.mythicDailyShopBackground
    EFortRarity.Transcendent -> Resources.mythicDailyShopBackground
    EFortRarity.Elegant -> Resources.mythicDailyShopBackground
    EFortRarity.Mythic -> Resources.mythicDailyShopBackground
    EFortRarity.Fine -> Resources.legendaryDailyShopBackground
    EFortRarity.Legendary -> Resources.legendaryDailyShopBackground
    EFortRarity.Quality -> Resources.epicDailyShopBackground
    EFortRarity.Epic -> Resources.epicDailyShopBackground
    EFortRarity.Sturdy -> Resources.rareDailyShopBackground
    EFortRarity.Rare -> Resources.rareDailyShopBackground
    EFortRarity.Uncommon -> Resources.uncommonDailyShopBackground
    EFortRarity.Handmade -> Resources.commonDailyShopBackground
    EFortRarity.Common -> Resources.commonDailyShopBackground
}
fun EFortRarity.getFeaturedShopBackgroundImage() = when(this) {
    EFortRarity.Masterwork -> Resources.mythicFeaturedShopBackground
    EFortRarity.Transcendent -> Resources.mythicFeaturedShopBackground
    EFortRarity.Elegant -> Resources.mythicFeaturedShopBackground
    EFortRarity.Mythic -> Resources.mythicFeaturedShopBackground
    EFortRarity.Fine -> Resources.legendaryFeaturedShopBackground
    EFortRarity.Legendary -> Resources.legendaryFeaturedShopBackground
    EFortRarity.Quality -> Resources.epicFeaturedShopBackground
    EFortRarity.Epic -> Resources.epicFeaturedShopBackground
    EFortRarity.Sturdy -> Resources.rareFeaturedShopBackground
    EFortRarity.Rare -> Resources.rareFeaturedShopBackground
    EFortRarity.Uncommon -> Resources.uncommonFeaturedShopBackground
    EFortRarity.Handmade -> Resources.commonFeaturedShopBackground
    EFortRarity.Common -> Resources.commonFeaturedShopBackground
}