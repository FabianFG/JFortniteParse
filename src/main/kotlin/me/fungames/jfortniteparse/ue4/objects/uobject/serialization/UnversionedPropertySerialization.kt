package me.fungames.jfortniteparse.ue4.objects.uobject.serialization

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.OnlyAnnotated
import me.fungames.jfortniteparse.ue4.assets.UProperty
import me.fungames.jfortniteparse.ue4.assets.exports.UObject
import me.fungames.jfortniteparse.ue4.assets.objects.FPropertyTag
import me.fungames.jfortniteparse.ue4.assets.objects.FPropertyTagType
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.unprefix
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.util.INDEX_NONE
import me.fungames.jfortniteparse.util.divideAndRoundUp
import me.fungames.jfortniteparse.util.indexOfFirst
import java.lang.reflect.Field
import java.lang.reflect.ParameterizedType
import java.util.*

// custom class
class PropertyInfo {
    @JvmField var name: String? = null
    @JvmField var type: String? = null
    @JvmField var structType: String? = null
    @JvmField var bool: Boolean? = null
    @JvmField var enumName: String? = null
    @JvmField var enumType: String? = null
    @JvmField var innerType: String? = null
    @JvmField var valueType: String? = null
    @JvmField var arrayDim = 1

    var structClass: Class<*>? = null
    var enumClass: Class<out Enum<*>>? = null

    val field: Field

    constructor(field: Field, ann: UProperty?) {
        this.field = field

        if (ann != null) {
            name = ann.name.takeIf { it.isNotEmpty() }
            arrayDim = ann.arrayDim
            enumType = ann.enumType.takeIf { it.isNotEmpty() }
        }
        if (name == null) {
            name = field.name
        }

        type = propertyType(field.type)

        when (type) {
            "EnumProperty" -> {
                enumName = field.type.simpleName
                enumClass = field.type as Class<out Enum<*>>?
            }
            "StructProperty" -> {
                structType = field.type.simpleName.unprefix()
                structClass = field.type
            }
            "ArrayProperty", "SetProperty" -> applyInner(false, true)
            "MapProperty" -> {
                applyInner(false, false)
                applyInner(true, true)
            }
        }
    }

    private fun applyInner(applyToValue: Boolean, applyStructOrEnumClass: Boolean) {
        val typeArgs = (field.genericType as ParameterizedType).actualTypeArguments
        val idx = if (applyToValue) 1 else 0
        val type = typeArgs[idx] as Class<*>
        val propertyType = propertyType(type)
        if (applyToValue) {
            valueType = propertyType
        } else {
            innerType = propertyType
        }
        if (applyStructOrEnumClass) {
            if (propertyType == "EnumProperty") {
                enumName = type.simpleName.unprefix()
                enumClass = type as Class<out Enum<*>>?
            } else if (propertyType == "StructProperty") {
                structType = type.simpleName.unprefix()
                structClass = type
            }
        }
    }

    private fun propertyType(c: Class<*>): String {
        return when {
            c == Boolean::class.javaPrimitiveType || c == Boolean::class.javaObjectType -> "BoolProperty"
            c == Char::class.javaPrimitiveType || c == Char::class.javaObjectType -> "CharProperty"
            c == Double::class.javaPrimitiveType || c == Double::class.javaObjectType -> "DoubleProperty"
            c == Float::class.javaPrimitiveType || c == Float::class.javaObjectType -> "FloatProperty"
            c == Byte::class.javaPrimitiveType || c == Byte::class.javaObjectType -> "Int8Property"
            c == Short::class.javaPrimitiveType || c == Short::class.javaObjectType -> "Int16Property"
            c == Int::class.javaPrimitiveType || c == Int::class.javaObjectType -> "IntProperty"
            c == Long::class.javaPrimitiveType || c == Long::class.javaObjectType -> "Int64Property"
            c == UByte::class.java -> "UInt8Property"
            c == UShort::class.java -> "UInt16Property"
            c == UInt::class.java -> "UIntProperty"
            c == ULong::class.java -> "UInt64Property"
            c == String::class.java -> "StrProperty"
            c == FName::class.java -> "NameProperty"
            c == FText::class.java -> "TextProperty"
            c.isEnum -> "EnumProperty"
            List::class.java.isAssignableFrom(c) -> "ArrayProperty"
            Set::class.java.isAssignableFrom(c) -> "SetProperty"
            Map::class.java.isAssignableFrom(c) -> "MapProperty"
            c == FPackageIndex::class.java || UObject::class.java.isAssignableFrom(c) -> "ObjectProperty"
            c == FSoftObjectPath::class.java -> "SoftObjectProperty"
            else -> "StructProperty"
        }
    }
}

class FUnversionedPropertySerializer(val propertyInfo: PropertyInfo, val arrayIndex: Int) {
    fun deserialize(Ar: FAssetArchive): FPropertyTag {
        val propertyType = propertyInfo.type!!
        val tag = FPropertyTag(propertyInfo)
        tag.arrayIndex = arrayIndex
        tag.prop = FPropertyTagType.readFPropertyTagType(Ar, propertyType, tag, FPropertyTagType.ReadType.NORMAL)
        return tag
    }

