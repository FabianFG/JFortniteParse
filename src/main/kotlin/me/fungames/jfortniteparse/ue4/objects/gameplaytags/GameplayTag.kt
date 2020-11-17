package me.fungames.jfortniteparse.ue4.objects.gameplaytags

import me.fungames.jfortniteparse.ue4.assets.UStruct
import me.fungames.jfortniteparse.ue4.objects.uobject.FName

@UStruct
class FGameplayTag(@JvmField var TagName: FName = FName.NAME_None)