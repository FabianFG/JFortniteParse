package me.fungames.jfortniteparse.ue4.objects.core.misc

import me.fungames.jfortniteparse.ue4.UClass

/** Enum for the components of a version string. */
enum class EVersionComponent {
    /** Major version increments introduce breaking API changes. */
    Major,
    /** Minor version increments add additional functionality without breaking existing APIs. */
    Minor,
    /** Patch version increments fix existing functionality without changing the API. */
    Patch,
    /** The pre-release field adds additional versioning through a series of comparable dotted strings or numbers. */
    Changelist,
    Branch
}

/** Components of a version string. */
enum class EVersionComparison { Neither, First, Second }

/** Base class for the EngineVersion class. Holds basic version numbers. */
@ExperimentalUnsignedTypes
open class FEngineVersionBase : UClass {
    /** Major version number. */
    var major: UShort

    /** Minor version number. */
    var minor: UShort

    /** Patch version number. */
    var patch: UShort

    /** Changelist number. This is used to arbitrate when Major/Minor/Patch version numbers match. */
    var changelist: UInt
        get() = field and 0x7fffffffu // Mask to ignore licensee bit

    constructor() : this(0u, 0u, 0u, 0u)

    constructor(major: UShort, minor: UShort, patch: UShort, changelist: UInt) {
        this.major = major
        this.minor = minor
        this.patch = patch
        this.changelist = changelist
    }

    /** Checks if the changelist number represents licensee changelist number. */
    fun isLicenseeVersion() = (changelist and 0x80000000u) != 0u // Check for licensee bit

    /** Returns whether the current version is empty. */
    fun isEmpty() = major == 0u.toUShort() && minor == 0u.toUShort() && patch == 0u.toUShort()

    /** Returns whether the engine version has a changelist component. */
    fun hasChangelist() = changelist != 0u

    /** Encodes a licensee changelist number (by setting the top bit) */
    fun encodeLicenseeChangelist(changelist: UInt) = changelist or 0x80000000u
}