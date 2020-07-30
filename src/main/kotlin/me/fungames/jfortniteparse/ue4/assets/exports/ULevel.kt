package me.fungames.jfortniteparse.ue4.assets.exports

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter
import me.fungames.jfortniteparse.ue4.objects.engine.FURL
import me.fungames.jfortniteparse.ue4.objects.uobject.FObjectExport
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex

@ExperimentalUnsignedTypes
class ULevel : UObject {
    var url: FURL
    var actors: Array<UExport?> // Array<AActor?>
//    var model: UModel?
//    var modelComponents: Array<FPackageIndex> // UModelComponent

    constructor(Ar: FAssetArchive, exportObject: FObjectExport) : super(Ar, exportObject) {
        actors = Ar.readTArray { Ar.loadObject<UExport>(FPackageIndex(Ar)) }
        url = FURL(Ar)
//        model = Ar.loadObject<UModel>(FPackageIndex(Ar))
//        modelComponents = Ar.readTArray { FPackageIndex(Ar) }
//        navListStart = TODO continue, at least we can grab the actors array
        super.complete(Ar)
    }

    override fun serialize(Ar: FAssetArchiveWriter) {
        super.serialize(Ar)
        super.completeWrite(Ar)
        throw ParserException("Serializing ULevel not supported")
    }
}

/*fun main() {
    val value = Package(File("Apollo_1x1_Woods_SnackShack_a.umap"), File("Apollo_1x1_Woods_SnackShack_a.uexp"), null)
    val exports = value.exports
    println("done")
}*/
