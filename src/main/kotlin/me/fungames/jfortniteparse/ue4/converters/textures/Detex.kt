package me.fungames.jfortniteparse.ue4.converters.textures

import com.sun.jna.*
import java.io.File
import java.io.FileOutputStream

object Detex {
	class DetexTexture : Structure() {
		@JvmField var format: Int = 0
		@JvmField var data: Pointer? = null
		@JvmField var width: Int = 0
		@JvmField var height: Int = 0
		@JvmField var widthInBlocks: Int = 0
		@JvmField var heightInBlocks: Int = 0

		override fun getFieldOrder() = listOf("format", "data", "width", "height", "widthInBlocks", "heightInBlocks")
	}

	object DetexCompressedTextureFormatIndex {
		const val UNCOMPRESSED = 0
		const val BC1 = 1
		const val DXT1 = BC1
		const val S3TC = BC1
		const val BC1A = 2
		const val DXT1A = BC1A
		const val BC2 = 3
		const val DXT3 = BC2
		const val BC3 = 4
		const val DXT5 = BC3
		const val RGTC1 = 5
		const val BC4_UNORM = RGTC1
		const val SIGNED_RGTC1 = 6
		const val BC4_SNORM = SIGNED_RGTC1
		const val RGTC2 = 7
		const val BC5_UNORM = RGTC2
		const val SIGNED_RGTC2 = 8
		const val BC5_SNORM = SIGNED_RGTC2
		const val BPTC_FLOAT = 9
		const val BC6H_UF16 = BPTC_FLOAT
		const val BPTC_SIGNED_FLOAT = 10
		const val BC6H_SF16 = BPTC_SIGNED_FLOAT
		const val BPTC = 11
		const val BC7 = BPTC
		const val ETC1 = 12
		const val ETC2 = 13
		const val ETC2_PUNCHTHROUGH = 14
		const val ETC2_EAC = 15
		const val EAC_R11 = 16
		const val EAC_SIGNED_R11 = 17
		const val EAC_RG11 = 18
		const val EAC_SIGNED_RG11 = 19
		const val ASTC_4X4 = 20
	}

	object DetexPixelFormat {
		const val _16BIT_COMPONENT_BIT = 0x1
		const val _32BIT_COMPONENT_BIT = 0x2
		const val ALPHA_COMPONENT_BIT = 0x4
		const val RGB_COMPONENT_ORDER_BIT = 0x0
		const val BGR_COMPONENT_ORDER_BIT = 0x8
		const val ONE_COMPONENT_BITS = 0x0
		const val TWO_COMPONENTS_BITS = 0x10
		const val THREE_COMPONENTS_BITS = 0x20
		const val FOUR_COMPONENTS_BITS = 0x30
		const val _8BIT_PIXEL_BITS = 0x000
		const val _16BIT_PIXEL_BITS = 0x100
		const val _24BIT_PIXEL_BITS = 0x200
		const val _32BIT_PIXEL_BITS = 0x300
		const val _48BIT_PIXEL_BITS = 0x500
		const val _64BIT_PIXEL_BITS = 0x700
		const val _96BIT_PIXEL_BITS = 0xB00
		const val _128BIT_PIXEL_BITS = 0xF00
		const val SIGNED_BIT = 0x1000
		const val FLOAT_BIT = 0x2000
		const val HDR_BIT = 0x4000

