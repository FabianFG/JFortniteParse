package me.fungames.jfortniteparse.ue4.objects.uobject.serialization

import androidx.collection.SparseArrayCompat
import androidx.collection.set
import me.fungames.jfortniteparse.GDebugProperties
import me.fungames.jfortniteparse.LOG_JFP
import me.fungames.jfortniteparse.exceptions.MissingSchemaException
import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.exceptions.UnknownPropertyException
import me.fungames.jfortniteparse.ue4.assets.OnlyAnnotated
import me.fungames.jfortniteparse.ue4.assets.UProperty
import me.fungames.jfortniteparse.ue4.assets.exports.FPropertySerialized
import me.fungames.jfortniteparse.ue4.assets.exports.UScriptStruct
import me.fungames.jfortniteparse.ue4.assets.exports.UStruct
import me.fungames.jfortniteparse.ue4.assets.objects.FProperty.ReadType
import me.fungames.jfortniteparse.ue4.assets.objects.FPropertyTag
import me.fungames.jfortniteparse.ue4.assets.objects.PropertyInfo
import me.fungames.jfortniteparse.ue4.assets.objects.PropertyType
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.util.INDEX_NONE
import me.fungames.jfortniteparse.util.divideAndRoundUp
import me.fungames.jfortniteparse.util.indexOfFirst
import java.lang.reflect.Modifier
import java.util.*
import kotlin.collections.LinkedHashMap

class FUnversionedPropertySerializer(val info: PropertyInfo, val arrayIndex: Int) {
    fun deserialize(Ar: FAssetArchive, type: ReadType) = FPropertyTag(Ar, info, arrayIndex, type)
    override fun toString() = "${info.type} ${info.name}"
}

/**
 * Serialization is based on indices into this property array
 */
class FUnversionedStructSchema {
    val serializers = SparseArrayCompat<FUnversionedPropertySerializer>()

    constructor(struct: UStruct) {
        var index = 0
        var struct: UStruct? = struct
        while (struct != null) {
            if (struct is UScriptStruct && struct.useClassProperties) {
                val clazz = struct.structClass
                    ?: throw MissingSchemaException("Missing schema for $struct")
                val onlyAnnotated = clazz.isAnnotationPresent(OnlyAnnotated::class.java)
                for (field in clazz.declaredFields) { // Reflection
                    if (Modifier.isStatic(field.modifiers)) {
                        continue
                    }
                    val ann = field.getAnnotation(UProperty::class.java)
                    if (onlyAnnotated && ann == null) {
                        continue
                    }
                    index += ann?.skipPrevious ?: 0
                    val propertyInfo = PropertyInfo(field, ann)
                    for (arrayIdx in 0 until propertyInfo.arrayDim) {
                        if (GDebugProperties) println("$index = ${propertyInfo.name}")
                        serializers[index++] = FUnversionedPropertySerializer(propertyInfo, arrayIdx)
                    }
                    index += ann?.skipNext ?: 0
                }
            } else if (struct.childProperties.isNotEmpty()) {
                for (prop in struct.childProperties) { // Serialized in packages
                    val propertyInfo = PropertyInfo(prop.name.text, PropertyType(prop as FPropertySerialized), prop.arrayDim)
                    for (arrayIdx in 0 until prop.arrayDim) {
                        if (GDebugProperties) println("$index = ${prop.name} [SERIALIZED]")
                        serializers[index++] = FUnversionedPropertySerializer(propertyInfo, arrayIdx)
                    }
                }
            } else if (struct.childProperties2.isNotEmpty()) {
                val startIndex = index
                for (prop in struct.childProperties2) { // Provided by TypeMappingsProvider
                    index = startIndex + prop.index
                    for (arrayIdx in 0 until prop.arrayDim) {
                        if (GDebugProperties) println("$index = ${prop.name}")
                        serializers[index++] = FUnversionedPropertySerializer(prop, arrayIdx)
                    }
                }
                index = startIndex + struct.propertyCount
            }
            struct = struct.superStruct?.value
        }
    }
}

val schemaCache = hashMapOf<Class<*>, FUnversionedStructSchema>()

