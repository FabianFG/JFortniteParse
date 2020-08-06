package me.fungames.jfortniteparse.valorant

import java.awt.Font

object ValorantResources {
    //Fonts
    val dinNextBlack: Font by lazy { Font.createFont(Font.TRUETYPE_FONT, ValorantResources.javaClass.getResourceAsStream("/fonts/DINNext_Black.ttf")) }
    val dinNextBold: Font by lazy { Font.createFont(Font.TRUETYPE_FONT, ValorantResources.javaClass.getResourceAsStream("/fonts/DINNext_Bold.ttf")) }
    val dinNextLight: Font by lazy { Font.createFont(Font.TRUETYPE_FONT, ValorantResources.javaClass.getResourceAsStream("/fonts/DINNext_Light.ttf")) }
}