		const val RGBA8 = ALPHA_COMPONENT_BIT or FOUR_COMPONENTS_BITS or _32BIT_PIXEL_BITS
		const val BGRA8 = ALPHA_COMPONENT_BIT or BGR_COMPONENT_ORDER_BIT or FOUR_COMPONENTS_BITS or _32BIT_PIXEL_BITS
		const val RGBX8 = THREE_COMPONENTS_BITS or _32BIT_PIXEL_BITS
		const val BGRX8 = BGR_COMPONENT_ORDER_BIT or THREE_COMPONENTS_BITS or _32BIT_PIXEL_BITS
		const val RGB8 = THREE_COMPONENTS_BITS or _24BIT_PIXEL_BITS
		const val BGR8 = BGR_COMPONENT_ORDER_BIT or THREE_COMPONENTS_BITS or _24BIT_PIXEL_BITS
		const val R8 = ONE_COMPONENT_BITS or _8BIT_PIXEL_BITS
		const val SIGNED_R8 = ONE_COMPONENT_BITS or _8BIT_PIXEL_BITS or SIGNED_BIT
		const val RG8 = TWO_COMPONENTS_BITS or _16BIT_PIXEL_BITS
		const val SIGNED_RG8 = TWO_COMPONENTS_BITS or _16BIT_PIXEL_BITS or SIGNED_BIT
		const val R16 = _16BIT_COMPONENT_BIT or ONE_COMPONENT_BITS or _16BIT_PIXEL_BITS
		const val SIGNED_R16 = _16BIT_COMPONENT_BIT or ONE_COMPONENT_BITS or _16BIT_PIXEL_BITS or SIGNED_BIT
		const val RG16 = _16BIT_COMPONENT_BIT or TWO_COMPONENTS_BITS or _32BIT_PIXEL_BITS
		const val SIGNED_RG16 = _16BIT_COMPONENT_BIT or TWO_COMPONENTS_BITS or _32BIT_PIXEL_BITS or SIGNED_BIT
		const val RGB16 = _16BIT_COMPONENT_BIT or THREE_COMPONENTS_BITS or _48BIT_PIXEL_BITS
		const val RGBX16 = _16BIT_COMPONENT_BIT or THREE_COMPONENTS_BITS or _64BIT_PIXEL_BITS
		const val RGBA16 = _16BIT_COMPONENT_BIT or ALPHA_COMPONENT_BIT or FOUR_COMPONENTS_BITS or _64BIT_PIXEL_BITS
		const val FLOAT_R16 = _16BIT_COMPONENT_BIT or ONE_COMPONENT_BITS or _16BIT_PIXEL_BITS or FLOAT_BIT
		const val FLOAT_R16_HDR = _16BIT_COMPONENT_BIT or ONE_COMPONENT_BITS or _16BIT_PIXEL_BITS or FLOAT_BIT or HDR_BIT
		const val FLOAT_RG16 = _16BIT_COMPONENT_BIT or TWO_COMPONENTS_BITS or _32BIT_PIXEL_BITS or FLOAT_BIT
		const val FLOAT_RG16_HDR = _16BIT_COMPONENT_BIT or TWO_COMPONENTS_BITS or _32BIT_PIXEL_BITS or FLOAT_BIT or HDR_BIT
		const val FLOAT_RGBX16 = _16BIT_COMPONENT_BIT or THREE_COMPONENTS_BITS or _64BIT_PIXEL_BITS or FLOAT_BIT
		const val FLOAT_RGBX16_HDR = _16BIT_COMPONENT_BIT or THREE_COMPONENTS_BITS or _64BIT_PIXEL_BITS or FLOAT_BIT or HDR_BIT
		const val FLOAT_RGBA16 = _16BIT_COMPONENT_BIT or ALPHA_COMPONENT_BIT or FOUR_COMPONENTS_BITS or _64BIT_PIXEL_BITS or FLOAT_BIT or HDR_BIT
		const val FLOAT_RGBA16_HDR = _16BIT_COMPONENT_BIT or ALPHA_COMPONENT_BIT or FOUR_COMPONENTS_BITS or _64BIT_PIXEL_BITS or FLOAT_BIT
		const val FLOAT_RGB16 = _16BIT_COMPONENT_BIT or THREE_COMPONENTS_BITS or _48BIT_PIXEL_BITS or FLOAT_BIT
		const val FLOAT_RGB16_HDR = _16BIT_COMPONENT_BIT or THREE_COMPONENTS_BITS or _48BIT_PIXEL_BITS or FLOAT_BIT or HDR_BIT
		const val FLOAT_BGRX16 = _16BIT_COMPONENT_BIT or BGR_COMPONENT_ORDER_BIT or THREE_COMPONENTS_BITS or _64BIT_PIXEL_BITS or FLOAT_BIT
		const val FLOAT_BGRX16_HDR = _16BIT_COMPONENT_BIT or BGR_COMPONENT_ORDER_BIT or THREE_COMPONENTS_BITS or _64BIT_PIXEL_BITS or FLOAT_BIT or HDR_BIT
		const val SIGNED_FLOAT_RGBX16 = _16BIT_COMPONENT_BIT or THREE_COMPONENTS_BITS or _64BIT_PIXEL_BITS or SIGNED_BIT or FLOAT_BIT
		const val SIGNED_FLOAT_BGRX16 = _16BIT_COMPONENT_BIT or BGR_COMPONENT_ORDER_BIT or THREE_COMPONENTS_BITS or _64BIT_PIXEL_BITS or SIGNED_BIT or FLOAT_BIT
		const val FLOAT_R32 = _32BIT_COMPONENT_BIT or ONE_COMPONENT_BITS or _32BIT_PIXEL_BITS or FLOAT_BIT
		const val FLOAT_R32_HDR = _32BIT_COMPONENT_BIT or ONE_COMPONENT_BITS or _32BIT_PIXEL_BITS or FLOAT_BIT or HDR_BIT
		const val FLOAT_RG32 = _32BIT_COMPONENT_BIT or TWO_COMPONENTS_BITS or _64BIT_PIXEL_BITS or FLOAT_BIT
		const val FLOAT_RG32_HDR = _32BIT_COMPONENT_BIT or TWO_COMPONENTS_BITS or _64BIT_PIXEL_BITS or FLOAT_BIT or HDR_BIT
		const val FLOAT_RGB32 = _32BIT_COMPONENT_BIT or THREE_COMPONENTS_BITS or _96BIT_PIXEL_BITS or FLOAT_BIT
		const val FLOAT_RGB32_HDR = _32BIT_COMPONENT_BIT or THREE_COMPONENTS_BITS or _96BIT_PIXEL_BITS or FLOAT_BIT or HDR_BIT
		const val FLOAT_RGBX32 = _32BIT_COMPONENT_BIT or THREE_COMPONENTS_BITS or _128BIT_PIXEL_BITS or FLOAT_BIT
		const val FLOAT_RGBX32_HDR = _32BIT_COMPONENT_BIT or THREE_COMPONENTS_BITS or _128BIT_PIXEL_BITS or FLOAT_BIT or HDR_BIT
		const val FLOAT_RGBA32 = _32BIT_COMPONENT_BIT or ALPHA_COMPONENT_BIT or FOUR_COMPONENTS_BITS or _128BIT_PIXEL_BITS or FLOAT_BIT
		const val FLOAT_RGBA32_HDR = _32BIT_COMPONENT_BIT or ALPHA_COMPONENT_BIT or FOUR_COMPONENTS_BITS or _128BIT_PIXEL_BITS or FLOAT_BIT or HDR_BIT
		const val A8 = ALPHA_COMPONENT_BIT or ONE_COMPONENT_BITS or _8BIT_PIXEL_BITS
	}

