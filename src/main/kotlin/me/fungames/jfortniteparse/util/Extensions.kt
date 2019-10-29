package me.fungames.jfortniteparse.util

import java.awt.Graphics2D
import java.awt.Image
import java.awt.image.BufferedImage

fun BufferedImage.scale(newWidth : Int, newHeight : Int, flags : Int = Image.SCALE_SMOOTH) : BufferedImage {
    val scaled = this.getScaledInstance(newWidth, newHeight, flags)
    val scaledImg = BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB)
    val g2 = scaledImg.createGraphics()
    g2.drawImage(scaled, 0, 0, null)
    g2.dispose()
    return scaledImg
}

fun BufferedImage.cut(newWidth: Int): BufferedImage {
    val startX = (this.width / 2) - (newWidth / 2)
    return this.getSubimage(startX, 0, newWidth, this.height)
}

fun Graphics2D.drawCenteredString(s : String, x : Int, y : Int) {
    drawString(s, x - fontMetrics.stringWidth(s) / 2, y)
}