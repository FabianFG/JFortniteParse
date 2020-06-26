package me.fungames.jfortniteparse.util

fun String.parseHexBinary(): ByteArray {
    val len = this.length

    // "111" is not a valid hex encoding.
    require(len % 2 == 0) { "hexBinary needs to be even-length: $this" }

    val out = ByteArray(len / 2)

    var i = 0
    while (i < len) {
        val h = hexToBin(this[i])
        val l = hexToBin(this[i + 1])
        require(!(h == -1 || l == -1)) { "contains illegal character for hexBinary: $this" }

        out[i / 2] = (h * 16 + l).toByte()
        i += 2
    }

    return out
}

private val hexCode = "0123456789ABCDEF".toCharArray()

fun ByteArray.printHexBinary() : String {
    val r = StringBuilder(size * 2)
    for (b in this) {
        r.append(hexCode[b.toInt() shr 4 and 0xF])
        r.append(hexCode[b.toInt() and 0xF])
    }
    return r.toString()
}

private fun hexToBin(ch: Char): Int {
    if (ch in '0'..'9') {
        return ch - '0'
    }
    if (ch in 'A'..'F') {
        return ch - 'A' + 10
    }
    return if (ch in 'a'..'f') {
        ch - 'a' + 10
    } else -1
}