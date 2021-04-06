package me.fungames.jfortniteparse.ue4.assets.exports

import me.fungames.jfortniteparse.GDebugProperties
import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.assets.OnlyAnnotated
import me.fungames.jfortniteparse.ue4.assets.objects.PropertyInfo
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.objects.uobject.FName.Companion.NAME_None
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex

@OnlyAnnotated
open class UStruct : UObject() {
    /** Struct this inherits from, may be null */
    var superStruct: Lazy<UStruct>? = null
    lateinit var children: Array<FPackageIndex> // UField
    var childProperties = emptyArray<FField>()
    var childProperties2 = emptyList<PropertyInfo>()
    var propertyCount = 0

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
            logger.debug("Skipped $serializedScriptSize bytes of bytecode data")
        }
        // endregion
    }

    protected fun serializeProperties(Ar: FAssetArchive) {
        childProperties = Ar.readTArray {
            val propertyTypeName = Ar.readFName()
            val prop = FField.construct(propertyTypeName)
                ?: throw ParserException("Unsupported serialized property type $propertyTypeName", Ar)
            prop.deserialize(Ar)
            if (GDebugProperties) println("$it = $propertyTypeName ${prop.name}")
            prop
        }
    }
}

open class FField {
    var name: FName = NAME_None
    var flags: UInt = 0u

    open fun deserialize(Ar: FAssetArchive) {
        name = Ar.readFName()
        flags = Ar.readUInt32()
    }

    companion object {
        @JvmStatic
        fun construct(fieldTypeName: FName) = when (fieldTypeName.text) {
            "ArrayProperty" -> FArrayProperty()
            "BoolProperty" -> FBoolProperty()
            "ByteProperty" -> FByteProperty()
            "ClassProperty" -> FClassProperty()
            "DelegateProperty" -> FDelegateProperty()
            "EnumProperty" -> FEnumProperty()
            "FloatProperty" -> FFloatProperty()
            "Int16Property" -> FInt16Property()
            "Int64Property" -> FInt64Property()
            "Int8Property" -> FInt8Property()
            "IntProperty" -> FIntProperty()
            "InterfaceProperty" -> FInterfaceProperty()
            "MapProperty" -> FMapProperty()
            "MulticastDelegateProperty" -> FMulticastDelegateProperty()
            "MulticastInlineDelegateProperty" -> FMulticastInlineDelegateProperty()
            "NameProperty" -> FNameProperty()
            "ObjectProperty" -> FObjectProperty()
            "SetProperty" -> FSetProperty()
            "SoftClassProperty" -> FSoftClassProperty()
            "SoftObjectProperty" -> FSoftObjectProperty()
            "StrProperty" -> FStrProperty()
            "StructProperty" -> FStructProperty()
            "TextProperty" -> FTextProperty()
            "UInt16Property" -> FUInt16Property()
            "UInt32Property" -> FUInt32Property()
            "UInt64Property" -> FUInt64Property()
            else -> null
        }
    }
}

open class FPropertySerialized : FField() {
    var arrayDim: Int = 1
    var elementSize: Int = 0
    var propertyFlags: ULong = 0u
    var repIndex: UShort = 0u
    var repNotifyFunc: FName = NAME_None
    var blueprintReplicationCondition: UByte = 0u

    override fun deserialize(Ar: FAssetArchive) {
        super.deserialize(Ar)
        arrayDim = Ar.readInt32()
        elementSize = Ar.readInt32()
        propertyFlags = Ar.readUInt64()
        repIndex = Ar.readUInt16()
        repNotifyFunc = Ar.readFName()
        blueprintReplicationCondition = Ar.readUInt8()
    }
}

class FBoolProperty : FPropertySerialized() {
    var fieldSize: UByte = 0u
    var byteOffset: UByte = 0u
    var byteMask: UByte = 0u
    var fieldMask: UByte = 0u
    var boolSize: UByte = 0u
    var nativeBool: UByte = 0u

    override fun deserialize(Ar: FAssetArchive) {
        super.deserialize(Ar)
        fieldSize = Ar.readUInt8()
        byteOffset = Ar.readUInt8()
        byteMask = Ar.readUInt8()
        fieldMask = Ar.readUInt8()
        boolSize = Ar.readUInt8()
        nativeBool = Ar.readUInt8()
    }
}

class FArrayProperty : FPropertySerialized() {
    var inner: FPropertySerialized? = null

