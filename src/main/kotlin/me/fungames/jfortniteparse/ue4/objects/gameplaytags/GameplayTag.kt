package me.fungames.jfortniteparse.ue4.objects.gameplaytags

import me.fungames.jfortniteparse.ue4.assets.UStruct
import me.fungames.jfortniteparse.ue4.objects.uobject.FName

@UStruct
class FGameplayTag(@JvmField var TagName: FName = FName.NAME_None) {
    override fun toString() = TagName.toString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FGameplayTag) return false

        return TagName == other.TagName
    }

    override fun hashCode() = TagName.toString().toLowerCase().hashCode()
}