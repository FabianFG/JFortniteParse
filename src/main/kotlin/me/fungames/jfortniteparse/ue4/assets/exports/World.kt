package me.fungames.jfortniteparse.ue4.assets.exports

import me.fungames.jfortniteparse.ue4.assets.OnlyAnnotated
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive

@OnlyAnnotated
class UWorld : UObject() {
    var persistentLevel: Lazy<ULevel>? = null
    lateinit var extraReferencedObjects: Array<Lazy<UObject>?>
    lateinit var streamingLevels: Array<Lazy<UObject>?>

    override fun deserialize(Ar: FAssetArchive, validPos: Int) {
        super.deserialize(Ar, validPos)
        persistentLevel = Ar.readObject()
        extraReferencedObjects = Ar.readTArray { Ar.readObject() }
        streamingLevels = Ar.readTArray { Ar.readObject() }
    }
}