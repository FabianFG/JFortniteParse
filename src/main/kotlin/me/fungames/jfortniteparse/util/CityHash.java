/*
 * Copyright (C) 2012 tamtam180
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.fungames.jfortniteparse.util;

/**
 * @author tamtam180 - kirscheless at gmail.com
 * @author modified by amrsatrio
 * @see <a href="https://opensource.googleblog.com/2011/04/introducing-cityhash.html">https://opensource.googleblog.com/2011/04/introducing-cityhash.html</a>
 * @see <a href="https://github.com/google/cityhash">https://github.com/google/cityhash</a>
 */
public class CityHash {
    // Some primes between 2^63 and 2^64 for various uses.
    private static final long k0 = 0xc3a5c85c97cb3127L;
    private static final long k1 = 0xb492b66fbe98f273L;
    private static final long k2 = 0x9ae16a3b2f90404fL;

    private static long toLongLE(byte[] b, int i) {
        return (((long) b[i + 7] << 56) +
            ((long) (b[i + 6] & 255) << 48) +
            ((long) (b[i + 5] & 255) << 40) +
            ((long) (b[i + 4] & 255) << 32) +
            ((long) (b[i + 3] & 255) << 24) +
            ((b[i + 2] & 255) << 16) +
            ((b[i + 1] & 255) << 8) +
            (b[i] & 255));
    }

    private static int toIntLE(byte[] b, int i) {
        return (((b[i + 3] & 255) << 24) + ((b[i + 2] & 255) << 16) + ((b[i + 1] & 255) << 8) + (b[i] & 255));
    }

    private static long bswap64(long value) {
        long b1 = value & 0xff;
        long b2 = (value >> 8) & 0xff;
        long b3 = (value >> 16) & 0xff;
        long b4 = (value >> 24) & 0xff;
        long b5 = (value >> 32) & 0xff;
        long b6 = (value >> 40) & 0xff;
        long b7 = (value >> 48) & 0xff;
        long b8 = (value >> 56) & 0xff;

        return b1 << 56 | b2 << 48 | b3 << 40 | b4 << 32 | b5 << 24 | b6 << 16 | b7 << 8 | b8;
    }

    private static long fetch64(byte[] s, int pos) {
        return toLongLE(s, pos);
    }

    private static int fetch32(byte[] s, int pos) {
        return toIntLE(s, pos);
    }

    /**
     * Bitwise right rotate.  Normally this will compile to a single
     * instruction, especially if the shift is a manifest constant.
     */
    private static long rotate(long val, int shift) {
        // Avoid shifting by 64: doing so yields an undefined result.
        return shift == 0 ? val : ((val >>> shift) | (val << (64 - shift)));
    }

    private static long shiftMix(long val) {
        return val ^ (val >>> 47);
    }

    private static long hashLen16(long u, long v) {
        return cityHash128to64(u, v);
    }

    private static long hashLen16(long u, long v, long mul) {
        System.out.printf("HashLen16(u, v, mul) = %x, %x, %x\n", u, v, mul);
        // Murmur-inspired hashing.
        long a = (u ^ v) * mul;
        a ^= (a >>> 47);
        long b = (v ^ a) * mul;
        b ^= (b >>> 47);
        b *= mul;
        System.out.printf("return %x;\n", b);
        return b;
    }

    private static long hashLen0to16(byte[] s, int pos, int len) {
        if (len > 8) {
            long mul = k2 + len * 2;
            long a = fetch64(s, pos) + k2;
            long b = fetch64(s, pos + len - 8);
            long c = rotate(b, 37) * mul + a;
            long d = (rotate(a, 25) + b) * mul;
            return hashLen16(c, d, mul);
        }
        if (len >= 4) {
            long mul = k2 + len * 2;
            long a = fetch32(s, pos);
            return hashLen16(len + (a << 3), fetch32(s, pos + len - 4), mul);
        }
        if (len > 0) {
            int a = s[pos] & 0xFF;
            int b = s[pos + (len >>> 1)] & 0xFF;
            int c = s[pos + len - 1] & 0xFF;
            int y = a + (b << 8);
            int z = len + (c << 2);
            return shiftMix(y * k2 ^ z * k0) * k2;
        }
        return k2;
    }

    /**
     * This probably works well for 16-byte strings as well, but it may be overkill
     * in that case.
     */
    private static long hashLen17to32(byte[] s, int pos, int len) {
        long mul = k2 + len * 2;
        long a = fetch64(s, pos) * k1;
        long b = fetch64(s, pos + 8);
        long c = fetch64(s, pos + len - 8) * mul;
        long d = fetch64(s, pos + len - 16) * k2;
        return hashLen16(rotate(a + b, 43) + rotate(c, 30) + d,
            a + rotate(b + k2, 18) + c, mul);
    }

