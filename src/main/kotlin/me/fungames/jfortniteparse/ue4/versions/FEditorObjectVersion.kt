package me.fungames.jfortniteparse.ue4.versions

import me.fungames.jfortniteparse.ue4.reader.FArchive

// Custom serialization version for changes made in Dev-Editor stream
object FEditorObjectVersion {
    // Before any version changes were made
    const val BeforeCustomVersionWasAdded = 0
    // Localizable text gathered and stored in packages is now flagged with a localizable text gathering process version
    const val GatheredTextProcessVersionFlagging = 1
    // Fixed several issues with the gathered text cache stored in package headers
    const val GatheredTextPackageCacheFixesV1 = 2
    // Added support for "root" meta-data (meta-data not associated with a particular object in a package)
    const val RootMetaDataSupport = 3
    // Fixed issues with how Blueprint bytecode was cached
    const val GatheredTextPackageCacheFixesV2 = 4
    // Updated FFormatArgumentData to allow variant data to be marshaled from a BP into C++
    const val TextFormatArgumentDataIsVariant = 5
    // Changes to SplineComponent
    const val SplineComponentCurvesInStruct = 6
    // Updated ComboBox to support toggling the menu open, better controller support
    const val ComboBoxControllerSupportUpdate = 7
    // Refactor mesh editor materials
    const val RefactorMeshEditorMaterials = 8
    // Added UFontFace assets
    const val AddedFontFaceAssets = 9
    // Add UPROPERTY for TMap of Mesh section, so the serialize will be done normally (and export to text will work correctly)
    const val UPropertryForMeshSection = 10
    // Update the schema of all widget blueprints to use the WidgetGraphSchema
    const val WidgetGraphSchema = 11
    // Added a specialized content slot to the background blur widget
    const val AddedBackgroundBlurContentSlot = 12
    // Updated UserDefinedEnums to have stable keyed display names
    const val StableUserDefinedEnumDisplayNames = 13
    // Added "Inline" option to UFontFace assets
    const val AddedInlineFontFaceAssets = 14
    // Fix a serialization issue with static mesh FMeshSectionInfoMap FProperty
    const val UPropertryForMeshSectionSerialize = 15
    // Adding a version bump for the new fast widget construction in case of problems.
    const val FastWidgetTemplates = 16
    // Update material thumbnails to be more intelligent on default primitive shape for certain material types
    const val MaterialThumbnailRenderingChanges = 17
    // Introducing a new clipping system for Slate/UMG
    const val NewSlateClippingSystem = 18
    // MovieScene Meta Data added as native Serialization
    const val MovieSceneMetaDataSerialization = 19
    // Text gathered from properties now adds two variants: a version without the package localization ID (for use at runtime), and a version with it (which is editor-only)
    const val GatheredTextEditorOnlyPackageLocId = 20
    // Added AlwaysSign to FNumberFormattingOptions
    const val AddedAlwaysSignNumberFormattingOption = 21
    // Added additional objects that must be serialized as part of this new material feature
    const val AddedMaterialSharedInputs = 22
    // Added morph target section indices
    const val AddedMorphTargetSectionIndices = 23
    // Serialize the instanced static mesh render data, to avoid building it at runtime
    const val SerializeInstancedStaticMeshRenderData = 24
    // Change to MeshDescription serialization (moved to release)
    const val MeshDescriptionNewSerialization_MovedToRelease = 25
    // New format for mesh description attributes
    const val MeshDescriptionNewAttributeFormat = 26
    // Switch root component of SceneCapture actors from MeshComponent to SceneComponent
    const val ChangeSceneCaptureRootComponent = 27
    // StaticMesh serializes MeshDescription instead of RawMesh
    const val StaticMeshDeprecatedRawMesh = 28
    // MeshDescriptionBulkData contains a Guid used as a DDC key
    const val MeshDescriptionBulkDataGuid = 29
    // Change to MeshDescription serialization (removed FMeshPolygon::HoleContours)
    const val MeshDescriptionRemovedHoles = 30
    // Change to the WidgetCompoent WindowVisibilty default value
    const val ChangedWidgetComponentWindowVisibilityDefault = 31
    // Avoid keying culture invariant display strings during serialization to avoid non-deterministic cooking issues
    const val CultureInvariantTextSerializationKeyStability = 32
    // Change to UScrollBar and UScrollBox thickness property (removed implicit padding of 2, so thickness value must be incremented by 4).
    const val ScrollBarThicknessChange = 33
    // Deprecated LandscapeHoleMaterial
    const val RemoveLandscapeHoleMaterial = 34
    // MeshDescription defined by triangles instead of arbitrary polygons
    const val MeshDescriptionTriangles = 35
    //Add weighted area and angle when computing the normals
    const val ComputeWeightedNormals = 36
    // SkeletalMesh now can be rebuild in editor, no more need to re-import
    const val SkeletalMeshBuildRefactor = 37
    // Move all SkeletalMesh source data into a private uasset in the same package has the skeletalmesh
    const val SkeletalMeshMoveEditorSourceDataToPrivateAsset = 38
    // Parse text only if the number is inside the limits of its type
    const val NumberParsingOptionsNumberLimitsAndClamping = 39
    //Make sure we can have more then 255 material in the skeletal mesh source data
    const val SkeletalMeshSourceDataSupport16bitOfMaterialNumber = 40

    // -----<new versions can be added above this line>-------------------------------------------------
    const val LatestVersion = SkeletalMeshSourceDataSupport16bitOfMaterialNumber

    fun get(Ar: FArchive): Int {
        val game = Ar.game
        return when {
            game < GAME_UE4(12) -> BeforeCustomVersionWasAdded
            game < GAME_UE4(13) -> GatheredTextPackageCacheFixesV1
            game < GAME_UE4(14) -> SplineComponentCurvesInStruct
            game < GAME_UE4(15) -> RefactorMeshEditorMaterials
            game < GAME_UE4(16) -> AddedInlineFontFaceAssets
            game < GAME_UE4(17) -> MaterialThumbnailRenderingChanges
            game < GAME_UE4(19) -> GatheredTextEditorOnlyPackageLocId
            game < GAME_UE4(20) -> AddedMorphTargetSectionIndices
            game < GAME_UE4(21) -> SerializeInstancedStaticMeshRenderData
            game < GAME_UE4(22) -> MeshDescriptionNewAttributeFormat
            game < GAME_UE4(23) -> MeshDescriptionRemovedHoles
            game < GAME_UE4(24) -> RemoveLandscapeHoleMaterial
            game < GAME_UE4(25) -> SkeletalMeshBuildRefactor
            game < GAME_UE4(26) -> SkeletalMeshMoveEditorSourceDataToPrivateAsset
            else -> LatestVersion
        }
    }
}