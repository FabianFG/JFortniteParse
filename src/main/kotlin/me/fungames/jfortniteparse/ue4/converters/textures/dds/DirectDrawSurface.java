package me.fungames.jfortniteparse.ue4.converters.textures.dds;

public class DirectDrawSurface {
    public static int MAKEFOURCC(char ch0, char ch1, char ch2, char ch3) {
        return ch0 | (ch1 << 8) | (ch2 << 16) | (ch3 << 24);
    }

    public static final int FOURCC_DDS = MAKEFOURCC('D', 'D', 'S', ' ');
    public static final int FOURCC_DXT1 = MAKEFOURCC('D', 'X', 'T', '1');
    public static final int FOURCC_DXT2 = MAKEFOURCC('D', 'X', 'T', '2');
    public static final int FOURCC_DXT3 = MAKEFOURCC('D', 'X', 'T', '3');
    public static final int FOURCC_DXT4 = MAKEFOURCC('D', 'X', 'T', '4');
    public static final int FOURCC_DXT5 = MAKEFOURCC('D', 'X', 'T', '5');
    public static final int FOURCC_RXGB = MAKEFOURCC('R', 'X', 'G', 'B');
    public static final int FOURCC_ATI1 = MAKEFOURCC('A', 'T', 'I', '1');
    public static final int FOURCC_ATI2 = MAKEFOURCC('A', 'T', 'I', '2');

    public static final int FOURCC_A2XY = MAKEFOURCC('A', '2', 'X', 'Y');

    public static final int FOURCC_DX10 = MAKEFOURCC('D', 'X', '1', '0');

    // 32 bit RGB formats.
    public static final int D3DFMT_R8G8B8 = 20;
    public static final int D3DFMT_A8R8G8B8 = 21;
    public static final int D3DFMT_X8R8G8B8 = 22;
    public static final int D3DFMT_R5G6B5 = 23;
    public static final int D3DFMT_X1R5G5B5 = 24;
    public static final int D3DFMT_A1R5G5B5 = 25;
    public static final int D3DFMT_A4R4G4B4 = 26;
    public static final int D3DFMT_R3G3B2 = 27;
    public static final int D3DFMT_A8 = 28;
    public static final int D3DFMT_A8R3G3B2 = 29;
    public static final int D3DFMT_X4R4G4B4 = 30;
    public static final int D3DFMT_A2B10G10R10 = 31;
    public static final int D3DFMT_A8B8G8R8 = 32;
    public static final int D3DFMT_X8B8G8R8 = 33;
    public static final int D3DFMT_G16R16 = 34;
    public static final int D3DFMT_A2R10G10B10 = 35;

    public static final int D3DFMT_A16B16G16R16 = 36;

    // Palette formats.
    public static final int D3DFMT_A8P8 = 40;
    public static final int D3DFMT_P8 = 41;

    // Luminance formats.
    public static final int D3DFMT_L8 = 50;
    public static final int D3DFMT_A8L8 = 51;
    public static final int D3DFMT_A4L4 = 52;
    public static final int D3DFMT_L16 = 81;

    // Floating point formats
    public static final int D3DFMT_R16F = 111;
    public static final int D3DFMT_G16R16F = 112;
    public static final int D3DFMT_A16B16G16R16F = 113;
    public static final int D3DFMT_R32F = 114;
    public static final int D3DFMT_G32R32F = 115;
    public static final int D3DFMT_A32B32G32R32F = 116;

    public static final int DDSD_CAPS = 0x00000001;
    public static final int DDSD_PIXELFORMAT = 0x00001000;
    public static final int DDSD_WIDTH = 0x00000004;
    public static final int DDSD_HEIGHT = 0x00000002;
    public static final int DDSD_PITCH = 0x00000008;
    public static final int DDSD_MIPMAPCOUNT = 0x00020000;
    public static final int DDSD_LINEARSIZE = 0x00080000;
    public static final int DDSD_DEPTH = 0x00800000;

    public static final int DDSCAPS_COMPLEX = 0x00000008;
    public static final int DDSCAPS_TEXTURE = 0x00001000;
    public static final int DDSCAPS_MIPMAP = 0x00400000;
    public static final int DDSCAPS2_VOLUME = 0x00200000;
    public static final int DDSCAPS2_CUBEMAP = 0x00000200;

