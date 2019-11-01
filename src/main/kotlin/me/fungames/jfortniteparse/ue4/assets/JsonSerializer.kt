package me.fungames.jfortniteparse.ue4.assets

import com.github.salomonbrys.kotson.*
import com.google.gson.*
import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.FGuid
import me.fungames.jfortniteparse.ue4.assets.util.FName

object JsonSerializer{
    val packageConverter = jsonSerializer<Package> { arg ->
        jsonObject(
            "import_map" to arg.context.serialize(arg.src.importMap),
            "export_map" to arg.context.serialize(arg.src.exportMap),
            "export_properties" to arg.context.serialize(arg.src.exports.map { it.baseObject })
        )
    }
    val importSerializer = jsonSerializer<FObjectImport> {
        jsonObject(
            "class_name" to it.src.className.text,
            "class_package" to it.src.classPackage.text,
            "object_name" to it.src.objectName.text
        )
    }
    val exportSerializer = jsonSerializer<FObjectExport> {
        jsonObject(
            "export_type" to it.src.classIndex.importName,
            "export_offset" to it.src.serialOffset,
            "export_length" to it.src.serialSize
        )
    }

    @Suppress("EXPERIMENTAL_API_USAGE")
    fun FPropertyTagType.toJson(context: JsonSerializationContext) : JsonElement {
        if(this is FPropertyTagType.StructProperty) return this.struct.toJson(context)
        return when(val tagValue = this.getTagTypeValue()) {
            is UScriptArray -> jsonArray(tagValue.contents.map { it.toJson(context) })
            is UScriptMap -> jsonArray(tagValue.mapData.map { jsonObject("key" to it.key.toJson(context), "value" to it.value.toJson(context)) })
            is UScriptStruct -> tagValue.toJson(context)
            is Boolean -> JsonPrimitive(tagValue)
            is Int -> JsonPrimitive(tagValue)
            is UShort -> JsonPrimitive(tagValue.toShort())
            is UInt -> JsonPrimitive(tagValue.toInt())
            is ULong -> JsonPrimitive(tagValue.toLong())
            is Byte -> JsonPrimitive(tagValue)
            is Float -> JsonPrimitive(tagValue)
            is String -> JsonPrimitive(tagValue)
            is FName -> JsonPrimitive(tagValue.text)
            is FText -> jsonObject("namespace" to tagValue.nameSpace, "key" to tagValue.key, "source_string" to tagValue.text)
            is FPackageIndex -> JsonPrimitive(tagValue.importName)
            is UInterfaceProperty -> JsonPrimitive(tagValue.interfaceNumber.toInt())
            is FSoftObjectPath -> jsonObject("asset_path" to tagValue.assetPathName.text, "sub_path" to tagValue.subPathString)
            is FGuid -> JsonPrimitive(tagValue.toString())
            else -> throw ParserException("Unknown tag value ${tagValue::class.java.simpleName}, cannot be serialized to json")
        }
    }

    @Suppress("EXPERIMENTAL_API_USAGE")
    fun UScriptStruct.toJson(context: JsonSerializationContext) : JsonElement {
        return when(val ob = this.structType) {
            is FIntPoint -> jsonObject("X" to ob.x, "Y" to ob.y)
            is FGuid -> JsonPrimitive(ob.toString())
            is FGameplayTagContainer -> jsonArray(ob.gameplayTags.map { it.text })
            is FColor -> jsonObject("R" to ob.r, "B" to ob.b, "G" to ob.g, "A" to ob.a)
            is FLinearColor -> jsonObject("R" to ob.r, "B" to ob.b, "G" to ob.g, "A" to ob.a)
            is FSoftObjectPath -> jsonObject("asset_path" to ob.assetPathName.text, "sub_path" to ob.subPathString)
            is FStructFallback -> {
                val jsOb = JsonObject()
                ob.properties.forEach { jsOb[it.name.text] = it.tag!!.toJson(context) }
                jsOb
            }
            is FVector2D -> jsonObject("X" to ob.x, "Y" to ob.y)
            is FQuat -> jsonObject("X" to ob.x, "Y" to ob.y, "Z" to ob.z, "W" to ob.w)
            is FVector -> jsonObject("X" to ob.x, "Y" to ob.y, "Z" to ob.z)
            is FRotator -> jsonObject("Pitch" to ob.pitch, "Yaw" to ob.yaw, "Roll" to ob.roll)
            is FPerPlatformFloat -> JsonPrimitive(ob.value)
            is FPerPlatformInt -> JsonPrimitive(ob.value.toInt())
            is FWeightedRandomSampler -> jsonObject("alias" to ob.alias, "prob" to ob.prob, "totalWeight" to ob.totalWeight)
            is FLevelSequenceLegacyObjectReference -> jsonObject(
                "key_guid" to ob.keyGuid.toString(),
                "object_id" to ob.objectId.toString(),
                "object_path" to ob.objectPath
                //theres more but not implemented for json
            )
            is FFrameNumber -> JsonPrimitive(ob.value)
            is FSmartName -> JsonPrimitive(ob.displayName.text)
            is FRichCurveKey -> jsonObject(
                "interp_mode" to ob.interpMode,
                "tangent_mode" to ob.tangentMode,
                "tangent_weight_mode" to ob.tangentWeightMode,
                "time" to ob.time,
                "arrive_tangent" to ob.arriveTangent,
                "arrive_tangent_weight" to ob.arriveTangentWeight,
                "leave_tangent" to ob.leaveTangent,
                "leave_tangent_weight" to ob.leaveTangentWeight
            )
            is FSimpleCurveKey -> jsonObject("time" to ob.time, "value" to ob.value)
            is FDateTime -> JsonPrimitive(ob.date)
            else -> throw ParserException("Unknown tag value ${this::class.java.simpleName}, cannot be serialized to json")
        }
    }

    val uobjectSerializer = jsonSerializer<UObject> {
        val ob = jsonObject("export_type" to it.src.exportType)
        it.src.properties.forEach {pTag ->
            val tagValue = pTag.tag ?: return@forEach
            ob[pTag.name.text] = tagValue.toJson(it.context)
        }
        ob
    }
}