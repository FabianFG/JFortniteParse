package me.fungames.jfortniteparse.ue4.vfs

import me.fungames.jfortniteparse.encryption.aes.Aes
import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid
import me.fungames.jfortniteparse.ue4.versions.VersionContainer

abstract class AbstractAesVfsReader(path: String, versions: VersionContainer) : AbstractVfsReader(path, versions) {
    abstract val encryptionKeyGuid: FGuid
    var length = 0L
        protected set
    var customEncryption: CustomEncryption? = null
    var aesKey: ByteArray? = null

    abstract fun isEncrypted(): Boolean
    var encryptedFileCount = 0
        protected set

    abstract fun indexCheckBytes(): ByteArray

    /**
     * Test all keys from a collection and return the working one if there is one
     */
    fun testAesKeys(keys: Iterable<ByteArray>): ByteArray? {
        if (!isEncrypted())
            return null
        keys.forEach {
            if (testAesKey(it))
                return it
        }
        return null
    }

    /**
     * Test all keys from a collection and return the working one if there is one
     */
    @JvmName("testAesKeysStr")
    fun testAesKeys(keys: Iterable<String>): String? {
        if (!isEncrypted())
            return null
        keys.forEach {
            if (testAesKey(it))
                return it
        }
        return null
    }

    /**
     * Test whether the given encryption key is valid by attempting to read the pak mount point and validating it
     */
    fun testAesKey(key: ByteArray) = !isEncrypted() || testAesKey(indexCheckBytes(), key)

    /**
     * Test whether the given encryption key is valid by attempting to read the pak mount point and validating it
     */
    fun testAesKey(key: String) = testAesKey(Aes.parseKey(key))

    interface CustomEncryption {
        fun decryptData(contents: ByteArray, offBytes: Int, numBytes: Int, reader: AbstractAesVfsReader)
    }

    companion object {
        fun testAesKey(bytes: ByteArray, key: ByteArray): Boolean {
            Aes.decryptData(bytes, key)
            return isValidIndex(bytes)
        }
    }
}