    fun loadZero(Ar: FAssetArchive): FPropertyTag {
        val propertyType = propertyInfo.type!!
        val tag = FPropertyTag(propertyInfo)
        tag.prop = FPropertyTagType.readFPropertyTagType(Ar, propertyType, tag, FPropertyTagType.ReadType.ZERO)
        return tag
    }

    fun writeToField() {

    }

    override fun toString() = propertyInfo.field.type.simpleName + ' ' + propertyInfo.field.name
}

/**
 * Serialization is based on indices into this property array
 */
class FUnversionedStructSchema(struct: Class<*>) {
    val serializers = mutableMapOf<Int, FUnversionedPropertySerializer>()

    init {
        var index = 0
        var lastClassIndex = 0
        var clazz: Class<*>? = struct
        while (clazz != null && clazz != UObject::class.java && clazz != Object::class.java) {
            val bOnlyAnnotated = clazz.isAnnotationPresent(OnlyAnnotated::class.java)
            for (field in clazz.declaredFields) {
                val ann = field.getAnnotation(UProperty::class.java)
                if (bOnlyAnnotated && ann == null) {
                    continue
                }
                index += ann?.skipPrevious ?: 0
                val propertyInfo = PropertyInfo(field, ann)
                for (arrayIdx in 0 until propertyInfo.arrayDim) {
                    println("${lastClassIndex + index} = ${propertyInfo.field.name}")
                    serializers[lastClassIndex + index] = FUnversionedPropertySerializer(propertyInfo, arrayIdx)
                }
                index += (ann?.skipNext ?: 0) + 1
            }
            lastClassIndex += index
            index = 0
            clazz = clazz.superclass
        }
    }
}

val schemaCache = mutableMapOf<Class<*>, FUnversionedStructSchema>()

fun getOrCreateUnversionedSchema(struct: Class<*>): FUnversionedStructSchema {
    /*val existingSchema = struct.existingSchema
    if (existingSchema != null) {
        return existingSchema
    }*/

    return schemaCache.getOrPut(struct) { FUnversionedStructSchema(struct) }
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

    protected fun loadZeroMaskData(Ar: FArchive, numBits: UInt): BitSet {
        /*val num = numBits.divideAndRoundUp(64u).toInt()
        println("load zero mask num = $num")
        val data = LongArray(num)
        when {
            numBits <= 8u -> data[0] = Ar.readInt8().toLong()
            numBits <= 16u -> data[0] = Ar.readInt16().toLong()
            numBits <= 32u -> data[0] = Ar.readInt32().toLong()
            else -> for (idx in 0 until num) {
                data[idx] = Ar.readInt64()
            }
        }*/
        /*return BitSet.valueOf(Ar.read(when {
            numBits <= 8u -> 1u
            numBits <= 16u -> 2u
            else -> numBits.divideAndRoundUp(32u) * 4u
        }.toInt()))*/
        val size = when {
            numBits <= 8u -> 1u
            numBits <= 16u -> 2u
            else -> numBits.divideAndRoundUp(32u) * 4u
        }.toInt()
        return BitSet.valueOf(Ar.read(size))//.also { println("zeromask $size, ${it.size()}") }
    }

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

fun deserializeUnversionedProperties(struct: Class<*>, Ar: FAssetArchive): MutableList<FPropertyTag> {
    //check(canUseUnversionedPropertySerialization())

    val properties = mutableListOf<FPropertyTag>()
    val header = FUnversionedHeader()
    header.load(Ar)
    println("Load: ${struct.simpleName}")

    if (header.hasValues()) {
        val schemas = getOrCreateUnversionedSchema(struct).serializers

        if (header.bHasNonZeroValues) {
            //val defaults = FDefaultStruct(defaultsData, defaultsStruct)

            val it = FUnversionedHeader.FIterator(header, schemas)
            while (!it.bDone) {
                val serializer = it.serializer
                if (serializer != null) {
                    println("Val: ${it.schemaIt} (IsNonZero: ${it.isNonZero()})")
                    if (it.isNonZero()) {
                        val element = serializer.deserialize(Ar)
                        properties.add(element)
                        println(element.toString())
                    } else {
                        properties.add(serializer.loadZero(Ar))
                    }
                } else {
                    UClass.logger.warn("Unknown property for ${struct.simpleName} with value ${it.schemaIt}${if (it.isNonZero()) ", cannot proceed with serialization" else " but it's zero so we are good"}")
                }
                it.next()
            }
        } else {
            val it = FUnversionedHeader.FIterator(header, schemas)
            while (!it.bDone) {
                check(!it.isNonZero())
                it.serializer?.run { properties.add(loadZero(Ar)) }
                    ?: UClass.logger.warn("Unknown property for ${struct.simpleName} with value ${it.schemaIt} but it's zero so we are good")
                it.next()
            }
        }
    }

    return properties
}