    /**
     * Return a 16-byte hash for 48 bytes.  Quick and dirty.
     * Callers do best to use "random-looking" values for a and b.
     */
    private static long[] weakHashLen32WithSeeds(
        long w, long x, long y, long z, long a, long b) {
        a += w;
        b = rotate(b + a + z, 21);
        long c = a;
        a += x;
        a += y;
        b += rotate(a, 44);
        return new long[]{a + z, b + c};
    }

    /**
     * Return a 16-byte hash for s[0] ... s[31], a, and b.  Quick and dirty.
     */
    private static long[] weakHashLen32WithSeeds(
        byte[] s, int pos, long a, long b) {
        return weakHashLen32WithSeeds(
            fetch64(s, pos),
            fetch64(s, pos + 8),
            fetch64(s, pos + 16),
            fetch64(s, pos + 24),
            a,
            b);
    }

    /**
     * Return an 8-byte hash for 33 to 64 bytes.
     */
    private static long hashLen33to64(byte[] s, int pos, int len) {
        long mul = k2 + len * 2;
        long a = fetch64(s, pos) * k2;
        long b = fetch64(s, pos + 8);
        long c = fetch64(s, pos + len - 24);
        long d = fetch64(s, pos + len - 32);
        long e = fetch64(s, pos + 16) * k2;
        long f = fetch64(s, pos + 24) * 9;
        long g = fetch64(s, pos + len - 8);
        long h = fetch64(s, pos + len - 16) * mul;
        long u = rotate(a + g, 43) + (rotate(b, 30) + c) * 9;
        long v = ((a + g) ^ d) + f + 1;
        long w = bswap64((u + v) * mul) + h;
        long x = rotate(e + f, 42) + c;
        long y = (bswap64((v + w) * mul) + g) * mul;
        long z = e + f + c;
        a = bswap64((x + z) * mul + y) + b;
        b = shiftMix((z + a) * mul + d + h) * mul;
        return b + x;
    }

    /**
     * Hash function for a byte array.
     */
    public static long cityHash64(byte[] s, int pos, int len) {
        if (len <= 32) {
            if (len <= 16) {
                return hashLen0to16(s, pos, len);
            } else {
                return hashLen17to32(s, pos, len);
            }
        } else if (len <= 64) {
            return hashLen33to64(s, pos, len);
        }

        // For strings over 64 bytes we hash the end first, and then as we
        // loop we keep 56 bytes of state: v, w, x, y, and z.
        long x = fetch64(s, pos + len - 40);
        long y = fetch64(s, pos + len - 16) + fetch64(s, pos + len - 56);
        long z = hashLen16(fetch64(s, pos + len - 48) + len, fetch64(s, pos + len - 24));
        long[] v = weakHashLen32WithSeeds(s, pos + len - 64, len, z);
        long[] w = weakHashLen32WithSeeds(s, pos + len - 32, y + k1, x);
        x = x * k1 + fetch64(s, pos);

        // Decrease len to the nearest multiple of 64, and operate on 64-byte chunks.
        len = (len - 1) & (~63);
        do {
            x = rotate(x + y + v[0] + fetch64(s, pos + 8), 37) * k1;
            y = rotate(y + v[1] + fetch64(s, pos + 48), 42) * k1;
            x ^= w[1];
            y += v[0] + fetch64(s, pos + 40);
            z = rotate(z + w[0], 33) * k1;
            v = weakHashLen32WithSeeds(s, pos, v[1] * k1, x + w[0]);
            w = weakHashLen32WithSeeds(s, pos + 32, z + w[1], y + fetch64(s, pos + 16));
            { long swap = z; z = x; x = swap; }
            pos += 64;
            len -= 64;
        } while (len != 0);

        return hashLen16(hashLen16(v[0], w[0]) + shiftMix(y) * k1 + z,
            hashLen16(v[1], w[1]) + x);
    }

    /**
     * Hash function for a byte array.  For convenience, a 64-bit seed is also
     * hashed into the result.
     */
    public static long cityHash64WithSeed(byte[] s, int pos, int len, long seed) {
        return cityHash64WithSeeds(s, pos, len, k2, seed);
    }

    /**
     * Hash function for a byte array.  For convenience, two seeds are also
     * hashed into the result.
     */
    public static long cityHash64WithSeeds(byte[] s, int pos, int len, long seed0, long seed1) {
        return hashLen16(cityHash64(s, pos, len) - seed0, seed1);
    }

    /**
     * Hash function for a byte array.  Most useful in 32-bit binaries.
     */
    public static long cityHash32(byte[] s, int pos, int len) {
        return -1;
    }

    /**
     * Hash 128 input bits down to 64 bits of output.
     * This is intended to be a reasonably good hash function.
     */
    public static long cityHash128to64(long u, long v) {
        // Murmur-inspired hashing.
        final long kMul = 0x9ddfea08eb382d69L;
        long a = (u ^ v) * kMul;
        a ^= (a >>> 47);
        long b = (v ^ a) * kMul;
        b ^= (b >>> 47);
        b *= kMul;
        return b;
    }
}
