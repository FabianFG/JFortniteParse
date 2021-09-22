package me.fungames.jfortniteparse.ue4.objects.core.math

/** Rotation matrix no translation */
class FRotationMatrix : FRotationTranslationMatrix {
    /**
     * Constructor.
     *
     * @param rot rotation
     */
    constructor(rot: FRotator) : super(rot, FVector(0f, 0f, 0f))
}