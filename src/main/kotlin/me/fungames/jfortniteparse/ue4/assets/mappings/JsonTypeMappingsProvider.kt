package me.fungames.jfortniteparse.ue4.assets.mappings

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import me.fungames.jfortniteparse.ue4.assets.exports.UScriptStruct
import me.fungames.jfortniteparse.ue4.assets.objects.PropertyInfo

abstract class JsonTypeMappingsProvider : TypeMappingsProvider() {
    protected fun addStructs(json: JsonElement): Boolean {
        if (json !is JsonArray) return false
        for (entry in json) {
            if (entry !is JsonObject) continue
            val structEntry = UScriptStruct()
            structEntry.name = entry["name"].asString
            val superType = entry["superType"]?.asString
            structEntry.superStruct = if (superType != null) lazy { mappings.types[superType]!! } else null
            structEntry.childProperties2 = (entry["properties"] as? JsonArray)?.map { PropertyInfo(it.asJsonObject) } ?: emptyList()
            mappings.types[structEntry.name] = structEntry
        }
        return true
    }

    protected fun addEnums(json: JsonElement): Boolean {
        if (json !is JsonArray) return false
        for (entry in json) {
            if (entry !is JsonObject) continue
            val values = entry["values"].asJsonArray.map { it.asString }
            var i = 0
            mappings.enums[entry["name"].asString] = values
        }
        return true
    }
}