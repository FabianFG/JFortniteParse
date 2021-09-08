package me.fungames.jfortniteparse.ue4.objects.core.i18n

import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid

object FTextLocalizationResourceVersion {
    @JvmField
    val LOC_META_MAGIC = FGuid(0xA14CEE4Fu, 0x83554868u, 0xBD464C6Cu, 0x7C50DA70u)
    @JvmField
    val LOC_RES_MAGIC = FGuid(0x7574140Eu, 0xFC034A67u, 0x9D90154Au, 0x1B7F37C3u)

    /**
     * Data versions for LocMeta files.
     */
    enum class ELocMetaVersion {
        /** Initial format. */
        Initial,
        /** Added complete list of cultures compiled for the localization target. */
        AddedCompiledCultures,
    }

    /**
     * Data versions for LocRes files.
     */
    enum class ELocResVersion {
        /** Legacy format file - will be missing the magic number. */
        Legacy,
        /** Compact format file - strings are stored in a LUT to avoid duplication. */
        Compact,
        /** Optimized format file - namespaces/keys are pre-hashed (CRC32), we know the number of elements up-front, and the number of references for each string in the LUT (to allow stealing). */
        Optimized_CRC32,
        /** Optimized format file - namespaces/keys are pre-hashed (CityHash64, UTF-16), we know the number of elements up-front, and the number of references for each string in the LUT (to allow stealing). */
        Optimized_CityHash64_UTF16,
    }
}