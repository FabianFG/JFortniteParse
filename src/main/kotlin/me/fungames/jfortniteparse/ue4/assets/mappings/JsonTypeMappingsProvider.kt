package me.fungames.jfortniteparse.ue4.assets.mappings

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import me.fungames.jfortniteparse.ue4.assets.objects.Struct

abstract class JsonTypeMappingsProvider : TypeMappingsProvider() {
    protected fun addStructs(json: JsonElement): Boolean {
        if (json !is JsonArray) return false
        for (entry in json) {
            if (entry as? JsonObject == null) continue
            val structEntry = Struct(mappings, entry)
            mappings.types[structEntry.name] = structEntry
        }
        return true
    }

    protected fun addEnums(json: JsonElement): Boolean {
        if (json !is JsonArray) return false
        for (entry in json) {
            if (entry as? JsonObject == null) continue
            val values = entry["values"].asJsonArray.map { it.asString }
            var i = 0
            mappings.enums[entry["name"].asString] = values
        }
        return true
    }
}