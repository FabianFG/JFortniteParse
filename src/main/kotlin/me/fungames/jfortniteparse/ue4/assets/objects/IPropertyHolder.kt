package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.objects.uobject.FName

interface IPropertyHolder {
    var properties: LinkedHashMap<FName, FPropertyTag>
}