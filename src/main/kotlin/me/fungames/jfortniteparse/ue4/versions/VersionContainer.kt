package me.fungames.jfortniteparse.ue4.versions

import me.fungames.jfortniteparse.ue4.objects.core.serialization.FCustomVersion

/**
 * Contains versioning configuration used for serializing assets.
 * Here you can specify the UE game, archive version, custom versions, and option overrides for cooked data.
 */
class VersionContainer {
    companion object {
        @JvmStatic
        val DEFAULT = VersionContainer()
    }

    var game: Int
        get() = _game
        set(value) {
            _game = value
            initOptions()
        }
    private var _game = -1

    var ver: Int
        get() = _ver
        set(value) {
            explicitVer = value != VER_UE4_DETERMINE_BY_GAME
            _ver = if (explicitVer) value else getArVer(game)
            initOptions()
        }
    private var _ver = -1

    var explicitVer = false
        private set
    var customVersions: List<FCustomVersion>?
    val options = hashMapOf<String, Boolean>()
    private val optionOverrides: Map<String, Boolean>?

    constructor(game: Int = GAME_UE4(LATEST_SUPPORTED_UE4_VERSION),
                ver: Int = VER_UE4_DETERMINE_BY_GAME,
                customVersions: List<FCustomVersion>? = null,
                optionOverrides: Map<String, Boolean>? = null) {
        this.optionOverrides = optionOverrides
        this.game = game
        this.ver = ver
        this.customVersions = customVersions
    }

    constructor(other: VersionContainer) : this(other.game, other.ver, other.customVersions, other.optionOverrides) {
        explicitVer = other.explicitVer
    }

    private fun initOptions() {
        options.clear()
        options["RawIndexBuffer.HasShouldExpandTo32Bit"] = game >= GAME_UE4(25)
        options["ShaderMap.UseNewCookedFormat"] = game >= GAME_UE5(0)
        options["SkeletalMesh.KeepMobileMinLODSettingOnDesktop"] = game >= GAME_UE4(27)
        options["SkeletalMesh.UseNewCookedFormat"] = game >= GAME_UE4(24)
        options["StaticMesh.HasLODsShareStaticLighting"] = game < GAME_UE4(15) || game >= GAME_UE4(16) // Exists in all engine versions except UE4.15
        options["StaticMesh.HasRayTracingGeometry"] = game >= GAME_UE4(25)
        options["StaticMesh.HasVisibleInRayTracing"] = game >= GAME_UE4(26)
        options["StaticMesh.KeepMobileMinLODSettingOnDesktop"] = game >= GAME_UE4(27)
        options["StaticMesh.UseNewCookedFormat"] = game >= GAME_UE4(23)
        options["VirtualTextures"] = game >= GAME_UE4(23)

        if (optionOverrides != null) {
            for ((key, value) in optionOverrides) {
                options[key] = value
            }
        }
    }

    inline operator fun get(optionKey: String) = options.getOrDefault(optionKey, false)
}