	object DetexTextureFormat {
		const val PIXEL_FORMAT_MASK = 0x0000FFFF
		const val _128BIT_BLOCK_BIT = 0x00800000

		const val BC1 = (DetexCompressedTextureFormatIndex.BC1 shl 24) or DetexPixelFormat.RGBX8
		const val BC1A = (DetexCompressedTextureFormatIndex.BC1A shl 24) or DetexPixelFormat.RGBA8
		const val BC2 = (DetexCompressedTextureFormatIndex.BC2 shl 24) or _128BIT_BLOCK_BIT or DetexPixelFormat.RGBA8
		const val BC3 = (DetexCompressedTextureFormatIndex.BC3 shl 24) or _128BIT_BLOCK_BIT or DetexPixelFormat.RGBA8
		const val RGTC1 = (DetexCompressedTextureFormatIndex.RGTC1 shl 24) or DetexPixelFormat.R8
		const val SIGNED_RGTC1 = (DetexCompressedTextureFormatIndex.SIGNED_RGTC1 shl 24) or DetexPixelFormat.SIGNED_R16
		const val RGTC2 = (DetexCompressedTextureFormatIndex.RGTC2 shl 24) or _128BIT_BLOCK_BIT or DetexPixelFormat.RG8
		const val SIGNED_RGTC2 = (DetexCompressedTextureFormatIndex.SIGNED_RGTC2 shl 24) or _128BIT_BLOCK_BIT or DetexPixelFormat.SIGNED_RG16
		const val BPTC_FLOAT = (DetexCompressedTextureFormatIndex.BPTC_FLOAT shl 24) or _128BIT_BLOCK_BIT or DetexPixelFormat.FLOAT_RGBX16
		const val BPTC_SIGNED_FLOAT = (DetexCompressedTextureFormatIndex.BPTC_SIGNED_FLOAT shl 24) or _128BIT_BLOCK_BIT or DetexPixelFormat.SIGNED_FLOAT_RGBX16
		const val BPTC = (DetexCompressedTextureFormatIndex.BPTC shl 24) or _128BIT_BLOCK_BIT or DetexPixelFormat.RGBA8
		const val ETC1 = (DetexCompressedTextureFormatIndex.ETC1 shl 24) or DetexPixelFormat.RGBX8
		const val ETC2 = (DetexCompressedTextureFormatIndex.ETC2 shl 24) or DetexPixelFormat.RGBX8
		const val ETC2_PUNCHTHROUGH = (DetexCompressedTextureFormatIndex.ETC2_PUNCHTHROUGH shl 24) or DetexPixelFormat.RGBA8
		const val ETC2_EAC = (DetexCompressedTextureFormatIndex.ETC2_EAC shl 24) or _128BIT_BLOCK_BIT or DetexPixelFormat.RGBA8
		const val EAC_R11 = (DetexCompressedTextureFormatIndex.EAC_R11 shl 24) or DetexPixelFormat.R16
		const val EAC_SIGNED_R11 = (DetexCompressedTextureFormatIndex.EAC_SIGNED_R11 shl 24) or DetexPixelFormat.SIGNED_R16
		const val EAC_RG11 = (DetexCompressedTextureFormatIndex.EAC_RG11 shl 24) or _128BIT_BLOCK_BIT or DetexPixelFormat.RG16
		const val EAC_SIGNED_RG11 = (DetexCompressedTextureFormatIndex.EAC_SIGNED_RG11 shl 24) or _128BIT_BLOCK_BIT or DetexPixelFormat.SIGNED_RG16
		const val ASTC_4X4 = (DetexCompressedTextureFormatIndex.ASTC_4X4 shl 24) or _128BIT_BLOCK_BIT or DetexPixelFormat.RGBA8
	}

