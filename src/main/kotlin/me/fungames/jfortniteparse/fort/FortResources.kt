@file:Suppress("EXPERIMENTAL_API_USAGE")

package me.fungames.jfortniteparse.fort

import me.fungames.jfortniteparse.fort.enums.EFortRarity
import java.awt.Font
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

object FortResources {
    // Common Rarity
    val commonBackground: BufferedImage by lazy {
        ImageIO.read(FortResources.javaClass.getResourceAsStream("/icons/C512.png"))
            ?: throw IllegalStateException("Failed to load a resource")
    }
    val commonVariantsBackground: BufferedImage by lazy {
        ImageIO.read(FortResources.javaClass.getResourceAsStream("/icons/vC512.png"))
            ?: throw IllegalStateException("Failed to load a resource")
    }
    val commonDailyShopBackground: BufferedImage by lazy {
        ImageIO.read(FortResources.javaClass.getResourceAsStream("/icons/CDail.png"))
            ?: throw IllegalStateException("Failed to load a resource")
    }
    val commonFeaturedShopBackground: BufferedImage by lazy {
        ImageIO.read(FortResources.javaClass.getResourceAsStream("/icons/CFeat.png"))
            ?: throw IllegalStateException("Failed to load a resource")
    }

    // Uncommon Rarity
    val uncommonBackground: BufferedImage by lazy {
        ImageIO.read(FortResources.javaClass.getResourceAsStream("/icons/U512.png"))
            ?: throw IllegalStateException("Failed to load a resource")
    }
    val uncommonVariantsBackground: BufferedImage by lazy {
        ImageIO.read(FortResources.javaClass.getResourceAsStream("/icons/vU512.png"))
            ?: throw IllegalStateException("Failed to load a resource")
    }
    val uncommonDailyShopBackground: BufferedImage by lazy {
        ImageIO.read(FortResources.javaClass.getResourceAsStream("/icons/UDail.png"))
            ?: throw IllegalStateException("Failed to load a resource")
    }
    val uncommonFeaturedShopBackground: BufferedImage by lazy {
        ImageIO.read(FortResources.javaClass.getResourceAsStream("/icons/UFeat.png"))
            ?: throw IllegalStateException("Failed to load a resource")
    }

    // Rare Rarity
    val rareBackground: BufferedImage by lazy {
        ImageIO.read(FortResources.javaClass.getResourceAsStream("/icons/R512.png"))
            ?: throw IllegalStateException("Failed to load a resource")
    }
    val rareVariantsBackground: BufferedImage by lazy {
        ImageIO.read(FortResources.javaClass.getResourceAsStream("/icons/vR512.png"))
            ?: throw IllegalStateException("Failed to load a resource")
    }
    val rareDailyShopBackground: BufferedImage by lazy {
        ImageIO.read(FortResources.javaClass.getResourceAsStream("/icons/RDail.png"))
            ?: throw IllegalStateException("Failed to load a resource")
    }
    val rareFeaturedShopBackground: BufferedImage by lazy {
        ImageIO.read(FortResources.javaClass.getResourceAsStream("/icons/RFeat.png"))
            ?: throw IllegalStateException("Failed to load a resource")
    }

    // Epic Rarity
    val epicBackground: BufferedImage by lazy {
        ImageIO.read(FortResources.javaClass.getResourceAsStream("/icons/E512.png"))
            ?: throw IllegalStateException("Failed to load a resource")
    }
    val epicVariantsBackground: BufferedImage by lazy {
        ImageIO.read(FortResources.javaClass.getResourceAsStream("/icons/vE512.png"))
            ?: throw IllegalStateException("Failed to load a resource")
    }
    val epicDailyShopBackground: BufferedImage by lazy {
        ImageIO.read(FortResources.javaClass.getResourceAsStream("/icons/EDail.png"))
            ?: throw IllegalStateException("Failed to load a resource")
    }
    val epicFeaturedShopBackground: BufferedImage by lazy {
        ImageIO.read(FortResources.javaClass.getResourceAsStream("/icons/EFeat.png"))
            ?: throw IllegalStateException("Failed to load a resource")
    }

