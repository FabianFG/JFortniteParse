package me.fungames.jfortniteparse.util

@ExperimentalUnsignedTypes
fun UShort.toFloat16(): Float {
    val hbits = this.toInt()
    var mant = hbits and 0x03ff // 10 bits mantissa
    var exp = hbits and 0x7c00 // 5 bits exponent
    if (exp == 0x7c00)
    // NaN/Inf
        exp = 0x3fc00 // -> NaN/Inf
    else if (exp != 0)
    // normalized value
    {
        exp += 0x1c000 // exp - 15 + 127
        if (mant == 0 && exp > 0x1c400)
        // smooth transition
            return Float.fromBits((hbits and 0x8000 shl 16 or (exp shl 13) or 0x3ff).toInt())
    } else if (mant != 0)
    // && exp==0 -> subnormal
    {
        exp = 0x1c400 // make it normal
        do {
            mant = mant shl 1 // mantissa * 2
            exp -= 0x400 // decrease exp by 1
        } while (mant and 0x400 == 0) // while not normal
        mant = mant and 0x3ff // discard subnormal bit
    } // else +/-0 -> +/-0
    return Float.fromBits( // combine all parts
        (hbits and 0x8000 shl 16 // sign << ( 31 - 15 )
                or (exp or mant shl 13)).toInt()
    ) // value << ( 23 - 10 )
}