    public static final int DDSCAPS2_CUBEMAP_POSITIVEX = 0x00000400;
    public static final int DDSCAPS2_CUBEMAP_NEGATIVEX = 0x00000800;
    public static final int DDSCAPS2_CUBEMAP_POSITIVEY = 0x00001000;
    public static final int DDSCAPS2_CUBEMAP_NEGATIVEY = 0x00002000;
    public static final int DDSCAPS2_CUBEMAP_POSITIVEZ = 0x00004000;
    public static final int DDSCAPS2_CUBEMAP_NEGATIVEZ = 0x00008000;
    public static final int DDSCAPS2_CUBEMAP_ALL_FACES = 0x0000FC00;

    public static final int DDPF_ALPHAPIXELS = 0x00000001;
    public static final int DDPF_ALPHA = 0x00000002;
    public static final int DDPF_FOURCC = 0x00000004;
    public static final int DDPF_RGB = 0x00000040;
    public static final int DDPF_PALETTEINDEXED1 = 0x00000800;
    public static final int DDPF_PALETTEINDEXED2 = 0x00001000;
    public static final int DDPF_PALETTEINDEXED4 = 0x00000008;
    public static final int DDPF_PALETTEINDEXED8 = 0x00000020;
    public static final int DDPF_LUMINANCE = 0x00020000;
    public static final int DDPF_ALPHAPREMULT = 0x00008000;
    public static final int DDPF_NORMAL = 0x80000000;    // @@ Custom nv flag.

    // DX10 formats.
    //enum DXGI_FORMAT {
    public static final int DXGI_FORMAT_UNKNOWN = 0;

    public static final int DXGI_FORMAT_R32G32B32A32_TYPELESS = 1;
    public static final int DXGI_FORMAT_R32G32B32A32_FLOAT = 2;
    public static final int DXGI_FORMAT_R32G32B32A32_UINT = 3;
    public static final int DXGI_FORMAT_R32G32B32A32_SINT = 4;

    public static final int DXGI_FORMAT_R32G32B32_TYPELESS = 5;
    public static final int DXGI_FORMAT_R32G32B32_FLOAT = 6;
    public static final int DXGI_FORMAT_R32G32B32_UINT = 7;
    public static final int DXGI_FORMAT_R32G32B32_SINT = 8;

    public static final int DXGI_FORMAT_R16G16B16A16_TYPELESS = 9;
    public static final int DXGI_FORMAT_R16G16B16A16_FLOAT = 10;
    public static final int DXGI_FORMAT_R16G16B16A16_UNORM = 11;
    public static final int DXGI_FORMAT_R16G16B16A16_UINT = 12;
    public static final int DXGI_FORMAT_R16G16B16A16_SNORM = 13;
    public static final int DXGI_FORMAT_R16G16B16A16_SINT = 14;

    public static final int DXGI_FORMAT_R32G32_TYPELESS = 15;
    public static final int DXGI_FORMAT_R32G32_FLOAT = 16;
    public static final int DXGI_FORMAT_R32G32_UINT = 17;
    public static final int DXGI_FORMAT_R32G32_SINT = 18;

    public static final int DXGI_FORMAT_R32G8X24_TYPELESS = 19;
    public static final int DXGI_FORMAT_D32_FLOAT_S8X24_UINT = 20;
    public static final int DXGI_FORMAT_R32_FLOAT_X8X24_TYPELESS = 21;
    public static final int DXGI_FORMAT_X32_TYPELESS_G8X24_UINT = 22;

    public static final int DXGI_FORMAT_R10G10B10A2_TYPELESS = 23;
    public static final int DXGI_FORMAT_R10G10B10A2_UNORM = 24;
    public static final int DXGI_FORMAT_R10G10B10A2_UINT = 25;

    public static final int DXGI_FORMAT_R11G11B10_FLOAT = 26;

    public static final int DXGI_FORMAT_R8G8B8A8_TYPELESS = 27;
    public static final int DXGI_FORMAT_R8G8B8A8_UNORM = 28;
    public static final int DXGI_FORMAT_R8G8B8A8_UNORM_SRGB = 29;
    public static final int DXGI_FORMAT_R8G8B8A8_UINT = 30;
    public static final int DXGI_FORMAT_R8G8B8A8_SNORM = 31;
    public static final int DXGI_FORMAT_R8G8B8A8_SINT = 32;

    public static final int DXGI_FORMAT_R16G16_TYPELESS = 33;
    public static final int DXGI_FORMAT_R16G16_FLOAT = 34;
    public static final int DXGI_FORMAT_R16G16_UNORM = 35;
    public static final int DXGI_FORMAT_R16G16_UINT = 36;
    public static final int DXGI_FORMAT_R16G16_SNORM = 37;
    public static final int DXGI_FORMAT_R16G16_SINT = 38;

