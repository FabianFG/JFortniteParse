package me.fungames.jfortniteparse.ue4.assets

import me.fungames.jfortniteparse.fort.exports.*
import me.fungames.jfortniteparse.fort.exports.actors.*
import me.fungames.jfortniteparse.fort.exports.components.FortPoiCollisionComponent
import me.fungames.jfortniteparse.fort.exports.components.FortPoi_DiscoverableComponent
import me.fungames.jfortniteparse.fort.exports.variants.*
import me.fungames.jfortniteparse.fort.objects.rows.*
import me.fungames.jfortniteparse.ue4.assets.exports.*
import me.fungames.jfortniteparse.ue4.assets.exports.actors.*
import me.fungames.jfortniteparse.ue4.assets.exports.components.*
import me.fungames.jfortniteparse.ue4.assets.exports.mats.UMaterial
import me.fungames.jfortniteparse.ue4.assets.exports.mats.UMaterialInstance
import me.fungames.jfortniteparse.ue4.assets.exports.mats.UMaterialInstanceConstant
import me.fungames.jfortniteparse.ue4.assets.exports.mats.UMaterialInterface
import me.fungames.jfortniteparse.ue4.assets.exports.tex.UTexture
import me.fungames.jfortniteparse.ue4.assets.exports.tex.UTexture2D
import me.fungames.jfortniteparse.ue4.objects.FScalableFloat
import me.fungames.jfortniteparse.ue4.objects.core.math.FRandomStream
import me.fungames.jfortniteparse.ue4.objects.core.math.FTransform
import me.fungames.jfortniteparse.ue4.objects.engine.curves.UCurveFloat
import me.fungames.jfortniteparse.ue4.objects.engine.editorframework.UAssetImportData
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTag
import me.fungames.jfortniteparse.valorant.exports.*
import java.util.concurrent.ConcurrentHashMap

object ObjectTypeRegistry {
    val classes = ConcurrentHashMap<String, Class<out UObject>>()
    val structs = ConcurrentHashMap<String, Class<*>>()

    init {
        registerEngine()
        registerFortnite()
    }

    private inline fun registerEngine() {
        // -- Export classes --
        registerClass(UActorComponent::class.java)
        registerClass(UAssetImportData::class.java)
        registerClass(UAudioComponent::class.java)
        registerClass(UBlueprintGeneratedClass::class.java)
        registerClass(UBoxComponent::class.java)
        registerClass(UCapsuleComponent::class.java)
        registerClass(UChildActorComponent::class.java)
        registerClass(UClass::class.java)
        registerClass(UCurveFloat::class.java)
        registerClass(UCurveTable::class.java)
        registerClass(UDataAsset::class.java)
        registerClass(UDataTable::class.java)
        registerClass(UEnum::class.java)
        registerClass(UFunction::class.java)
        registerClass(ULevel::class.java)
        registerClass(ULightComponent::class.java)
        registerClass(ULightComponentBase::class.java)
        registerClass(ULocalLightComponent::class.java)
        registerClass(UMaterial::class.java)
        registerClass(UMaterialInstance::class.java)
        registerClass(UMaterialInstanceConstant::class.java)
        registerClass(UMaterialInterface::class.java)
        registerClass(UMeshComponent::class.java)
        //registerClass(UModel::class.java) not ready for production yet
        registerClass(UPaperSprite::class.java)
        registerClass(UPointLightComponent::class.java)
        registerClass(UPrimaryDataAsset::class.java)
        registerClass(UPrimitiveComponent::class.java)
        registerClass(USCS_Node::class.java)
        registerClass(USceneComponent::class.java)
        registerClass(UScriptStruct::class.java)
        registerClass(UShapeComponent::class.java)
        registerClass(USimpleConstructionScript::class.java)
        registerClass(USkeletalMesh::class.java)
        registerClass(USoundWave::class.java)
        registerClass(USphereComponent::class.java)
        registerClass(USpotLightComponent::class.java)
        registerClass(UStaticMesh::class.java)
        registerClass(UStaticMeshComponent::class.java)
        registerClass(UStreamableRenderAsset::class.java)
        registerClass(UStringTable::class.java)
        registerClass(UTextRenderComponent::class.java)
        registerClass(UTexture2D::class.java)
        registerClass(UTexture::class.java)
        registerClass(UTimelineComponent::class.java)
        registerClass(UTimelineTemplate::class.java)
        registerClass(UUserDefinedEnum::class.java)
        registerClass(UUserDefinedStruct::class.java)
        registerClass(UWidgetBlueprintGeneratedClass::class.java)
        registerClass(UWorld::class.java)

        registerClass(AActor::class.java)
        registerClass(ABrush::class.java)
        registerClass(ALevelBounds::class.java)
        registerClass(ALODActor::class.java)
        registerClass(AStaticMeshActor::class.java)
        registerClass(AVolume::class.java)

        // -- Structs --
        registerStruct(FGameplayTag::class.java)
        registerStruct(FPointerToUberGraphFrame::class.java)
        registerStruct(FRandomStream::class.java)
        registerStruct(FScalableFloat::class.java)
        registerStruct(FTransform::class.java)
    }

