package me.fungames.jfortniteparse.ue4.assets.exports

import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.objects.engine.FURL
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex

@ExperimentalUnsignedTypes
class ULevel : UObject() {
    lateinit var url: FURL
    lateinit var actors: Array<UExport?> // Array<AActor?>
//    var model: UModel?
//    lateinit var modelComponents: Array<FPackageIndex> // UModelComponent

    override fun deserialize(Ar: FAssetArchive, validPos: Int) {
        super.deserialize(Ar, validPos)
        actors = Ar.readTArray { FPackageIndex(Ar).run { Ar.owner.loadObject(this) } }
        url = FURL(Ar)
//        model = Ar.loadObject<UModel>(FPackageIndex(Ar))
//        modelComponents = Ar.readTArray { FPackageIndex(Ar) }
//        navListStart = TODO continue, at least we can grab the actors array
        super.complete(Ar)
    }
}

/*fun main() {
    val value = Package(File("Apollo_1x1_Woods_SnackShack_a.umap"), File("Apollo_1x1_Woods_SnackShack_a.uexp"), null)
    val exports = value.exports
    println("done")
}*/
