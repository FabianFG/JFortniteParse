package me.fungames.jfortniteparse.util

import java.io.ByteArrayOutputStream

/**
 * To avoid copying on .toByteArray() if buf has the correct size already
 */
class ModByteArrayOutputStream(size : Int) : ByteArrayOutputStream(size) {

    override fun toByteArray(): ByteArray {
        if (count == buf.size)
            return buf
        return super.toByteArray()
    }
}