    private inline fun registerFortnite() {
        // -- Export classes --
        registerClass(AthenaBackpackItemDefinition::class.java)
        registerClass(AthenaBattleBusItemDefinition::class.java)
        registerClass(AthenaChallengeDisplayData::class.java)
        registerClass(AthenaCharacterItemDefinition::class.java)
        registerClass(AthenaCharacterPartItemDefinition::class.java)
        registerClass(AthenaConsumableEmoteItemDefinition::class.java)
        registerClass(AthenaCosmeticItemDefinition::class.java)
        registerClass(AthenaDailyQuestDefinition::class.java)
        registerClass(AthenaDanceItemDefinition::class.java)
        registerClass(AthenaEmojiItemDefinition::class.java)
        registerClass(AthenaGadgetItemDefinition::class.java)
        registerClass(AthenaGliderItemDefinition::class.java)
        registerClass(AthenaItemShopOfferDisplayData::class.java)
        registerClass(AthenaItemWrapDefinition::class.java)
        registerClass(AthenaLoadingScreenItemDefinition::class.java)
        registerClass(AthenaMusicPackItemDefinition::class.java)
        registerClass(AthenaPetCarrierItemDefinition::class.java)
        registerClass(AthenaPetItemDefinition::class.java)
        registerClass(AthenaPickaxeItemDefinition::class.java)
        registerClass(AthenaRewardEventGraph::class.java)
        registerClass(AthenaRewardEventGraphCosmeticItemDefinition::class.java)
        registerClass(AthenaSkyDiveContrailItemDefinition::class.java)
        registerClass(AthenaSprayItemDefinition::class.java)
        registerClass(AthenaToyItemDefinition::class.java)
        registerClass(AthenaVictoryPoseItemDefinition::class.java)
        registerClass(BuildingTextureData::class.java)
        registerClass(CatalogMessaging::class.java)
        registerClass(CustomAccessoryAttachmentData::class.java)
        registerClass(CustomCharacterAccessoryData::class.java)
        registerClass(CustomCharacterBackpackData::class.java)
        registerClass(CustomCharacterBodyPartData::class.java)
        registerClass(CustomCharacterCharmData::class.java)
        registerClass(CustomCharacterPart::class.java)
        registerClass(CustomCharacterPartData::class.java)
        registerClass(FortAbilityKit::class.java)
        registerClass(FortAbilitySet::class.java)
        registerClass(FortAccoladeItemDefinition::class.java)
        registerClass(FortAccountItemDefinition::class.java)
        registerClass(FortAlterableItemDefinition::class.java)
        registerClass(FortAlterationItemDefinition::class.java)
        registerClass(FortAmmoItemDefinition::class.java)
        registerClass(FortAthenaRewardEventGraphPurchaseToken::class.java)
        registerClass(FortBackpackItemDefinition::class.java)
        registerClass(FortBadgeItemDefinition::class.java)
        registerClass(FortBannerTokenType::class.java)
        registerClass(FortBuildingItemDefinition::class.java)
        registerClass(FortCampaignHeroLoadoutItemDefinition::class.java)
        registerClass(FortCardPackItemDefinition::class.java)
        registerClass(FortChallengeBundleItemDefinition::class.java)
        registerClass(FortChallengeBundleProgressTrackerToken::class.java)
        registerClass(FortChallengeBundleScheduleDefinition::class.java)
        registerClass(FortCharacterType::class.java)
        registerClass(FortCollectionBookData::class.java)
        registerClass(FortCollectionData::class.java)
        registerClass(FortCollectionDataCharacter::class.java)
        registerClass(FortCollectionDataEntry::class.java)
        registerClass(FortCollectionDataEntryCharacter::class.java)
        registerClass(FortCollectionDataEntryFish::class.java)
        registerClass(FortCollectionDataFishing::class.java)
        registerClass(FortCollectionsDataTable::class.java)
        registerClass(FortConditionalResourceItemDefinition::class.java)
        registerClass(FortConsumableAccountItemDefinition::class.java)
        registerClass(FortContextTrapItemDefinition::class.java)
        registerClass(FortConversation::class.java)
        registerClass(FortCosmeticCharacterPartVariant::class.java)
        registerClass(FortCosmeticDynamicVariant::class.java)
        registerClass(FortCosmeticFloatSliderVariant::class.java)
        registerClass(FortCosmeticItemTexture::class.java)
        registerClass(FortCosmeticLoadoutTagDrivenVariant::class.java)
        registerClass(FortCosmeticLockerItemDefinition::class.java)
        registerClass(FortCosmeticMaterialVariant::class.java)
        registerClass(FortCosmeticMeshVariant::class.java)
        registerClass(FortCosmeticNumericalVariant::class.java)
        registerClass(FortCosmeticParticleVariant::class.java)
        registerClass(FortCosmeticProfileBannerVariant::class.java)
        registerClass(FortCosmeticProfileLoadoutVariant::class.java)
        registerClass(FortCosmeticRichColorVariant::class.java)
        registerClass(FortCosmeticVariant::class.java)
        registerClass(FortCosmeticVariantBackedByArray::class.java)
        registerClass(FortCurrencyItemDefinition::class.java)
        registerClass(FortDailyRewardScheduleDefinitions::class.java)
        registerClass(FortDailyRewardScheduleTokenDefinition::class.java)
        registerClass(FortDecoItemDefinition::class.java)
        registerClass(FortDefenderItemDefinition::class.java)
        registerClass(FortEditToolItemDefinition::class.java)
        registerClass(FortEventCurrencyItemDefinitionRedir::class.java)
        registerClass(FortExpeditionItemDefinition::class.java)
        registerClass(FortFeatItemDefinition::class.java)
        registerClass(FortGadgetItemDefinition::class.java)
        registerClass(FortGameplayModifierItemDefinition::class.java)
        registerClass(FortGiftBoxItemDefinition::class.java)
        registerClass(FortHeroClassGameplayDefinition::class.java)
        registerClass(FortHeroGameplayDefinition::class.java)
        registerClass(FortHeroType::class.java)
        registerClass(FortHomebaseBannerColorMap::class.java)
        registerClass(FortHomebaseBannerIconItemDefinition::class.java)
        registerClass(FortHomebaseManager::class.java)
        registerClass(FortHomebaseNodeGameplayEffectDataTable::class.java)
        registerClass(FortHomebaseNodeItemDefinition::class.java)
        registerClass(FortIngredientItemDefinition::class.java)
        registerClass(FortItemAccessTokenType::class.java)
        registerClass(FortItemCacheItemDefinition::class.java)
        registerClass(FortItemCategory::class.java)
        registerClass(FortItemDefToItemVariantDataMapping::class.java)
        registerClass(FortItemDefinition::class.java)
        registerClass(FortItemIconDefinition::class.java)
        registerClass(FortItemSeriesDefinition::class.java)
        registerClass(FortItemVariantData::class.java)
        registerClass(FortMedalsPunchCardItemDefinition::class.java)
        registerClass(FortMontageItemDefinitionBase::class.java)
        registerClass(FortMtxOfferData::class.java)
        registerClass(FortNeverPersistItemDefinition::class.java)
        registerClass(FortPersistableItemDefinition::class.java)
        registerClass(FortPersistentResourceItemDefinition::class.java)
        registerClass(FortPersonalVehicleItemDefinition::class.java)
        registerClass(FortPlaysetGrenadeItemDefinition::class.java)
        registerClass(FortPoiCollisionComponent::class.java)
        registerClass(FortPoi_DiscoverableComponent::class.java)
        registerClass(FortPrerollDataItemDefinition::class.java)
        registerClass(FortProfileItemDefinition::class.java)
        registerClass(FortQuestItemDefinition::class.java)
        registerClass(FortQuotaItemDefinition::class.java)
        registerClass(FortRarityData::class.java)
        registerClass(FortRepeatableDailiesCardItemDefinition::class.java)
        registerClass(FortResourceItemDefinition::class.java)
        registerClass(FortSchematicItemDefinition::class.java)
        registerClass(FortStatItemDefinition::class.java)
        registerClass(FortTandemCharacterData::class.java)
        registerClass(FortTeamPerkItemDefinition::class.java)
        registerClass(FortTokenType::class.java)
        registerClass(FortTrapItemDefinition::class.java)
        registerClass(FortVariantTokenType::class.java)
        registerClass(FortWeaponAdditionalData_AudioVisualizerData::class.java)
        registerClass(FortWeaponAdditionalData_SingleWieldState::class.java)
        registerClass(FortWeaponItemDefinition::class.java)
        registerClass(FortWeaponMeleeDualWieldItemDefinition::class.java)
        registerClass(FortWeaponMeleeItemDefinition::class.java)
        registerClass(FortWeaponRangedItemDefinition::class.java)
        registerClass(FortWorkerType::class.java)
        registerClass(FortWorldItemDefinition::class.java)
        registerClass(MarshalledVFX_AuthoredDataConfig::class.java)
        registerClass(McpItemDefinitionBase::class.java)
        registerClass(MyTownData::class.java)
        registerClass(RewardGraphToken::class.java)
        registerClass(VariantTypeBase::class.java)
        registerClass(VariantTypeMaterials::class.java)
        registerClass(VariantTypeParticles::class.java)
        registerClass(VariantTypeSounds::class.java)

        registerClass(BGAConsumableSpawner::class.java)
        registerClass(BuildingActor::class.java)
        registerClass(BuildingAutoNav::class.java)
        registerClass(BuildingContainer::class.java)
        registerClass(BuildingCorner::class.java)
        registerClass(BuildingDeco::class.java)
        registerClass(BuildingFloor::class.java)
        registerClass(BuildingFoundation::class.java)
        registerClass(BuildingFoundation3x3::class.java)
        registerClass(BuildingFoundation5x5::class.java)
        registerClass(BuildingFoundation5x10::class.java)
        registerClass(BuildingGameplayActor::class.java)
        registerClass(BuildingGameplayActorSpawnMachine::class.java)
        registerClass(BuildingProp::class.java)
        registerClass(BuildingPropCorner::class.java)
        registerClass(BuildingPropSimpleInteract::class.java)
        registerClass(BuildingPropWall::class.java)
        registerClass(BuildingRoof::class.java)
        registerClass(BuildingStairs::class.java)
        registerClass(BuildingSMActor::class.java)
        registerClass(BuildingTimeOfDayLights::class.java)
        registerClass(BuildingWall::class.java)
        //registerClass(FortPoiVolume::class.java)
        registerClass(FortStaticMeshActor::class.java)

        // -- Data table row structs --
        registerStruct(AlterationGroup::class.java)
        registerStruct(AlterationIntrinsicMapping::class.java)
        registerStruct(AlterationMapping::class.java)
        registerStruct(AlterationNamedExclusions::class.java)
        registerStruct(AlterationSlotDefinition::class.java)
        registerStruct(AlterationSlotsLoadout::class.java)
        registerStruct(AthenaDynamicRestedXpGoldenPath::class.java)
        registerStruct(AthenaDynamicRestedXpProgression::class.java)
        registerStruct(AthenaExtendedXPCurveEntry::class.java)
        registerStruct(AthenaSeasonalXPCurveEntry::class.java)
        registerStruct(CosmeticFilterTagDataRow::class.java)
        registerStruct(CosmeticMarkupTagDataRow::class.java)
        registerStruct(CosmeticSetDataRow::class.java)
        registerStruct(ExpeditionSlot::class.java)
        registerStruct(FortBadgeScoringData::class.java)
        registerStruct(FortBaseWeaponStats::class.java)
        registerStruct(FortCategoryTableRow::class.java)
        registerStruct(FortCollectionBookDirectPurchaseData::class.java)
        registerStruct(FortCollectionBookPageCategoryTableRow::class.java)
        registerStruct(FortCollectionBookPageData::class.java)
        registerStruct(FortCollectionBookSectionData::class.java)
        registerStruct(FortCollectionBookSlotData::class.java)
        registerStruct(FortCollectionBookSlotSourceData::class.java)
        registerStruct(FortCollectionBookSlotXPWeightData::class.java)
        registerStruct(FortCollectionBookXPData::class.java)
        registerStruct(FortConversionControlKeyCosts::class.java)
        registerStruct(FortCriteriaRequirementData::class.java)
        registerStruct(FortLoginReward::class.java)
        registerStruct(FortLootLevelData::class.java)
        registerStruct(FortLootPackageData::class.java)
        registerStruct(FortLootTierData::class.java)
        registerStruct(FortMeleeWeaponStats::class.java)
        registerStruct(FortPawnStats::class.java)
        registerStruct(FortPhoenixLevelRewardData::class.java)
        registerStruct(FortPlayerPawnStats::class.java)
        registerStruct(FortPostMaxPhoenixLevelRewardData::class.java)
        registerStruct(FortQuestObjectiveStatTableRow::class.java)
        registerStruct(FortQuestRewardTableRow::class.java)
        registerStruct(FortRangedWeaponStats::class.java)
        registerStruct(FortSquadIconData::class.java)
        registerStruct(FortTrapStats::class.java)
        registerStruct(FortWeaponAlterationRarityMappingData::class.java)
        registerStruct(FortWeaponDurabilityByRarityStats::class.java)
        registerStruct(GameDifficultyInfo::class.java)
        registerStruct(HomebaseBannerCategoryData::class.java)
        registerStruct(HomebaseBannerColorData::class.java)
        registerStruct(HomebaseBannerIconData::class.java)
        registerStruct(HomebaseNodeGameplayEffectDataTableRow::class.java)
        registerStruct(HomebaseSquad::class.java)
        registerStruct(ItemPromotionCosts::class.java)
        registerStruct(Recipe::class.java)
        registerStruct(TransmogSacrifice::class.java)
        registerStruct(WeaponUpgradeItemRow::class.java)
    }

