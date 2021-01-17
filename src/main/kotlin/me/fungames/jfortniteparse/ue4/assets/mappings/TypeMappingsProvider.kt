package me.fungames.jfortniteparse.ue4.assets.mappings

import me.fungames.jfortniteparse.exceptions.MissingSchemaException
import me.fungames.jfortniteparse.ue4.assets.ObjectTypeRegistry
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
            struct.structClass = ObjectTypeRegistry.classes[structName.text] ?: ObjectTypeRegistry.structs[structName.text]
        }
        return struct
    }

    open fun getEnum(enumName: FName) = mappings.enums[enumName.text]
        ?: throw MissingSchemaException("Unknown enum $enumName")
}