package me.fungames.jfortniteparse.ue4.reader

open class FArchiveProxy(val wrappedAr: FArchive) : FArchive() {
    override var littleEndian: Boolean
        get() = wrappedAr.littleEndian
        set(value) { wrappedAr.littleEndian = value }

    override fun clone() = FArchiveProxy(wrappedAr)

    override fun seek(pos: Int) = wrappedAr.seek(pos)
    override fun size() = wrappedAr.size()
    override fun pos() = wrappedAr.pos()

    override fun read() = wrappedAr.read()
    override fun read(b: ByteArray, off: Int, len: Int) = wrappedAr.read(b, off, len)
    override fun readBuffer(size: Int) = wrappedAr.readBuffer(size)

    override fun skip(n: Long) = wrappedAr.skip(n)
    override fun close() = wrappedAr.close()
    override fun printError() = wrappedAr.printError()

    //Only overriding these to keep optimal performance with FByteArchive
    override fun readInt8() = wrappedAr.readInt8()
    override fun readInt16() = wrappedAr.readInt16()
    override fun readInt32() = wrappedAr.readInt32()
    override fun readInt64() = wrappedAr.readInt64()
    override fun readFloat32() = wrappedAr.readFloat32()
    override fun readDouble() = wrappedAr.readDouble()
}