	interface DetexLibrary : Library {
		fun detexDecompressTextureLinear(texture: DetexTexture, pixel_buffer: Pointer, pixel_format: Int): Boolean
	}

	@JvmStatic
	private lateinit var detexLib: DetexLibrary

	@Throws(IllegalStateException::class)
	private fun ensureLib() {
		if (::detexLib.isInitialized)
			return
		val osName = System.getProperty("os.name")
		val osArch = System.getProperty("os.arch")
		val isWindows = osName.startsWith("Windows")
		val isLinux = osName == "Linux"
		val osDirName: String
		val libName: String
		when {
			isWindows -> {
				osDirName = "windows"
				libName = "detex.dll"
			}
			isLinux -> {
				osDirName = "linux"
				libName = "libdetex.so"
			}
			else -> throw IllegalStateException("Detex library is not supported on $osName")
		}
		val libFile = File(libName)
		var canLoad = libFile.exists()
		if (!canLoad) {
			val resourcePath = "/native/$osDirName/$osArch/$libName"
			val input = this::class.java.getResourceAsStream(resourcePath)
			if (input != null) {
				val out = FileOutputStream(libFile)
				input.copyTo(out)
				input.close()
				out.close()
				canLoad = true
			}
		}
		if (!canLoad){
			throw IllegalStateException("Detex library could not be loaded")
		}
		System.setProperty("jna.library.path", System.getProperty("user.dir"))
		try {
			detexLib = Native.load(libName, DetexLibrary::class.java)
		} catch (e: Exception) {
			throw IllegalStateException("Detex library failed to load", e)
		}
	}

	@JvmStatic
	fun decompressTextureLinear(inp: ByteArray, dst: ByteArray, width: Int, height: Int, inputFormat: Int/*DetexTextureFormat*/, outputPixelFormat: Int/*DetexPixelFormat*/): Boolean {
		ensureLib()

		val tex = DetexTexture()
		tex.format = inputFormat
		tex.data = Memory(inp.size.toLong()).apply { write(0, inp, 0, inp.size) }
		tex.width = width
		tex.height = height
		tex.widthInBlocks = width / 4
		tex.heightInBlocks = height / 4

		val pixelBuffer = Memory(dst.size.toLong())
		val success = detexLib.detexDecompressTextureLinear(tex, pixelBuffer, outputPixelFormat)
		if (success) {
			pixelBuffer.read(0, dst, 0, dst.size)
		}

		return success
	}
}