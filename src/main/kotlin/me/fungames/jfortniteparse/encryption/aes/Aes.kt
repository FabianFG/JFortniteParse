package me.fungames.jfortniteparse.encryption.aes

import me.fungames.jfortniteparse.exceptions.InvalidAesKeyException
import me.fungames.jfortniteparse.util.ModByteArrayOutputStream
import me.fungames.jfortniteparse.util.parseHexBinary
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
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

    inline fun decrypt(encrypted: ByteArray, key: String) = decrypt(encrypted, parseKey(key))
    fun decrypt(encrypted: ByteArray, key: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/CBC/NoPadding")
        val secretKeySpec = SecretKeySpec(key, "AES")
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, IvParameterSpec(ByteArray(BLOCK_SIZE)))
        val out = ModByteArrayOutputStream(encrypted.size)
        for (i in encrypted.indices step BLOCK_SIZE)
            out.write(cipher.doFinal(encrypted.copyOfRange(i, i + BLOCK_SIZE)))
        return out.toByteArray()
    }

    inline fun encrypt(decrypted: ByteArray, key: String) = encrypt(decrypted, parseKey(key))
    fun encrypt(decrypted: ByteArray, key: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/CBC/NoPadding")
        val secretKeySpec = SecretKeySpec(key, "AES")
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, IvParameterSpec(ByteArray(BLOCK_SIZE)))
        val out = ModByteArrayOutputStream(decrypted.size)
        for (i in decrypted.indices step BLOCK_SIZE)
            out.write(cipher.doFinal(decrypted.copyOfRange(i, i + BLOCK_SIZE)))
        return out.toByteArray()
    }

    inline fun decryptData(contents: ByteArray, key: ByteArray) {
        decryptData(contents, 0, contents.size, key)
    }

    fun decryptData(contents: ByteArray, offBytes: Int, numBytes: Int, key: ByteArray) {
        Cipher.getInstance("AES/ECB/NoPadding").apply {
            init(Cipher.DECRYPT_MODE, SecretKeySpec(key, "AES"))
            for (i in offBytes until offBytes + numBytes step BLOCK_SIZE)
                doFinal(contents, i, BLOCK_SIZE, contents, i)
        }
    }
}