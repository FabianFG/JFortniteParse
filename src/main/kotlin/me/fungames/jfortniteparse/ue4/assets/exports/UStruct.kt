package me.fungames.jfortniteparse.ue4.assets.exports

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.assets.OnlyAnnotated
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.objects.uobject.FName.Companion.NAME_None
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex

@OnlyAnnotated
open class UStruct : UObject() {
    /** Struct this inherits from, may be null */
    var superStruct: Lazy<UStruct>? = null
    lateinit var children: Array<FPackageIndex> // UField
    lateinit var childProperties: Array<FField>

    override fun deserialize(Ar: FAssetArchive, validPos: Int) {
        super.deserialize(Ar, validPos)
        superStruct = Ar.readObject()
        children = Ar.readTArray { FPackageIndex(Ar) }
        serializeProperties(Ar)
        // region FStructScriptLoader::FStructScriptLoader
        val bytecodeBufferSize = Ar.readInt32()
        val serializedScriptSize = Ar.readInt32()
        Ar.skip(serializedScriptSize.toLong())
        if (serializedScriptSize > 0) {
            logger.info("Skipped $serializedScriptSize bytes of bytecode data")
        }
        // endregion
    }

    protected fun serializeProperties(Ar: FAssetArchive) {
        childProperties = Ar.readTArray {
            val propertyTypeName = Ar.readFName()
            val prop = FField.construct(Ar, propertyTypeName)
            //println("$propertyTypeName ${prop.name}")
            prop
        }
    }
}

open class FField(Ar: FAssetArchive) {
    val name = Ar.readFName()
    val flags = Ar.readUInt32()

    companion object {
        @JvmStatic
        fun construct(Ar: FAssetArchive, fieldTypeName: FName) = when (fieldTypeName.text) {
            "ArrayProperty" -> FArrayProperty(Ar)
            "BoolProperty" -> FBoolProperty(Ar)
            "ByteProperty" -> FByteProperty(Ar)
            "ClassProperty" -> FClassProperty(Ar)
            "DelegateProperty" -> FDelegateProperty(Ar)
            "FloatProperty" -> FFloatProperty(Ar)
            "IntProperty" -> FIntProperty(Ar)
            "MapProperty" -> FMapProperty(Ar)
            "MulticastDelegateProperty" -> FMulticastDelegateProperty(Ar)
            "MulticastInlineDelegateProperty" -> FMulticastInlineDelegateProperty(Ar)
            "NameProperty" -> FNameProperty(Ar)
            "ObjectProperty" -> FObjectProperty(Ar)
            "StrProperty" -> FStrProperty(Ar)
            "StructProperty" -> FStructProperty(Ar)
            else -> throw ParserException("Unsupported serialized property type $fieldTypeName")
        }
    }
}

open class FPropertySerialized(Ar: FAssetArchive) : FField(Ar) {
    val arrayDim = Ar.readInt32()
    val elementSize = Ar.readInt32()
    val saveFlags = Ar.readUInt64()
    val repIndex = Ar.readUInt16()
    val repNotifyFunc = Ar.readFName()
    val blueprintReplicationCondition = Ar.readUInt8()
}

class FBoolProperty(Ar: FAssetArchive) : FPropertySerialized(Ar) {
    val fieldSize = Ar.readUInt8()
    val byteOffset = Ar.readUInt8()
    val byteMask = Ar.readUInt8()
    val fieldMask = Ar.readUInt8()
    val boolSize = Ar.readUInt8()
    val nativeBool = Ar.readUInt8()
}

class FArrayProperty(Ar: FAssetArchive) : FPropertySerialized(Ar) {
    val inner = serializeSingleField(Ar) as FPropertySerialized?
}

class FByteProperty(Ar: FAssetArchive) : FPropertySerialized(Ar) {
    val enum = FPackageIndex(Ar)
}

class FClassProperty(Ar: FAssetArchive) : FPropertySerialized(Ar) {
    val metaClass = Ar.readObject<UClassReal>()
}

class FDelegateProperty(Ar: FAssetArchive) : FPropertySerialized(Ar) {
    val signatureFunction = Ar.readObject<UFunction>()
}

class FFloatProperty(Ar: FAssetArchive) : FPropertySerialized(Ar)

class FIntProperty(Ar: FAssetArchive) : FPropertySerialized(Ar)

class FMapProperty(Ar: FAssetArchive) : FPropertySerialized(Ar) {
    val keyProp = serializeSingleField(Ar) as FPropertySerialized?
    val valueProp = serializeSingleField(Ar) as FPropertySerialized?
}

class FMulticastDelegateProperty(Ar: FAssetArchive) : FPropertySerialized(Ar) {
    val signatureFunction = Ar.readObject<UFunction>()
}

class FMulticastInlineDelegateProperty(Ar: FAssetArchive) : FPropertySerialized(Ar) {
    val signatureFunction = Ar.readObject<UFunction>()
}

class FNameProperty(Ar: FAssetArchive) : FPropertySerialized(Ar)

class FObjectProperty(Ar: FAssetArchive) : FPropertySerialized(Ar) {
    val propertyClass = Ar.readObject<UClassReal>()
}

class FStrProperty(Ar: FAssetArchive) : FPropertySerialized(Ar)

class FStructProperty(Ar: FAssetArchive) : FPropertySerialized(Ar) {
    val struct = Ar.readObject<UScriptStruct>()
}

fun serializeSingleField(Ar: FAssetArchive): FField? {
    val propertyTypeName = Ar.readFName()
    return if (propertyTypeName != NAME_None) FField.construct(Ar, propertyTypeName) else null
}