    public static final int DXGI_FORMAT_R32_TYPELESS = 39;
    public static final int DXGI_FORMAT_D32_FLOAT = 40;
    public static final int DXGI_FORMAT_R32_FLOAT = 41;
    public static final int DXGI_FORMAT_R32_UINT = 42;
    public static final int DXGI_FORMAT_R32_SINT = 43;

    public static final int DXGI_FORMAT_R24G8_TYPELESS = 44;
    public static final int DXGI_FORMAT_D24_UNORM_S8_UINT = 45;
    public static final int DXGI_FORMAT_R24_UNORM_X8_TYPELESS = 46;
    public static final int DXGI_FORMAT_X24_TYPELESS_G8_UINT = 47;

    public static final int DXGI_FORMAT_R8G8_TYPELESS = 48;
    public static final int DXGI_FORMAT_R8G8_UNORM = 49;
    public static final int DXGI_FORMAT_R8G8_UINT = 50;
    public static final int DXGI_FORMAT_R8G8_SNORM = 51;
    public static final int DXGI_FORMAT_R8G8_SINT = 52;

    public static final int DXGI_FORMAT_R16_TYPELESS = 53;
    public static final int DXGI_FORMAT_R16_FLOAT = 54;
    public static final int DXGI_FORMAT_D16_UNORM = 55;
    public static final int DXGI_FORMAT_R16_UNORM = 56;
    public static final int DXGI_FORMAT_R16_UINT = 57;
    public static final int DXGI_FORMAT_R16_SNORM = 58;
    public static final int DXGI_FORMAT_R16_SINT = 59;

    public static final int DXGI_FORMAT_R8_TYPELESS = 60;
    public static final int DXGI_FORMAT_R8_UNORM = 61;
    public static final int DXGI_FORMAT_R8_UINT = 62;
    public static final int DXGI_FORMAT_R8_SNORM = 63;
    public static final int DXGI_FORMAT_R8_SINT = 64;
    public static final int DXGI_FORMAT_A8_UNORM = 65;

    public static final int DXGI_FORMAT_R1_UNORM = 66;

    public static final int DXGI_FORMAT_R9G9B9E5_SHAREDEXP = 67;

    public static final int DXGI_FORMAT_R8G8_B8G8_UNORM = 68;
    public static final int DXGI_FORMAT_G8R8_G8B8_UNORM = 69;

    public static final int DXGI_FORMAT_BC1_TYPELESS = 70;
    public static final int DXGI_FORMAT_BC1_UNORM = 71;
    public static final int DXGI_FORMAT_BC1_UNORM_SRGB = 72;

    public static final int DXGI_FORMAT_BC2_TYPELESS = 73;
    public static final int DXGI_FORMAT_BC2_UNORM = 74;
    public static final int DXGI_FORMAT_BC2_UNORM_SRGB = 75;

    public static final int DXGI_FORMAT_BC3_TYPELESS = 76;
    public static final int DXGI_FORMAT_BC3_UNORM = 77;
    public static final int DXGI_FORMAT_BC3_UNORM_SRGB = 78;

    public static final int DXGI_FORMAT_BC4_TYPELESS = 79;
    public static final int DXGI_FORMAT_BC4_UNORM = 80;
    public static final int DXGI_FORMAT_BC4_SNORM = 81;

    public static final int DXGI_FORMAT_BC5_TYPELESS = 82;
    public static final int DXGI_FORMAT_BC5_UNORM = 83;
    public static final int DXGI_FORMAT_BC5_SNORM = 84;

    public static final int DXGI_FORMAT_B5G6R5_UNORM = 85;
    public static final int DXGI_FORMAT_B5G5R5A1_UNORM = 86;
    public static final int DXGI_FORMAT_B8G8R8A8_UNORM = 87;
    public static final int DXGI_FORMAT_B8G8R8X8_UNORM = 88;
    //}

    //enum D3D10_RESOURCE_DIMENSION {
    public static final int D3D10_RESOURCE_DIMENSION_UNKNOWN = 0;
    public static final int D3D10_RESOURCE_DIMENSION_BUFFER = 1;
    public static final int D3D10_RESOURCE_DIMENSION_TEXTURE1D = 2;
    public static final int D3D10_RESOURCE_DIMENSION_TEXTURE2D = 3;
    public static final int D3D10_RESOURCE_DIMENSION_TEXTURE3D = 4;
    //}
}
