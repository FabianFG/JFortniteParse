package me.fungames.jfortniteparse.ue4.versions

import me.fungames.jfortniteparse.ue4.reader.FArchive

// Custom serialization version for changes made in Dev-Rendering stream
object FRenderingObjectVersion {
    // Before any version changes were made
    const val BeforeCustomVersionWasAdded = 0

    // Added support for 3 band SH in the ILC
    const val IndirectLightingCache3BandSupport = 1

    // Allows specifying resolution for reflection capture probes
    const val CustomReflectionCaptureResolutionSupport = 2

    const val RemovedTextureStreamingLevelData = 3

    // translucency is now a property which matters for materials with the decal domain
    const val IntroducedMeshDecals = 4

    // Reflection captures are no longer prenormalized
    const val ReflectionCapturesStoreAverageBrightness = 5

    const val ChangedPlanarReflectionFadeDefaults = 6

    const val RemovedRenderTargetSize = 7

    // Particle Cutout (SubUVAnimation) data is now stored in the ParticleRequired Module
    const val MovedParticleCutoutsToRequiredModule = 8

    const val MapBuildDataSeparatePackage = 9

    // StaticMesh and SkeletalMesh texcoord size data.
    const val TextureStreamingMeshUVChannelData = 10

    // Added type handling to material normalize and length (sqrt) nodes
    const val TypeHandlingForMaterialSqrtNodes = 11

    const val FixedBSPLightmaps = 12

    const val DistanceFieldSelfShadowBias = 13

    const val FixedLegacyMaterialAttributeNodeTypes = 14

    const val ShaderResourceCodeSharing = 15

    const val MotionBlurAndTAASupportInSceneCapture2d = 16

    const val AddedTextureRenderTargetFormats = 17

    // Triggers a rebuild of the mesh UV density while also adding an update in the postedit
    const val FixedMeshUVDensity = 18

    const val AddedbUseShowOnlyList = 19

    const val VolumetricLightmaps = 20

    const val MaterialAttributeLayerParameters = 21

    const val StoreReflectionCaptureBrightnessForCooking = 22

    // FModelVertexBuffer does serialize a regular TArray instead of a TResourceArray
    const val ModelVertexBufferSerialization = 23

    const val ReplaceLightAsIfStatic = 24

    // Added per FShaderType permutation id.
    const val ShaderPermutationId = 25

    // Changed normal precision in imported data
    const val IncreaseNormalPrecision = 26

    const val VirtualTexturedLightmaps = 27

    const val GeometryCacheFastDecoder = 28

    const val LightmapHasShadowmapData = 29

    // Removed old gaussian and bokeh DOF methods from deferred shading renderer.
    const val DiaphragmDOFOnlyForDeferredShadingRenderer = 30

    // Lightmaps replace ULightMapVirtualTexture (non-UTexture derived class) with ULightMapVirtualTexture2D (derived from UTexture)
    const val VirtualTexturedLightmapsV2 = 31

    const val SkyAtmosphereStaticLightingVersioning = 32

    // UTextureRenderTarget2D now explicitly allows users to create sRGB or non-sRGB type targets
    const val ExplicitSRGBSetting = 33

    const val VolumetricLightmapStreaming = 34

    //ShaderModel4 support removed from engine
    const val RemovedSM4 = 35

    // Deterministic ShaderMapID serialization
    const val MaterialShaderMapIdSerialization = 36

    // Add force opaque flag for static mesh
    const val StaticMeshSectionForceOpaqueField = 37

    // Add force opaque flag for static mesh
    const val AutoExposureChanges = 38

    // Removed emulated instancing from instanced static meshes
    const val RemovedEmulatedInstancing = 39

    // Added per instance custom data (for Instanced Static Meshes)
    const val PerInstanceCustomData = 40

    // Added material attributes to shader graph to support anisotropic materials
    const val AnisotropicMaterial = 41

    // Add if anything has changed in the exposure, override the bias to avoid the new default propagating
    const val AutoExposureForceOverrideBiasFlag = 42

    // Override for a special case for objects that were serialized and deserialized between versions AutoExposureChanges and AutoExposureForceOverrideBiasFlag
    const val AutoExposureDefaultFix = 43

    // Remap Volume Extinction material input to RGB
    const val VolumeExtinctionBecomesRGB = 44

    // Add a new virtual texture to support virtual texture light map on mobile
    const val VirtualTexturedLightmapsV3 = 45

    // -----<new versions can be added above this line>-------------------------------------------------
    const val LatestVersion = StaticMeshSectionForceOpaqueField

    fun get(Ar: FArchive): Int {
        val game = Ar.game
        return when {
            game < GAME_UE4(12) -> BeforeCustomVersionWasAdded
            game < GAME_UE4(13) -> CustomReflectionCaptureResolutionSupport
            game < GAME_UE4(14) -> IntroducedMeshDecals
            game < GAME_UE4(16) -> FixedBSPLightmaps // 4.14 and 4.15
            game < GAME_UE4(17) -> ShaderResourceCodeSharing
            game < GAME_UE4(18) -> AddedbUseShowOnlyList
            game < GAME_UE4(19) -> VolumetricLightmaps
            game < GAME_UE4(20) -> ShaderPermutationId
            game < GAME_UE4(21) -> IncreaseNormalPrecision
            game < GAME_UE4(22) -> VirtualTexturedLightmaps
            game < GAME_UE4(23) -> GeometryCacheFastDecoder
            game < GAME_UE4(24) -> VirtualTexturedLightmapsV2
            game < GAME_UE4(25) -> MaterialShaderMapIdSerialization
            game < GAME_UE4(26) -> AutoExposureDefaultFix
            else -> LatestVersion
        }
    }
}