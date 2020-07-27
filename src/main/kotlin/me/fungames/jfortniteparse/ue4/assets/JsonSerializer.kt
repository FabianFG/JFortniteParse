@file:Suppress("EXPERIMENTAL_API_USAGE")

package me.fungames.jfortniteparse.ue4.assets

import com.github.salomonbrys.kotson.*
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.assets.exports.UObject
import me.fungames.jfortniteparse.ue4.assets.objects.*
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText
import me.fungames.jfortniteparse.ue4.objects.core.math.*
import me.fungames.jfortniteparse.ue4.objects.core.misc.FDateTime
import me.fungames.jfortniteparse.ue4.objects.core.misc.FFrameNumber
import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid
import me.fungames.jfortniteparse.ue4.objects.coreuobject.uobject.*
import me.fungames.jfortniteparse.ue4.objects.detailcustomizations.FNavAgentSelectorCustomization
import me.fungames.jfortniteparse.ue4.objects.engine.*
import me.fungames.jfortniteparse.ue4.objects.engine.animation.FSmartName
import me.fungames.jfortniteparse.ue4.objects.engine.curves.FRichCurveKey
import me.fungames.jfortniteparse.ue4.objects.engine.curves.FSimpleCurveKey
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagContainer
import me.fungames.jfortniteparse.ue4.objects.levelsequence.FLevelSequenceLegacyObjectReference
import me.fungames.jfortniteparse.ue4.objects.moviescene.FMovieSceneFrameRange
import me.fungames.jfortniteparse.ue4.objects.moviescene.evaluation.*

