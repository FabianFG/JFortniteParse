package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.objects.FPropertyTagType.Companion.valueOr
import me.fungames.jfortniteparse.ue4.assets.objects.FPropertyTagType.ReadType
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter
import me.fungames.jfortniteparse.ue4.objects.core.math.*
import me.fungames.jfortniteparse.ue4.objects.core.misc.FDateTime
import me.fungames.jfortniteparse.ue4.objects.core.misc.FFrameNumber
import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid
import me.fungames.jfortniteparse.ue4.objects.detailcustomizations.FNavAgentSelectorCustomization
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
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath

class UScriptStruct : UClass {
    val structName: String
    var structType: Any

    constructor(Ar: FAssetArchive, structName: String, type: ReadType = ReadType.NORMAL) {
        super.init(Ar)
        this.structName = structName
        structType = when (structName) { // TODO please complete the zero constructors
            "Box" -> valueOr({ FBox(Ar) }, { FBox() }, type)
            "Box2D" -> FBox2D(Ar)
            "Color" -> valueOr({ FColor(Ar) }, { FColor() }, type)
            "ColorMaterialInput" -> FColorMaterialInput(Ar)
            "ExpressionInput" -> FExpressionInput(Ar)
            "FrameNumber" -> FFrameNumber(Ar)
            "GameplayTagContainer" -> valueOr({ FGameplayTagContainer(Ar) }, { FGameplayTagContainer() }, type)
            "Guid" -> valueOr({ FGuid(Ar) }, { FGuid() }, type)
            "IntPoint" -> FIntPoint(Ar)
            "IntVector" -> FIntVector(Ar)
            "LevelSequenceObjectReferenceMap" -> FLevelSequenceObjectReferenceMap(Ar)
            "LinearColor" -> valueOr({ FLinearColor(Ar) }, { FLinearColor() }, type)
            "MaterialAttributesInput" -> FExpressionInput(Ar) // FMaterialAttributesInput(Ar)
            "MovieSceneEvaluationKey" -> FMovieSceneEvaluationKey(Ar)
            "MovieSceneEvaluationTemplate" -> FMovieSceneEvaluationTemplate(Ar)
            "MovieSceneFloatValue" -> FRichCurveKey(Ar)
            "MovieSceneFrameRange" -> FMovieSceneFrameRange(Ar)
            "MovieSceneSegment" -> FMovieSceneSegment(Ar)
            "MovieSceneSegmentIdentifier" -> FFrameNumber(Ar)
            "MovieSceneSequenceID" -> FFrameNumber(Ar)
            "MovieSceneTrackIdentifier" -> FFrameNumber(Ar)
            "NavAgentSelector" -> FNavAgentSelectorCustomization(Ar)
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
            "Timespan", "DateTime" -> valueOr({ FDateTime(Ar) }, { FDateTime() }, type)
            "Vector" -> valueOr({ FVector(Ar) }, { FVector() }, type)
            "Vector2D" -> valueOr({ FVector2D(Ar) }, { FVector2D() }, type)
            "Vector2MaterialInput" -> FVector2MaterialInput(Ar)
            "Vector4" -> valueOr({ FVector4(Ar) }, { FVector4() }, type)
            "VectorMaterialInput" -> FVectorMaterialInput(Ar)

            else -> {
                if (Ar.useUnversionedPropertySerialization) {
                    throw ParserException("Unknown struct type $structName, can't proceed with serialization", Ar)
                }
                logger.debug("Using FStructFallback $structName")
                //TODO this should in theory map the struct fallbacks directly to their target, not implemented yet
                //For now it will be done with the getTagTypeValue method, not optimal though
                FStructFallback(Ar)
            }
        }
        super.complete(Ar)
    }

    fun serialize(Ar: FAssetArchiveWriter) {
        super.initWrite(Ar)
        when (val structType = structType) {
            is FIntPoint -> structType.serialize(Ar)
            is FGuid -> structType.serialize(Ar)
            is FGameplayTagContainer -> structType.serialize(Ar)
            is FColor -> structType.serialize(Ar)
            is FLinearColor -> structType.serialize(Ar)
            is FSoftObjectPath -> structType.serialize(Ar)
            is FVector -> structType.serialize(Ar)
            is FVector2D -> structType.serialize(Ar)
            is FVector4 -> structType.serialize(Ar)
            is FBox -> structType.serialize(Ar)
            is FBox2D -> structType.serialize(Ar)
            is FRotator -> structType.serialize(Ar)
            is FQuat -> structType.serialize(Ar)
            is FIntVector -> structType.serialize(Ar)
            is FPerPlatformInt -> structType.serialize(Ar)
            is FPerPlatformFloat -> structType.serialize(Ar)
            is FPerPlatformBool -> structType.serialize(Ar)
            is FWeightedRandomSampler -> structType.serialize(Ar)
            is FLevelSequenceObjectReferenceMap -> structType.serialize(Ar)
            is FStructFallback -> structType.serialize(Ar)
            is FFrameNumber -> structType.serialize(Ar)
            is FSmartName -> structType.serialize(Ar)
            is FRichCurveKey -> structType.serialize(Ar)
            is FSimpleCurveKey -> structType.serialize(Ar)
            is FDateTime -> structType.serialize(Ar)
            is FNavAgentSelectorCustomization -> structType.serialize(Ar)
            is FExpressionInput -> structType.serialize(Ar)
            is FColorMaterialInput -> structType.serialize(Ar)
            is FScalarMaterialInput -> structType.serialize(Ar)
            is FVectorMaterialInput -> structType.serialize(Ar)
            is FVector2MaterialInput -> structType.serialize(Ar)
            // is FMaterialAttributesInput -> structType.serialize(Ar)
            is FMovieSceneSegment -> structType.serialize(Ar)
            is FSectionEvaluationDataTree -> structType.serialize(Ar)
            is FMovieSceneFrameRange -> structType.serialize(Ar)
            is FMovieSceneEvaluationKey -> structType.serialize(Ar)
            is FMovieSceneEvaluationTemplate -> structType.serialize(Ar)
        }
        super.completeWrite(Ar)
    }

    constructor(structName: String, structType: Any) {
        this.structName = structName
        this.structType = structType
    }
}