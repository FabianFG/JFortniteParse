package me.fungames.jfortniteparse.ue4.assets.reader

import me.fungames.jfortniteparse.LOG_JFP
import me.fungames.jfortniteparse.fileprovider.FileProvider
import me.fungames.jfortniteparse.ue4.assets.exports.UObject
import me.fungames.jfortniteparse.ue4.objects.uobject.FName

open class FObjectAndNameAsStringAssetArchive(data: ByteArray, provider: FileProvider?, name: String) : FAssetArchive(data, provider, name) {
    override fun readFName() = FName(readString())

    override fun <T : UObject> readObject(): Lazy<T>? {
        // load the path name to the object
        val loadedString = readString()
        LOG_JFP.debug { "Read object as string: $loadedString" }
        return provider?.run { lazy { loadObject(loadedString) as T } }
    }
}