object JsonSerializer {
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
            "export_type" to it.src.classIndex.name,
            "export_offset" to it.src.serialOffset,
            "export_length" to it.src.serialSize
        )
    }

    @Suppress("EXPERIMENTAL_API_USAGE")
    fun FPropertyTagType.toJson(context: JsonSerializationContext): JsonElement {
        return this.getTagTypeValueLegacy().toJson(context)
    }

    @Suppress("EXPERIMENTAL_API_USAGE")
    fun Any.toJson(context: JsonSerializationContext): JsonElement {
        return when (val ob = this) {
            //Basic Tag Types
            is Enum<*> -> JsonPrimitive(ob.name)
            is Array<*> -> jsonArray(ob.map { it?.toJson(context) })
            is Iterable<*> -> jsonArray(ob.map { it?.toJson(context) })
            is UScriptArray -> jsonArray(ob.contents.map { it.toJson(context) })
            is UScriptMap -> jsonArray(ob.mapData.map { jsonObject("key" to it.key.toJson(context), "value" to it.value.toJson(context)) })
            is UScriptStruct -> ob.structType.toJson(context)
            is Boolean -> JsonPrimitive(ob)
            is Int -> JsonPrimitive(ob)
            is UShort -> JsonPrimitive(ob.toInt())
            is UInt -> JsonPrimitive(ob.toLong())
            is ULong -> JsonPrimitive(ob.toLong())
            is UByte -> JsonPrimitive(ob.toShort())
            is Float -> JsonPrimitive(ob)
            is String -> JsonPrimitive(ob)
            is FName -> JsonPrimitive(ob.text)
            is FText -> jsonObject("historyType" to ob.historyType.toJson(context), "finalText" to ob.text, "value" to context.serialize(ob.textHistory))
            is FPackageIndex -> JsonPrimitive(ob.name)
            is UInterfaceProperty -> JsonPrimitive(ob.interfaceNumber.toInt())
            is FSoftObjectPath -> jsonObject("assetPath" to ob.assetPathName.text, "subPath" to ob.subPathString)
            is FGuid -> JsonPrimitive(ob.toString())
            is Double -> JsonPrimitive(ob)
            is Byte -> JsonPrimitive(ob)
            is Short -> JsonPrimitive(ob)
            is Long -> JsonPrimitive(ob)

            //Structs
            is FIntPoint -> jsonObject("X" to ob.x.toLong(), "Y" to ob.y.toLong())
            is FGameplayTagContainer -> ob.gameplayTags.toJson(context)
            is FColor -> jsonObject("R" to ob.r.toShort(), "B" to ob.b.toShort(), "G" to ob.g.toShort(), "A" to ob.a)
            is FLinearColor -> jsonObject("R" to ob.r, "B" to ob.b, "G" to ob.g, "A" to ob.a)
            is FStructFallback -> {
                val jsOb = JsonObject()
                ob.properties.forEach { jsOb[it.name.text] = it.tag!!.toJson(context) }
                jsOb
            }
            is FVector2D -> jsonObject("X" to ob.x, "Y" to ob.y)
            is FQuat -> jsonObject("X" to ob.x, "Y" to ob.y, "Z" to ob.z, "W" to ob.w)
            is FVector -> jsonObject("X" to ob.x, "Y" to ob.y, "Z" to ob.z)
            is FVector4 -> jsonObject("X" to ob.x, "Y" to ob.y, "Z" to ob.z, "W" to ob.w)
            is FRotator -> jsonObject("Pitch" to ob.pitch, "Yaw" to ob.yaw, "Roll" to ob.roll)
            is FPerPlatformInt -> JsonPrimitive(ob.value.toLong())
            is FPerPlatformFloat -> JsonPrimitive(ob.value)
            is FPerPlatformBool -> JsonPrimitive(ob.value)
            is FWeightedRandomSampler -> jsonObject("alias" to ob.alias.toJson(context), "prob" to ob.prob.toJson(context), "totalWeight" to ob.totalWeight)
            is FLevelSequenceLegacyObjectReference -> jsonObject(
                "keyGuid" to ob.keyGuid.toJson(context),
                "objectId" to ob.objectId.toJson(context),
                "objectPath" to ob.objectPath
                //theres more but not implemented for json
            )
            is FFrameNumber -> JsonPrimitive(ob.value)
            is FSmartName -> JsonPrimitive(ob.displayName.text)
            is FRichCurveKey -> jsonObject(
                "interpMode" to ob.interpMode,
                "tangentMode" to ob.tangentMode,
                "tangentWeightMode" to ob.tangentWeightMode,
                "time" to ob.time,
                "arriveTangent" to ob.arriveTangent,
                "arriveTangentWeight" to ob.arriveTangentWeight,
                "leaveTangent" to ob.leaveTangent,
                "leaveTangentWeight" to ob.leaveTangentWeight
            )
            is FSimpleCurveKey -> jsonObject("time" to ob.time, "value" to ob.value)
            is FDateTime -> JsonPrimitive(ob.date)
            is FNavAgentSelectorCustomization -> jsonObject("supportedDesc" to ob.supportedDesc.toJson(context))
            /*is FVectorMaterialInput -> jsonObject(
                "parent" to ob.parent.toJson(context),
                "useConstant" to ob.useConstant,
                "constant" to ob.constant.toJson(context),
                "temp" to ob.temp,
                "tempType" to ob.tempType
            )
            is FColorMaterialInput -> jsonObject(
                "parent" to ob.parent.toJson(context),
                "useConstant" to ob.useConstant,
                "constant" to ob.constant.toJson(context),
                "temp" to ob.temp,
                "tempType" to ob.tempType
            )*/
            is FExpressionInput -> jsonObject(
                "outputIndex" to ob.outputIndex,
                "inputName" to ob.inputName.toJson(context),
                "mask" to ob.mask,
                "maskR" to ob.maskR,
                "maskG" to ob.maskG,
                "maskB" to ob.maskB,
                "maskA" to ob.maskA,
                "expressionName" to ob.expressionName.toJson(context)
            )
            is FMovieSceneSegment -> jsonObject(
                "range" to ob.range.toJson(context),
                "id" to ob.id,
                "allowEmpty" to ob.allowEmpty,
                "impls" to ob.impls.toJson(context)
            )
            is TRange<*> -> jsonObject(
                "lowerBound" to ob.lowerBound.toJson(context),
                "upperBound" to ob.upperBound.toJson(context)
            )
            is TRangeBound<*> -> jsonObject(
                "type" to ob.type,
                "value" to ob.value
            )
            is FSectionEvaluationDataTree -> ob.tree.toJson(context)
            is TMovieSceneEvaluationTree<*> -> jsonObject(
                "rootNode" to ob.rootNode.toJson(context),
                "childNodes" to ob.childNodes.toJson(context),
                "data" to ob.data.toJson(context)
            )
            is FMovieSceneEvaluationTree -> jsonObject(
                "rootNode" to ob.rootNode.toJson(context),
                "childNodes" to ob.childNodes.toJson(context)
            )
            is FMovieSceneEvaluationTreeNode -> jsonObject(
                "range" to ob.range.toJson(context),
                "parent" to ob.parent.toJson(context),
                "childrenId" to ob.childrenId.toJson(context),
                "dataId" to ob.dataId.toJson(context)
            )
            is FMovieSceneEvaluationTreeNodeHandle -> jsonObject(
                "childrenHandle" to ob.childrenHandle.toJson(context),
                "index" to ob.index
            )
            is FEvaluationTreeEntryHandle -> JsonPrimitive(ob.entryIndex)

            is TEvaluationTreeEntryContainer.FEntry -> jsonObject(
                "startIndex" to ob.startIndex,
                "size" to ob.size,
                "capacity" to ob.capacity
            )

            is TEvaluationTreeEntryContainer<*> -> jsonObject(
                "entries" to ob.entries.toJson(context),
                "items" to ob.items.toJson(context)
            )

            is FMovieSceneFrameRange -> ob.value.toJson(context)
            is FMovieSceneEvaluationKey -> jsonObject(
                "sequenceId" to ob.sequenceId.toLong(),
                "trackId" to ob.trackId,
                "sectionIndex" to ob.sectionIndex.toLong()
            )
            is FMovieSceneEvaluationTemplate -> JsonPrimitive(ob.value.toLong())
            else -> throw ParserException("Unknown tag value ${ob::class.java.simpleName}, cannot be serialized to json")
        }
    }

    @Suppress("EXPERIMENTAL_API_USAGE")
    val uobjectSerializer = jsonSerializer<UObject> {
        val ob = jsonObject("exportType" to it.src.exportType)
        it.src.properties.forEach { pTag ->
            val tagValue = pTag.tag ?: return@forEach
            ob[pTag.name.text] = tagValue.toJson(it.context)
        }
        ob
    }
}