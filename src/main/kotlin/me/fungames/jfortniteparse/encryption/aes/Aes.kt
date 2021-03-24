package me.fungames.jfortniteparse.encryption.aes

import me.fungames.jfortniteparse.exceptions.InvalidAesKeyException
import me.fungames.jfortniteparse.util.parseHexBinary
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object Aes {
    const val BLOCK_SIZE = 16

    fun parseKey(key: String): ByteArray {
        val data = if (key.startsWith("0x"))
            key.substring(2).parseHexBinary()
        else
            key.parseHexBinary()
        if (data.size != 32)
            throw InvalidAesKeyException("Given AES key is not properly formatted, needs to be exactly 32 bytes long")
        return data
    }

    inline fun encryptData(contents: ByteArray, keyBytes: ByteArray) {
        encryptData(contents, 0, contents.size, keyBytes)
    }

    fun encryptData(contents: ByteArray, offBytes: Int, numBytes: Int, keyBytes: ByteArray) {
        Cipher.getInstance("AES/ECB/NoPadding").apply {
            init(Cipher.ENCRYPT_MODE, SecretKeySpec(keyBytes, "AES"))
            doFinal(contents, offBytes, numBytes, contents, offBytes)
        }
    }

    inline fun decryptData(contents: ByteArray, keyBytes: ByteArray) {
        decryptData(contents, 0, contents.size, keyBytes)
    }

    fun decryptData(contents: ByteArray, offBytes: Int, numBytes: Int, keyBytes: ByteArray) {
        Cipher.getInstance("AES/ECB/NoPadding").apply {
            init(Cipher.DECRYPT_MODE, SecretKeySpec(keyBytes, "AES"))
            doFinal(contents, offBytes, numBytes, contents, offBytes)
        }
    }
}