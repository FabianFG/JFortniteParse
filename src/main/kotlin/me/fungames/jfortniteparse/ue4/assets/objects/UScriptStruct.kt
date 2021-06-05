package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.LOG_JFP
import me.fungames.jfortniteparse.fort.objects.FFortActorRecord
import me.fungames.jfortniteparse.ue4.assets.objects.FProperty.Companion.valueOr
import me.fungames.jfortniteparse.ue4.assets.objects.FProperty.ReadType
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter
import me.fungames.jfortniteparse.ue4.objects.ai.navigation.FNavAgentSelector
import me.fungames.jfortniteparse.ue4.objects.core.math.*
import me.fungames.jfortniteparse.ue4.objects.core.misc.FDateTime
import me.fungames.jfortniteparse.ue4.objects.core.misc.FFrameNumber
import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid
import me.fungames.jfortniteparse.ue4.objects.engine.*
import me.fungames.jfortniteparse.ue4.objects.engine.animation.FSmartName
import me.fungames.jfortniteparse.ue4.objects.engine.curves.FRichCurveKey
import me.fungames.jfortniteparse.ue4.objects.engine.curves.FSimpleCurveKey
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagContainer
import me.fungames.jfortniteparse.ue4.objects.levelsequence.FLevelSequenceObjectReferenceMap
import me.fungames.jfortniteparse.ue4.objects.moviescene.FMovieSceneFrameRange
import me.fungames.jfortniteparse.ue4.objects.moviescene.evaluation.FMovieSceneEvaluationKey
import me.fungames.jfortniteparse.ue4.objects.moviescene.evaluation.FMovieSceneEvaluationTemplate
import me.fungames.jfortniteparse.ue4.objects.moviescene.evaluation.FMovieSceneSegment
import me.fungames.jfortniteparse.ue4.objects.moviescene.evaluation.FSectionEvaluationDataTree
import me.fungames.jfortniteparse.ue4.objects.niagara.FNiagaraVariable
import me.fungames.jfortniteparse.ue4.objects.niagara.FNiagaraVariableBase
import me.fungames.jfortniteparse.ue4.objects.niagara.FNiagaraVariableWithOffset
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftClassPath
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath

class UScriptStruct {
    val structName: FName
    var structType: Any

    constructor(Ar: FAssetArchive, typeData: PropertyType, type: ReadType = ReadType.NORMAL) {
        structName = typeData.structName
        structType = when (structName.text) { // TODO please complete the zero constructors
            "Box" -> valueOr({ FBox(Ar) }, { FBox() }, type)
            "Box2D" -> valueOr({ FBox2D(Ar) }, { FBox2D() }, type)
            "Color" -> valueOr({ FColor(Ar) }, { FColor() }, type)
            "ColorMaterialInput" -> FColorMaterialInput(Ar)
            "DateTime", "Timespan" -> valueOr({ FDateTime(Ar) }, { FDateTime() }, type)
            "ExpressionInput" -> FExpressionInput(Ar)
            "FrameNumber" -> FFrameNumber(Ar)
            "GameplayTagContainer" -> valueOr({ FGameplayTagContainer(Ar) }, { FGameplayTagContainer() }, type)
            "Guid", "GUID" -> valueOr({ FGuid(Ar) }, { FGuid() }, type)
            "IntPoint" -> FIntPoint(Ar)
            "IntVector" -> FIntVector(Ar)
            "LevelSequenceObjectReferenceMap" -> FLevelSequenceObjectReferenceMap(Ar)
            "LinearColor" -> valueOr({ FLinearColor(Ar) }, { FLinearColor() }, type)
            "MaterialAttributesInput" -> FMaterialAttributesInput(Ar)
            "MovieSceneEvaluationKey" -> FMovieSceneEvaluationKey(Ar)
            "MovieSceneEvaluationTemplate" -> FMovieSceneEvaluationTemplate(Ar)
            "MovieSceneFloatValue" -> FRichCurveKey(Ar)
            "MovieSceneFrameRange" -> FMovieSceneFrameRange(Ar)
            "MovieSceneSegment" -> FMovieSceneSegment(Ar)
            "MovieSceneSegmentIdentifier" -> FFrameNumber(Ar)
            "MovieSceneSequenceID" -> FFrameNumber(Ar)
            "MovieSceneTrackIdentifier" -> FFrameNumber(Ar)
            "NavAgentSelector" -> FNavAgentSelector(Ar)
            "NiagaraVariable" -> FNiagaraVariable(Ar)
            "NiagaraVariableBase" -> FNiagaraVariableBase(Ar)
            "NiagaraVariableWithOffset" -> FNiagaraVariableWithOffset(Ar)
            "PerPlatformBool" -> FPerPlatformBool(Ar)
            "PerPlatformFloat" -> FPerPlatformFloat(Ar)
            "PerPlatformInt" -> FPerPlatformInt(Ar)
            "Quat" -> FQuat(Ar)
            "RichCurveKey" -> FRichCurveKey(Ar)
            "Rotator" -> valueOr({ FRotator(Ar) }, { FRotator() }, type)
            "ScalarMaterialInput" -> FScalarMaterialInput(Ar)
            "SectionEvaluationDataTree" -> FSectionEvaluationDataTree(Ar)
            "SimpleCurveKey" -> FSimpleCurveKey(Ar)
            "SkeletalMeshSamplingLODBuiltData" -> FWeightedRandomSampler(Ar)
            "SmartName" -> FSmartName(Ar)
            "SoftObjectPath" -> valueOr({ FSoftObjectPath(Ar) }, { FSoftObjectPath() }, type).apply { owner = Ar.owner }
            "SoftClassPath" -> valueOr({ FSoftClassPath(Ar) }, { FSoftClassPath() }, type).apply { owner = Ar.owner }
            "Vector" -> valueOr({ FVector(Ar) }, { FVector() }, type)
            "Vector2D" -> valueOr({ FVector2D(Ar) }, { FVector2D() }, type)
            "Vector2MaterialInput" -> FVector2MaterialInput(Ar)
            "Vector4" -> valueOr({ FVector4(Ar) }, { FVector4() }, type)
            "VectorMaterialInput" -> FVectorMaterialInput(Ar)
            "FortActorRecord" -> FFortActorRecord(Ar)

            else -> {
                LOG_JFP.debug("Using property serialization for struct $structName")
                //TODO this should in theory map the struct fallbacks directly to their target, not implemented yet
                //For now it will be done with the getTagTypeValue method, not optimal though
                FStructFallback(Ar, typeData.structClass, structName)
            }
        }
    }

