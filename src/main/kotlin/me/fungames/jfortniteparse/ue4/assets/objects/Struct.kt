package me.fungames.jfortniteparse.ue4.assets.objects

import com.google.gson.JsonObject
import me.fungames.jfortniteparse.ue4.assets.mappings.TypeMappings
import java.util.*

class Struct(var context: TypeMappings, json: JsonObject) {
    var name: String = json["name"].asString
    var superType = json["superType"]?.asString
    var superStruct = lazy { superType?.let { context.types[it] } }
    var properties = json["properties"]?.asJsonArray?.run {
        val properties = HashMap<Int, PropertyInfo>(size())
        forEach {
            it as JsonObject
            val prop = PropertyInfo(it)
            properties[prop.index] = prop
        }
        properties
    } ?: mutableMapOf()
}