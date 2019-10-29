package me.fungames.jfortniteparse.encryption.aes

import me.fungames.jfortniteparse.exceptions.InvalidAesKeyException
import me.fungames.jfortniteparse.util.parseHexBinary
import java.io.ByteArrayOutputStream
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object Aes {
    private fun parseKey(key: String) : ByteArray {
        val data = if (key.startsWith("0x"))
            key.substring(2).parseHexBinary()
        else
            key.parseHexBinary()
        if (data.size != 32)
            throw InvalidAesKeyException("Given AES key is not properly formatted, needs to be exactly 32 bytes long")
        return data
    }

    fun decrypt(encrypted: ByteArray, key : String) = decrypt(encrypted, parseKey(key))
    fun decrypt(encrypted : ByteArray, key : ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/CBC/NoPadding")
        val secretKeySpec = SecretKeySpec(key, "AES")
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, IvParameterSpec(ByteArray(16)))
        val out = ByteArrayOutputStream()
        for (i in 0 until encrypted.size step 16)
            out.write(cipher.doFinal(encrypted.copyOfRange(i, i + 16)))
        return out.toByteArray()
    }
    fun encrypt(decrypted: ByteArray, key : String) = encrypt(decrypted, parseKey(key))
    fun encrypt(decrypted : ByteArray, key : ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/CBC/NoPadding")
        val secretKeySpec = SecretKeySpec(key, "AES")
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, IvParameterSpec(ByteArray(16)))
        val out = ByteArrayOutputStream()
        for (i in 0 until decrypted.size step 16)
            out.write(cipher.doFinal(decrypted.copyOfRange(i, i + 16)))
        return out.toByteArray()
    }


}