package me.fungames.jfortniteparse.ue4.objects.core.math

import me.fungames.jfortniteparse.ue4.assets.UProperty
import me.fungames.jfortniteparse.ue4.assets.UStruct
import me.fungames.jfortniteparse.ue4.reader.FArchive

@UStruct
class FTransform {
    @UProperty("Rotation")
    var rotation: FQuat
    @UProperty("Translation")
    var translation: FVector
    @UProperty("Scale3D")
    var scale3D: FVector

    constructor() : this(FQuat(0f, 0f, 0f, 0f), FVector(0f), FVector(1f))

    constructor(rotation: FQuat, translation: FVector, scale3D: FVector) {
        this.rotation = rotation
        this.translation = translation
        this.scale3D = scale3D
    }

    constructor(Ar: FArchive) {
        rotation = FQuat(Ar)
        translation = FVector(Ar)
        scale3D = FVector(Ar)
    }
}