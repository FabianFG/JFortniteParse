package me.fungames.jfortniteparse.ue4.objects.moviescene.evaluation

import me.fungames.jfortniteparse.ue4.assets.objects.FStructFallback
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.objects.uobject.FName

class FMovieSceneTrackImplementationPtr {
    var typeName: String
    var data: FStructFallback? = null

    constructor(Ar: FAssetArchive) {
        typeName = Ar.readString()
        if (typeName.isNotEmpty()) {
            data = FStructFallback(Ar, FName(typeName.substringAfterLast('.')))
        }
    }
}