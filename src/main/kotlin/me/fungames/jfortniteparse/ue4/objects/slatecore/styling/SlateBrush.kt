package me.fungames.jfortniteparse.ue4.objects.slatecore.styling

import me.fungames.jfortniteparse.ue4.assets.util.StructFallbackClass
import me.fungames.jfortniteparse.ue4.objects.core.math.FVector2D
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex

@ExperimentalUnsignedTypes
@StructFallbackClass
class FSlateBrush {
    var ImageSize: FVector2D? = null
    var ResourceObject: FPackageIndex? = null
    // There's more but we only include what we needed for now.
}