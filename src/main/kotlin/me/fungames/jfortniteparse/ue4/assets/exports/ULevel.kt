package me.fungames.jfortniteparse.ue4.assets.exports

import me.fungames.jfortniteparse.ue4.assets.OnlyAnnotated
import me.fungames.jfortniteparse.ue4.assets.exports.actors.AActor
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.objects.engine.FURL

@OnlyAnnotated
class ULevel : ULevel_Properties() {
    lateinit var url: FURL
    lateinit var actors: Array<Lazy<AActor>?>
//    var model: UModel?
//    lateinit var modelComponents: Array<FPackageIndex> // UModelComponent

    override fun deserialize(Ar: FAssetArchive, validPos: Int) {
        super.deserialize(Ar, validPos)
        actors = Ar.readTArray { Ar.readObject() }
        url = FURL(Ar)
//        model = Ar.loadObject<UModel>(FPackageIndex(Ar))
//        modelComponents = Ar.readTArray { FPackageIndex(Ar) }
//        navListStart = TODO continue, at least we can grab the actors array
        super.complete(Ar)
    }
}
