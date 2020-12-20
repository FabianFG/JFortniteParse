package me.fungames.jfortniteparse.ue4.objects.uobject.serialization

import me.fungames.jfortniteparse.GDebugUnversionedPropertySerialization
import me.fungames.jfortniteparse.GExportArchiveCheckDummyName
import me.fungames.jfortniteparse.exceptions.MissingSchemaException
import me.fungames.jfortniteparse.exceptions.UnknownPropertyException
import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.OnlyAnnotated
import me.fungames.jfortniteparse.ue4.assets.UProperty
import me.fungames.jfortniteparse.ue4.assets.exports.FPropertySerialized
import me.fungames.jfortniteparse.ue4.assets.exports.UScriptStruct
import me.fungames.jfortniteparse.ue4.assets.exports.UStruct
import me.fungames.jfortniteparse.ue4.assets.objects.FProperty
import me.fungames.jfortniteparse.ue4.assets.objects.FProperty.ReadType
import me.fungames.jfortniteparse.ue4.assets.objects.FPropertyTag
import me.fungames.jfortniteparse.ue4.assets.objects.PropertyInfo
import me.fungames.jfortniteparse.ue4.assets.objects.PropertyType
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.reader.FExportArchive
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.util.INDEX_NONE
import me.fungames.jfortniteparse.util.divideAndRoundUp
import me.fungames.jfortniteparse.util.indexOfFirst
import java.lang.reflect.Modifier
import java.util.*

class FUnversionedPropertySerializer(val info: PropertyInfo, val arrayIndex: Int) {
    fun deserialize(Ar: FAssetArchive, type: ReadType): FPropertyTag {
        if (GExportArchiveCheckDummyName && Ar is FExportArchive) {
            Ar.checkDummyName(info.name)
            val typeInfo = info.type
            setOf(
                typeInfo.type,
                typeInfo.structType,
                typeInfo.enumName,
                typeInfo.innerType?.type,
                typeInfo.valueType?.type
            ).forEach { if (it != null) Ar.checkDummyName(it.text) }
        }
        val tag = FPropertyTag(FName.dummy(info.name))
        /*if (true) {
            tag.name = FName.dummy(data.name!!)
            tag.type = data.type.type
            tag.structName = data.type.structType
            tag.boolVal = data.type.bool
            tag.enumName = data.type.enumName
            tag.enumType = data.type.enumType
            tag.innerType = data.type.innerType?.type ?: FName.NAME_None
            tag.valueType = data.type.valueType?.type ?: FName.NAME_None
        }*/
        tag.arrayIndex = arrayIndex
        tag.prop = FProperty.readPropertyValue(Ar, info.type, type)
        return tag
    }

    override fun toString() = (info.field?.type?.simpleName ?: info.type.toString()) + ' ' + info.name
}

/**
 * Serialization is based on indices into this property array
 */
class FUnversionedStructSchema {
    val serializers = mutableMapOf<Int, FUnversionedPropertySerializer>()

    constructor(struct: UStruct) {
        var index = 0
        var struct: UStruct? = struct
        while (struct != null) {
            if (struct.javaClass == UScriptStruct::class.java) {
                val clazz = (struct as UScriptStruct).structClass
                    ?: throw MissingSchemaException("Missing schema for $struct")
                val bOnlyAnnotated = clazz.isAnnotationPresent(OnlyAnnotated::class.java)
                for (field in clazz.declaredFields) {
                    if (Modifier.isStatic(field.modifiers)) {
                        continue
                    }
                    val ann = field.getAnnotation(UProperty::class.java)
                    if (bOnlyAnnotated && ann == null) {
                        continue
                    }
                    index += ann?.skipPrevious ?: 0
                    val propertyInfo = PropertyInfo(field, ann)
                    for (arrayIdx in 0 until propertyInfo.arrayDim) {
                        if (GDebugUnversionedPropertySerialization) println("$index = ${propertyInfo.name}")
                        serializers[index] = FUnversionedPropertySerializer(propertyInfo, arrayIdx)
                        ++index
                    }
                    index += ann?.skipNext ?: 0
                }
            } else {
                for (prop in struct.childProperties) {
                    val propertyInfo = PropertyInfo(prop.name.text, PropertyType(prop as FPropertySerialized), prop.arrayDim)
                    for (arrayIdx in 0 until prop.arrayDim) {
                        if (GDebugUnversionedPropertySerialization) println("$index = ${prop.name} [SERIALIZED]")
                        serializers[index] = FUnversionedPropertySerializer(propertyInfo, arrayIdx)
                        ++index
                    }
                }
            }
            struct = struct.superStruct?.value
        }
    }
}

val schemaCache = mutableMapOf<Class<*>, FUnversionedStructSchema>()

fun getOrCreateUnversionedSchema(struct: UStruct): FUnversionedStructSchema {
    return if (struct is UScriptStruct && struct.structClass != null) {
        schemaCache.getOrPut(struct.structClass!!) { FUnversionedStructSchema(struct) }
    } else {
        FUnversionedStructSchema(struct)
    }
}

/**
 * List of serialized property indices and which of them are non-zero.
 *
 * Serialized as a stream of 16-bit skip-x keep-y fragments and a zero bitmask.
 */
class FUnversionedHeader {
    protected val fragments = mutableListOf<FFragment>()
    var bHasNonZeroValues = false
        protected set
    protected lateinit var zeroMask: BitSet