    // Legendary Rarity
    val legendaryBackground: BufferedImage by lazy {
        ImageIO.read(FortResources.javaClass.getResourceAsStream("/icons/L512.png"))
            ?: throw IllegalStateException("Failed to load a resource")
    }
    val legendaryVariantsBackground: BufferedImage by lazy {
        ImageIO.read(FortResources.javaClass.getResourceAsStream("/icons/vL512.png"))
            ?: throw IllegalStateException("Failed to load a resource")
    }
    val legendaryDailyShopBackground: BufferedImage by lazy {
        ImageIO.read(FortResources.javaClass.getResourceAsStream("/icons/LDail.png"))
            ?: throw IllegalStateException("Failed to load a resource")
    }
    val legendaryFeaturedShopBackground: BufferedImage by lazy {
        ImageIO.read(FortResources.javaClass.getResourceAsStream("/icons/LFeat.png"))
            ?: throw IllegalStateException("Failed to load a resource")
    }

    // Mythic Rarity
    val mythicBackground: BufferedImage by lazy {
        ImageIO.read(FortResources.javaClass.getResourceAsStream("/icons/M512.png"))
            ?: throw IllegalStateException("Failed to load a resource")
    }
    //val mythicVariantsBackground : BufferedImage by lazy { ImageIO.read(Resources.javaClass.getResourceAsStream("/icons/vM512.png"))
    //    ?: throw IllegalStateException("Failed to load a resource") }
    val mythicDailyShopBackground: BufferedImage by lazy {
        ImageIO.read(FortResources.javaClass.getResourceAsStream("/icons/MDail.png"))
            ?: throw IllegalStateException("Failed to load a resource")
    }
    val mythicFeaturedShopBackground: BufferedImage by lazy {
        ImageIO.read(FortResources.javaClass.getResourceAsStream("/icons/MFeat.png"))
            ?: throw IllegalStateException("Failed to load a resource")
    }

    // Fallback Icon
    val fallbackIcon: BufferedImage by lazy {
        ImageIO.read(FortResources.javaClass.getResourceAsStream("/icons/NoIcon.png"))
            ?: throw IllegalStateException("Failed to load a resource")
    }

    // VBucks Icon
    val vbucksIcon: BufferedImage by lazy {
        ImageIO.read(FortResources.javaClass.getResourceAsStream("/icons/vbucks.png"))
            ?: throw IllegalStateException("Failed to load a resource")
    }

    //Fonts
    val burbank: Font by lazy { Font.createFont(Font.TRUETYPE_FONT, FortResources.javaClass.getResourceAsStream("/fonts/BurbankBigCondensed-Black.ttf")) }
    val notoSans: Font by lazy { Font.createFont(Font.TRUETYPE_FONT, FortResources.javaClass.getResourceAsStream("/fonts/NotoSans-Regular.ttf")) }
    val notoSansBold: Font by lazy { Font.createFont(Font.TRUETYPE_FONT, FortResources.javaClass.getResourceAsStream("/fonts/NotoSans-Bold.ttf")) }
}

fun EFortRarity.getBackgroundImage() = when (this) {
    EFortRarity.Common -> FortResources.commonBackground
    EFortRarity.Uncommon -> FortResources.uncommonBackground
    EFortRarity.Rare -> FortResources.rareBackground
    EFortRarity.Epic -> FortResources.epicBackground
    EFortRarity.Legendary -> FortResources.legendaryBackground
    else -> FortResources.mythicBackground
}

fun EFortRarity.getVariantsBackgroundImage() = when (this) {
    EFortRarity.Common -> FortResources.commonVariantsBackground
    EFortRarity.Uncommon -> FortResources.uncommonVariantsBackground
    EFortRarity.Rare -> FortResources.rareVariantsBackground
    EFortRarity.Epic -> FortResources.epicVariantsBackground
    EFortRarity.Legendary -> FortResources.legendaryVariantsBackground
    else -> FortResources.legendaryVariantsBackground
}

fun EFortRarity.getDailyShopBackgroundImage() = when (this) {
    EFortRarity.Common -> FortResources.commonDailyShopBackground
    EFortRarity.Uncommon -> FortResources.uncommonDailyShopBackground
    EFortRarity.Rare -> FortResources.rareDailyShopBackground
    EFortRarity.Epic -> FortResources.epicDailyShopBackground
    EFortRarity.Legendary -> FortResources.legendaryDailyShopBackground
    else -> FortResources.mythicDailyShopBackground
}

fun EFortRarity.getFeaturedShopBackgroundImage() = when (this) {
    EFortRarity.Common -> FortResources.commonFeaturedShopBackground
    EFortRarity.Uncommon -> FortResources.uncommonFeaturedShopBackground
    EFortRarity.Rare -> FortResources.rareFeaturedShopBackground
    EFortRarity.Epic -> FortResources.epicFeaturedShopBackground
    EFortRarity.Legendary -> FortResources.legendaryFeaturedShopBackground
    else -> FortResources.mythicFeaturedShopBackground
}