    private inline fun registerValorant() {
        registerClass(CharacterAbilityUIData::class.java)
        registerClass(CharacterDataAsset::class.java)
        registerClass(CharacterRoleDataAsset::class.java)
        registerClass(CharacterRoleUIData::class.java)
        registerClass(CharacterUIData::class.java)
    }

    fun registerClass(clazz: Class<out UObject>) {
        var name = clazz.simpleName
        if ((name[0] == 'U' || name[0] == 'A') && name[1].isUpperCase()) {
            name = name.substring(1)
        }
        registerClass(name, clazz)
    }

    fun registerClass(serializedName: String, clazz: Class<out UObject>) {
        classes[serializedName] = clazz
    }

    fun registerStruct(clazz: Class<*>) {
        var name = clazz.simpleName
        if (name[0] == 'F' && name[1].isUpperCase()) {
            name = name.substring(1)
        }
        registerStruct(name, clazz)
    }

    fun registerStruct(serializedName: String, clazz: Class<*>) {
        structs[serializedName] = clazz
    }

    fun get(name: String) = classes[name] ?: structs[name]
}

fun String.unprefix(): String {
    if ((get(0) == 'U' || get(0) == 'F' || get(0) == 'A') && get(1).isUpperCase()) {
        return substring(1)
    }
    return this
}