    fun load(Ar: FArchive) {
        var fragment: FFragment
        var zeroMaskNum = 0u
        var unmaskedNum = 0u
        do {
            val packed = Ar.readUInt16()
            fragment = FFragment(packed)

            fragments.add(fragment)

            if (fragment.bHasAnyZeroes) {
                zeroMaskNum += fragment.valueNum
            } else {
                unmaskedNum += fragment.valueNum
            }
        } while (!fragment.bIsLast)

        if (zeroMaskNum > 0u) {
            zeroMask = loadZeroMaskData(Ar, zeroMaskNum)
            bHasNonZeroValues = unmaskedNum > 0u || zeroMask.indexOfFirst(false) != INDEX_NONE
        } else {
            zeroMask = BitSet(0)
            bHasNonZeroValues = unmaskedNum > 0u
        }
    }

    fun hasValues() = bHasNonZeroValues || (zeroMask.size() > 0)

    protected fun loadZeroMaskData(Ar: FArchive, numBits: UInt) =
        BitSet.valueOf(Ar.read(when {
            numBits <= 8u -> 1u
            numBits <= 16u -> 2u
            else -> numBits.divideAndRoundUp(32u) * 4u
        }.toInt()))

    protected class FFragment {
        companion object {
            val SKIP_MAX = 127u
            val VALUE_MAX = 127u

            val SKIP_NUM_MASK: UShort = 0x007fu
            val HAS_ZERO_MASK: UShort = 0x0080u
            val VALUE_NUM_SHIFT = 9
            val IS_LAST_MASK: UShort = 0x0100u
        }

        /** Number of properties to skip before values */
        var skipNum: UByte = 0u
        var bHasAnyZeroes = false
        /** Number of subsequent property values stored */
        var valueNum: UByte = 0u
        /** Is this the last fragment of the header? */
        var bIsLast = false

        constructor(int: UShort) {
            skipNum = (int and SKIP_NUM_MASK).toUByte()
            bHasAnyZeroes = (int and HAS_ZERO_MASK) != 0u.toUShort()
            valueNum = (int.toUInt() shr VALUE_NUM_SHIFT).toUByte()
            bIsLast = (int and IS_LAST_MASK) != 0u.toUShort()
        }
    }

    class FIterator(header: FUnversionedHeader, private val schemas: Map<Int, FUnversionedPropertySerializer>) {
        /*private*/ internal var schemaIt = 0
        private val zeroMask = header.zeroMask
        private val fragments = header.fragments
        private var fragmentIt = 0
        internal var bDone = !header.hasValues()
        private var zeroMaskIndex = 0u
        private var remainingFragmentValues = 0u

        init {
            if (!bDone) {
                skip()
            }
        }

        fun next() {
            ++schemaIt
            --remainingFragmentValues
            if (fragments[fragmentIt].bHasAnyZeroes) {
                ++zeroMaskIndex
            }

            if (remainingFragmentValues == 0u) {
                if (fragments[fragmentIt].bIsLast) {
                    bDone = true
                } else {
                    ++fragmentIt
                    skip()
                }
            }
        }

        val serializer get() = schemas[schemaIt]

        fun isNonZero() = !fragments[fragmentIt].bHasAnyZeroes || !zeroMask[zeroMaskIndex.toInt()]

        private fun skip() {
            schemaIt += fragments[fragmentIt].skipNum.toInt()

            while (fragments[fragmentIt].valueNum == 0u.toUByte()) {
                check(!fragments[fragmentIt].bIsLast)
                ++fragmentIt
                schemaIt += fragments[fragmentIt].skipNum.toInt()
            }

            remainingFragmentValues = fragments[fragmentIt].valueNum.toUInt()
        }
    }
}

fun deserializeUnversionedProperties(properties: MutableList<FPropertyTag>, struct: UStruct, Ar: FAssetArchive) {
    //check(canUseUnversionedPropertySerialization())

    if (GDebugUnversionedPropertySerialization) println("Load: ${struct.name}")
    val header = FUnversionedHeader()
    header.load(Ar)

    if (header.hasValues()) {
        val schemas = getOrCreateUnversionedSchema(struct).serializers

        if (header.bHasNonZeroValues) {
            //val defaults = FDefaultStruct(defaultsData, defaultsStruct)

            val it = FUnversionedHeader.FIterator(header, schemas)
            while (!it.bDone) {
                val serializer = it.serializer
                if (serializer != null) {
                    if (GDebugUnversionedPropertySerialization) println("Val: ${it.schemaIt} (IsNonZero: ${it.isNonZero()})")
                    if (it.isNonZero()) {
                        val element = serializer.deserialize(Ar, ReadType.NORMAL)
                        properties.add(element)
                        if (GDebugUnversionedPropertySerialization) println(element.toString())
                    } else {
                        properties.add(serializer.deserialize(Ar, ReadType.ZERO))
                    }
                } else {
                    if (it.isNonZero()) {
                        throw UnknownPropertyException("Unknown property for ${struct.name} with index ${it.schemaIt}, cannot proceed with serialization")
                    }
                    UClass.logger.warn("Unknown property for ${struct.name} with index ${it.schemaIt}, but it's zero so we're good")
                }
                it.next()
            }
        } else {
            val it = FUnversionedHeader.FIterator(header, schemas)
            while (!it.bDone) {
                check(!it.isNonZero())
                it.serializer?.run { properties.add(deserialize(Ar, ReadType.ZERO)) }
                    ?: UClass.logger.warn("Unknown property for ${struct.name} with index ${it.schemaIt}, but it's zero so we're good")
                it.next()
            }
        }
    }
}