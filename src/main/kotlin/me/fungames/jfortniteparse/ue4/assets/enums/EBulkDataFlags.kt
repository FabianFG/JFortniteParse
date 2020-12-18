package me.fungames.jfortniteparse.ue4.assets.enums

enum class EBulkDataFlags(val bulkDataFlags: Int) {
    /** Empty flag set. */
    BULKDATA_None(0),
    /** If set, payload is stored at the end of the file and not inline. */
    BULKDATA_PayloadAtEndOfFile(1 shl 0),
    /** If set, payload should be [un]compressed using ZLIB during serialization. */
    BULKDATA_SerializeCompressedZLIB(1 shl 1),
    /** Force usage of SerializeElement over bulk serialization. */
    BULKDATA_ForceSingleElementSerialization(1 shl 2),
    /** Bulk data is only used once at runtime in the game. */
    BULKDATA_SingleUse(1 shl 3),
    /** Bulk data won't be used and doesn't need to be loaded. */
    BULKDATA_Unused(1 shl 5),
    /** Forces the payload to be saved inline, regardless of its size. */
    BULKDATA_ForceInlinePayload(1 shl 6),
    /** Flag to check if either compression mode is specified. */
    BULKDATA_SerializeCompressed(BULKDATA_SerializeCompressedZLIB.bulkDataFlags),
    /** Forces the payload to be always streamed, regardless of its size. */
    BULKDATA_ForceStreamPayload(1 shl 7),
    /** If set, payload is stored in a .upack file alongside the uasset. */
    BULKDATA_PayloadInSeperateFile(1 shl 8),
    /** DEPRECATED: If set, payload is compressed using platform specific bit window. */
    BULKDATA_SerializeCompressedBitWindow(1 shl 9),
    /** There is a new default to inline unless you opt out. */
    BULKDATA_Force_NOT_InlinePayload(1 shl 10),
    /** This payload is optional and may not be on device. */
    BULKDATA_OptionalPayload(1 shl 11),
    /** This payload will be memory mapped, this requires alignment, no compression etc. */
    BULKDATA_MemoryMappedPayload(1 shl 12),
    /** Bulk data size is 64 bits long. */
    BULKDATA_Size64Bit(1 shl 13),
    /** Duplicate non-optional payload in optional bulk data. */
    BULKDATA_DuplicateNonOptionalPayload(1 shl 14),
    /** Indicates that an old ID is present in the data, at some point when the DDCs are flushed we can remove this. */
    BULKDATA_BadDataVersion(1 shl 15),
    /** BulkData did not have it's offset changed during the cook and does not need the fix up at load time */
    BULKDATA_NoOffsetFixUp(1 shl 16);

    inline fun check(bulkDataFlags: Int) = (this.bulkDataFlags and bulkDataFlags) != 0
}