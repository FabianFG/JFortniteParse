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