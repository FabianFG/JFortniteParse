package me.fungames.jfortniteparse.ue4.io.al2

import me.fungames.jfortniteparse.ue4.reader.FArchive

class FPackageObjectIndex {
    companion object {
        val INDEX_BITS = 62uL
        val INDEX_MASK = (1uL shl INDEX_BITS.toInt()) - 1uL
        val TYPE_MASK = INDEX_MASK.inv()
        val TYPE_SHIFT = INDEX_BITS
        val INVALID = 0uL.inv()

        fun generateImportHashFromObjectPath(objectPath: String): ULong {
            return 0uL
        }

        fun fromExportIndex(index: Int) =
            FPackageObjectIndex(EType.Export, index.toULong())

        fun fromScriptPath(scriptObjectPath: String) =
            FPackageObjectIndex(EType.ScriptImport, generateImportHashFromObjectPath(scriptObjectPath))

        fun fromPackagePath(packageObjectPath: String) =
            FPackageObjectIndex(EType.PackageImport, generateImportHashFromObjectPath(packageObjectPath))
    }

    private var typeAndId = INVALID

    enum class EType {
        Export,
        ScriptImport,
        PackageImport,
        Null
    }

    constructor()

    constructor(type: EType, id: ULong) {
        typeAndId = (type.ordinal.toULong() shl TYPE_SHIFT.toInt()) or id
    }

    constructor(Ar: FArchive) {
        typeAndId = Ar.readUInt64()
    }


    fun isNull() = typeAndId == INVALID

    fun isExport() = (typeAndId shr TYPE_SHIFT.toInt()) == EType.Export.ordinal.toULong()

    fun isImport() = isScriptImport() || isPackageImport()

    fun isScriptImport() = (typeAndId shr TYPE_SHIFT.toInt()) == EType.ScriptImport.ordinal.toULong()

    fun isPackageImport() = (typeAndId shr TYPE_SHIFT.toInt()) == EType.PackageImport.ordinal.toULong()

    fun toExport(): UInt {
        check(isExport())
        return typeAndId.toUInt()
    }

    fun value() = typeAndId and INDEX_MASK

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FPackageObjectIndex

        if (typeAndId != other.typeAndId) return false

        return true
    }

    override fun hashCode() = typeAndId.hashCode()
}