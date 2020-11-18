package me.fungames.jfortniteparse.ue4.objects.core.math

import me.fungames.jfortniteparse.ue4.reader.FArchive

class FTransform {
    var rotation: FQuat
    var translation: FVector
    var scale3D: FVector

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