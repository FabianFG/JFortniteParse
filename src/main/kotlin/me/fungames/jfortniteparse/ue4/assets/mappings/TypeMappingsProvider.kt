package me.fungames.jfortniteparse.ue4.assets.mappings

import me.fungames.jfortniteparse.ue4.assets.ObjectTypeRegistry
import me.fungames.jfortniteparse.ue4.assets.exports.UEnum
import me.fungames.jfortniteparse.ue4.assets.exports.UStruct
import me.fungames.jfortniteparse.ue4.objects.uobject.FName

abstract class TypeMappingsProvider {
    val mappings = TypeMappings()
    abstract fun reload(): Boolean

    open fun getStruct(structName: FName): UStruct? {
        val struct = mappings.types[structName.text]
            ?: return null
        // required to be assigned so classes with custom serializers can be read properly
        if (struct.structClass == null) {
            struct.structClass = ObjectTypeRegistry.get(structName.text)
        }
        return struct
    }

    open fun getEnumValues(enumName: FName) = mappings.enums[enumName.text]

    fun getEnum(enumName: FName): UEnum? {
        val enumValues = getEnumValues(enumName)
        if (enumValues != null) {
            val enum = UEnum()
            enum.name = enumName.text
            enum.names = Array(enumValues.size) { FName("$enumName::${enumValues[it]}") to it.toLong() }
            return enum
        }
        return null
    }
}