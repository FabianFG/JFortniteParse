package me.fungames.jfortniteparse.ue4.assets.exports

import me.fungames.jfortniteparse.ue4.assets.OnlyAnnotated
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive

@OnlyAnnotated
class UWorld : UObject() {
    var persistentLevel: Lazy<ULevel>? = null
    var extraReferencedObjects: Lazy<UObject>? = null
    var streamingLevels: Lazy<UObject>? = null

    override fun deserialize(Ar: FAssetArchive, validPos: Int) {
        super.deserialize(Ar, validPos)
        persistentLevel = Ar.readObject()
        extraReferencedObjects = Ar.readObject()
        streamingLevels = Ar.readObject()
    }
}