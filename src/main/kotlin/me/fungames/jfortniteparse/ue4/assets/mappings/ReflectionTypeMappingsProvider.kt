package me.fungames.jfortniteparse.ue4.assets.mappings

import me.fungames.jfortniteparse.ue4.assets.ObjectTypeRegistry
import me.fungames.jfortniteparse.ue4.assets.exports.UScriptStruct
import me.fungames.jfortniteparse.ue4.objects.uobject.FName

class ReflectionTypeMappingsProvider : TypeMappingsProvider() {
    override fun getStruct(structName: FName) = UScriptStruct(ObjectTypeRegistry.get(structName.text), structName)
    override fun getEnum(enumName: FName) = emptyList<String>()
    override fun reload() = true
}