    override fun deserialize(Ar: FAssetArchive) {
        super.deserialize(Ar)
        inner = serializeSingleField(Ar) as FPropertySerialized?
    }
}

class FByteProperty : FNumericProperty() {
    var enum: Lazy<UEnum>? = null

    override fun deserialize(Ar: FAssetArchive) {
        super.deserialize(Ar)
        this.enum = Ar.readObject()
    }
}

class FClassProperty : FObjectProperty() {
    var metaClass: Lazy<UClassReal>? = null

    override fun deserialize(Ar: FAssetArchive) {
        super.deserialize(Ar)
        metaClass = Ar.readObject()
    }
}

class FDelegateProperty : FPropertySerialized() {
    var signatureFunction: Lazy<UFunction>? = null

    override fun deserialize(Ar: FAssetArchive) {
        super.deserialize(Ar)
        signatureFunction = Ar.readObject()
    }
}

class FEnumProperty : FPropertySerialized() {
    var underlyingProp: FNumericProperty? = null
    var enum: Lazy<UEnum>? = null

    override fun deserialize(Ar: FAssetArchive) {
        super.deserialize(Ar)
        enum = Ar.readObject()
        underlyingProp = serializeSingleField(Ar) as FNumericProperty?
    }
}

class FFloatProperty : FNumericProperty()

class FInt16Property : FNumericProperty()

class FInt64Property : FNumericProperty()

class FInt8Property : FNumericProperty()

class FIntProperty : FNumericProperty()

class FInterfaceProperty : FPropertySerialized() {
    var interfaceClass: Lazy<UClassReal>? = null

    override fun deserialize(Ar: FAssetArchive) {
        super.deserialize(Ar)
        interfaceClass = Ar.readObject()
    }
}

class FMapProperty : FPropertySerialized() {
    var keyProp: FPropertySerialized? = null
    var valueProp: FPropertySerialized? = null

    override fun deserialize(Ar: FAssetArchive) {
        super.deserialize(Ar)
        keyProp = serializeSingleField(Ar) as FPropertySerialized?
        valueProp = serializeSingleField(Ar) as FPropertySerialized?
    }
}

class FMulticastDelegateProperty : FPropertySerialized() {
    var signatureFunction: Lazy<UFunction>? = null

    override fun deserialize(Ar: FAssetArchive) {
        super.deserialize(Ar)
        signatureFunction = Ar.readObject()
    }
}

class FMulticastInlineDelegateProperty : FPropertySerialized() {
    var signatureFunction: Lazy<UFunction>? = null

    override fun deserialize(Ar: FAssetArchive) {
        super.deserialize(Ar)
        signatureFunction = Ar.readObject()
    }
}

class FNameProperty : FPropertySerialized()

open class FNumericProperty : FPropertySerialized()

open class FObjectProperty : FPropertySerialized() {
    var propertyClass: Lazy<UStruct>? = null

    override fun deserialize(Ar: FAssetArchive) {
        super.deserialize(Ar)
        propertyClass = Ar.readObject()
    }
}

class FSoftClassProperty : FObjectProperty() {
    var metaClass: Lazy<UClassReal>? = null

    override fun deserialize(Ar: FAssetArchive) {
        super.deserialize(Ar)
        metaClass = Ar.readObject()
    }
}

class FSoftObjectProperty : FObjectProperty()

class FSetProperty : FPropertySerialized() {
    var elementProp: FPropertySerialized? = null

    override fun deserialize(Ar: FAssetArchive) {
        super.deserialize(Ar)
        elementProp = serializeSingleField(Ar) as FPropertySerialized?
    }
}

class FStrProperty : FPropertySerialized()

class FStructProperty : FPropertySerialized() {
    var struct: Lazy<UScriptStruct>? = null

    override fun deserialize(Ar: FAssetArchive) {
        super.deserialize(Ar)
        struct = Ar.readObject()
    }
}

class FTextProperty : FPropertySerialized()

class FUInt16Property : FNumericProperty()

class FUInt32Property : FNumericProperty()

class FUInt64Property : FNumericProperty()

fun serializeSingleField(Ar: FAssetArchive): FField? {
    val propertyTypeName = Ar.readFName()
    return if (propertyTypeName != NAME_None) {
        val field = FField.construct(propertyTypeName)
            ?: throw ParserException("Unsupported serialized property type $propertyTypeName", Ar)
        field.deserialize(Ar)
        field
    } else null
}