package me.fungames.jfortniteparse.fort.exports

import me.fungames.jfortniteparse.ue4.objects.core.math.FLinearColor
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath

@ExperimentalUnsignedTypes
class AthenaEmojiItemDefinition : FortItemDefinition() {
    var PreviewAnimation: FSoftObjectPath? = null
    var SpriteSheet: FSoftObjectPath? = null
    var BaseMaterial: FPackageIndex? = null
    var InitialColor: FLinearColor? = null
    var Animation: FPackageIndex? = null
}