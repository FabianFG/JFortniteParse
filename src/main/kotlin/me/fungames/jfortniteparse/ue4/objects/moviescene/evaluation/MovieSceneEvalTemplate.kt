package me.fungames.jfortniteparse.ue4.objects.moviescene.evaluation

import me.fungames.jfortniteparse.ue4.assets.objects.FStructFallback
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.objects.uobject.FName

class FMovieSceneEvalTemplatePtr {
    var typeName: String
    var data: FStructFallback

    constructor(Ar: FAssetArchive) {
        typeName = Ar.readString()
        data = FStructFallback(Ar, FName.dummy(typeName.substringAfterLast('.')))
    }
}