    fun serialize(Ar: FAssetArchiveWriter) {
        when (val structType = structType) {
            is FBox -> structType.serialize(Ar)
            is FBox2D -> structType.serialize(Ar)
            is FColor -> structType.serialize(Ar)
            is FColorMaterialInput -> structType.serialize(Ar)
            is FDateTime -> structType.serialize(Ar)
            is FExpressionInput -> structType.serialize(Ar)
            is FFrameNumber -> structType.serialize(Ar)
            is FGameplayTagContainer -> structType.serialize(Ar)
            is FGuid -> structType.serialize(Ar)
            is FIntPoint -> structType.serialize(Ar)
            is FIntVector -> structType.serialize(Ar)
            is FLevelSequenceObjectReferenceMap -> structType.serialize(Ar)
            is FLinearColor -> structType.serialize(Ar)
            is FMaterialAttributesInput -> structType.serialize(Ar)
            is FMovieSceneEvaluationKey -> structType.serialize(Ar)
            is FMovieSceneEvaluationTemplate -> structType.serialize(Ar)
            is FMovieSceneFrameRange -> structType.serialize(Ar)
            is FMovieSceneSegment -> structType.serialize(Ar)
            is FNavAgentSelector -> structType.serialize(Ar)
            is FNiagaraVariable -> structType.serialize(Ar)
            is FNiagaraVariableBase -> structType.serialize(Ar)
            is FNiagaraVariableWithOffset -> structType.serialize(Ar)
            is FPerPlatformBool -> structType.serialize(Ar)
            is FPerPlatformFloat -> structType.serialize(Ar)
            is FPerPlatformInt -> structType.serialize(Ar)
            is FQuat -> structType.serialize(Ar)
            is FRichCurveKey -> structType.serialize(Ar)
            is FRotator -> structType.serialize(Ar)
            is FScalarMaterialInput -> structType.serialize(Ar)
            is FSectionEvaluationDataTree -> structType.serialize(Ar)
            is FSimpleCurveKey -> structType.serialize(Ar)
            is FSmartName -> structType.serialize(Ar)
            is FSoftObjectPath -> structType.serialize(Ar)
            is FStructFallback -> structType.serialize(Ar)
            is FVector -> structType.serialize(Ar)
            is FVector2D -> structType.serialize(Ar)
            is FVector2MaterialInput -> structType.serialize(Ar)
            is FVector4 -> structType.serialize(Ar)
            is FVectorMaterialInput -> structType.serialize(Ar)
            is FWeightedRandomSampler -> structType.serialize(Ar)
        }
    }

    constructor(structName: FName, structType: Any) {
        this.structName = structName
        this.structType = structType
    }
}