fun getOrCreateUnversionedSchema(struct: UStruct): FUnversionedStructSchema {
    return if (struct is UScriptStruct && struct.useClassProperties && struct.structClass != null) {
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
    var hasNonZeroValues = false
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

            if (fragment.hasAnyZeroes) {
                zeroMaskNum += fragment.valueNum
            } else {
                unmaskedNum += fragment.valueNum
            }
        } while (!fragment.isLast)

        if (zeroMaskNum > 0u) {
            zeroMask = loadZeroMaskData(Ar, zeroMaskNum)
            hasNonZeroValues = unmaskedNum > 0u || zeroMask.indexOfFirst(false) != INDEX_NONE
        } else {
            zeroMask = BitSet(0)
            hasNonZeroValues = unmaskedNum > 0u
        }
    }

    fun hasValues() = hasNonZeroValues || (zeroMask.size() > 0)

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
        var hasAnyZeroes = false
        /** Number of subsequent property values stored */
        var valueNum: UByte = 0u
        /** Is this the last fragment of the header? */
        var isLast = false

        constructor(int: UShort) {
            skipNum = (int and SKIP_NUM_MASK).toUByte()
            hasAnyZeroes = (int and HAS_ZERO_MASK) != 0u.toUShort()
            valueNum = (int.toUInt() shr VALUE_NUM_SHIFT).toUByte()
            isLast = (int and IS_LAST_MASK) != 0u.toUShort()
        }
    }

    class FIterator(header: FUnversionedHeader, private val schemas: SparseArrayCompat<FUnversionedPropertySerializer>) {
        /*private*/ internal var schemaIt = 0
        private val zeroMask = header.zeroMask
        private val fragments = header.fragments
        private var fragmentIt = 0
        internal var done = !header.hasValues()
        private var zeroMaskIndex = 0u
        private var remainingFragmentValues = 0u

        init {
            if (!done) {
                skip()
            }
        }

        fun next() {
            ++schemaIt
            --remainingFragmentValues
            if (fragments[fragmentIt].hasAnyZeroes) {
                ++zeroMaskIndex
            }

            if (remainingFragmentValues == 0u) {
                if (fragments[fragmentIt].isLast) {
                    done = true
                } else {
                    ++fragmentIt
                    skip()
                }
            }
        }

        val serializer get() = schemas[schemaIt]

        fun isNonZero() = !fragments[fragmentIt].hasAnyZeroes || !zeroMask[zeroMaskIndex.toInt()]

        private fun skip() {
            schemaIt += fragments[fragmentIt].skipNum.toInt()

            while (fragments[fragmentIt].valueNum == 0u.toUByte()) {
                check(!fragments[fragmentIt].isLast)
                ++fragmentIt
                schemaIt += fragments[fragmentIt].skipNum.toInt()
            }

            remainingFragmentValues = fragments[fragmentIt].valueNum.toUInt()
        }
    }
}

fun deserializeUnversionedProperties(properties: LinkedHashMap<FName, FPropertyTag>, struct: UStruct?, Ar: FAssetArchive) {
    if (struct == null) {
        throw ParserException("Cannot read unversioned properties without a struct", Ar)
    }

    if (GDebugProperties) println("Load: ${struct.name}")
    val header = FUnversionedHeader()
    header.load(Ar)

    if (header.hasValues()) {
        val schemas = getOrCreateUnversionedSchema(struct).serializers

        if (header.hasNonZeroValues) {
            val it = FUnversionedHeader.FIterator(header, schemas)
            while (!it.done) {
                val serializer = it.serializer
                if (serializer != null) {
                    if (GDebugProperties) println("Val: ${it.schemaIt} (IsNonZero: ${it.isNonZero()})")
                    if (it.isNonZero()) {
                        val element = serializer.deserialize(Ar, ReadType.NORMAL)
                        properties[element.name] = element
                        if (GDebugProperties) println(element.toString())
                    } else {
                        val start = Ar.pos()
                        val element = serializer.deserialize(Ar, ReadType.ZERO)
                        properties[element.name] = element
                        if (Ar.pos() != start) {
                            throw ParserException("Zero property $serializer should not advance the archive's position", Ar)
                        }
                    }
                } else {
                    if (it.isNonZero()) {
                        throw UnknownPropertyException("${struct.name}: Unknown property with value ${it.schemaIt}. Can't proceed with serialization (Serialized ${properties.size} properties until now)", Ar)
                    }
                    LOG_JFP.warn("${struct.name}: Unknown property with value ${it.schemaIt} but it's zero so we are good")
                }
                it.next()
            }
        } else {
            val it = FUnversionedHeader.FIterator(header, schemas)
            while (!it.done) {
                check(!it.isNonZero())
                it.serializer?.run { val element = deserialize(Ar, ReadType.ZERO); properties[element.name] = element }
                    ?: LOG_JFP.warn("${struct.name}: Unknown property with value ${it.schemaIt} but it's zero so we are good")
                it.next()
            }
        }
    }
}