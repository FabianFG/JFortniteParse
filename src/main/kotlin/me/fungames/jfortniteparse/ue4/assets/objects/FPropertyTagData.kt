package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.FGuid
import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.util.FName
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter
import me.fungames.jfortniteparse.ue4.reader.FArchive

@ExperimentalUnsignedTypes
sealed class FPropertyTagData : UClass() {

    abstract fun serialize(Ar: FAssetArchiveWriter)

    class StructProperty(Ar: FAssetArchive) : FPropertyTagData() {
        var nameData: FName
        var guid: FGuid

        init {
            super.init(Ar)
            nameData = Ar.readFName()
            guid = FGuid(Ar)
            super.complete(Ar)
        }

        override fun serialize(Ar: FAssetArchiveWriter) {
            super.initWrite(Ar)
            Ar.writeFName(nameData)
            guid.serialize(Ar)
            super.completeWrite(Ar)
        }
    }

    class BoolProperty(Ar: FArchive) : FPropertyTagData() {
        var bool: Boolean

        init {
            super.init(Ar)
            bool = Ar.readFlag()
            super.complete(Ar)
        }

        override fun serialize(Ar: FAssetArchiveWriter) {
            super.initWrite(Ar)
            Ar.writeFlag(bool)
            super.completeWrite(Ar)
        }
    }

    class EnumProperty(Ar: FAssetArchive) : FPropertyTagData() {
        var enum: FName

        init {
            super.init(Ar)
            enum = Ar.readFName()
            super.complete(Ar)
        }

        override fun serialize(Ar: FAssetArchiveWriter) {
            super.initWrite(Ar)
            Ar.writeFName(enum)
            super.completeWrite(Ar)
        }
    }

    class ByteProperty(Ar: FAssetArchive) : FPropertyTagData() {
        var byte: FName

        init {
            super.init(Ar)
            byte = Ar.readFName()
            super.complete(Ar)
        }

        override fun serialize(Ar: FAssetArchiveWriter) {
            super.initWrite(Ar)
            Ar.writeFName(byte)
            super.completeWrite(Ar)
        }
    }

    class ArrayProperty(Ar: FAssetArchive) : FPropertyTagData() {
        var property: FName

        init {
            super.init(Ar)
            property = Ar.readFName()
            super.complete(Ar)
        }

        override fun serialize(Ar: FAssetArchiveWriter) {
            super.initWrite(Ar)
            Ar.writeFName(property)
            super.completeWrite(Ar)
        }
    }

    class MapProperty(Ar: FAssetArchive) : FPropertyTagData() {
        var key: FName
        var value: FName

        init {
            super.init(Ar)
            key = Ar.readFName()
            value = Ar.readFName()
            super.complete(Ar)
        }

        override fun serialize(Ar: FAssetArchiveWriter) {
            super.initWrite(Ar)
            Ar.writeFName(key)
            Ar.writeFName(value)
            super.completeWrite(Ar)
        }
    }

    class SetProperty(Ar: FAssetArchive) : FPropertyTagData() {
        var property: FName

        init {
            super.init(Ar)
            property = Ar.readFName()
            super.complete(Ar)
        }

        override fun serialize(Ar: FAssetArchiveWriter) {
            super.initWrite(Ar)
            Ar.writeFName(property)
            super.completeWrite(Ar)
        }
    }
}