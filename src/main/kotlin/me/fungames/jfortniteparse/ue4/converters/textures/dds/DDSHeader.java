package me.fungames.jfortniteparse.ue4.converters.textures.dds;

import static me.fungames.jfortniteparse.ue4.converters.textures.dds.DirectDrawSurface.*;

/**
 * DDS file header.
 */
public class DDSHeader {
    public int fourcc;
    public int size;
    public int flags;
    public int height;
    public int width;
    public int pitch;
    public int depth;
    public int mipmapcount;
    public int[] reserved = new int[11];
    public DDSPixelFormat pf = new DDSPixelFormat();
    public DDSCaps caps = new DDSCaps();
    public int notused;
    public DDSHeader10 header10 = new DDSHeader10();


    // Helper methods.
    public DDSHeader() {
        fourcc = FOURCC_DDS;
        size = 124;
        flags = (DDSD_CAPS | DDSD_PIXELFORMAT);
        height = 0;
        width = 0;
        pitch = 0;
        depth = 0;
        mipmapcount = 0;
        //memset(reserved, 0, sizeof(reserved));

        // Store version information on the reserved header attributes.
        reserved[9] = MAKEFOURCC('N', 'V', 'T', 'T');
        reserved[10] = (2 << 16) | (0 << 8) | (6);    // major.minor.revision

        pf.size = 32;
        pf.flags = 0;
        pf.fourcc = 0;
        pf.bitcount = 0;
        pf.rmask = 0;
        pf.gmask = 0;
        pf.bmask = 0;
        pf.amask = 0;
        caps.caps1 = DDSCAPS_TEXTURE;
        caps.caps2 = 0;
        caps.caps3 = 0;
        caps.caps4 = 0;
        notused = 0;

        header10.dxgiFormat = DXGI_FORMAT_UNKNOWN;
        header10.resourceDimension = D3D10_RESOURCE_DIMENSION_UNKNOWN;
        header10.miscFlag = 0;
        header10.arraySize = 0;
        header10.reserved = 0;
    }

    public void setWidth(int w) {
        flags |= DDSD_WIDTH;
        width = w;
    }

    public void setHeight(int h) {
        flags |= DDSD_HEIGHT;
        height = h;
    }

    public void setDepth(int d) {
        flags |= DDSD_DEPTH;
        height = d;
    }

    public void setMipmapCount(int count) {
        if (count == 0 || count == 1) {
            flags &= ~DDSD_MIPMAPCOUNT;
            mipmapcount = 0;

            if (caps.caps2 == 0) {
                caps.caps1 = DDSCAPS_TEXTURE;
            } else {
                caps.caps1 = DDSCAPS_TEXTURE | DDSCAPS_COMPLEX;
            }
        } else {
            flags |= DDSD_MIPMAPCOUNT;
            mipmapcount = count;

            caps.caps1 |= DDSCAPS_COMPLEX | DDSCAPS_MIPMAP;
        }
    }

    public void setTexture2D() {
        header10.resourceDimension = D3D10_RESOURCE_DIMENSION_TEXTURE2D;
    }

    public void setTexture3D() {
        caps.caps2 = DDSCAPS2_VOLUME;

        header10.resourceDimension = D3D10_RESOURCE_DIMENSION_TEXTURE3D;
    }

    public void setTextureCube() {
        caps.caps1 |= DDSCAPS_COMPLEX;
        caps.caps2 = DDSCAPS2_CUBEMAP | DDSCAPS2_CUBEMAP_ALL_FACES;

        header10.resourceDimension = D3D10_RESOURCE_DIMENSION_TEXTURE2D;
        header10.arraySize = 6;
    }

    public void setLinearSize(int size) {
        flags &= ~DDSD_PITCH;
        flags |= DDSD_LINEARSIZE;
        pitch = size;
    }

    public void setPitch(int pitch) {
        flags &= ~DDSD_LINEARSIZE;
        flags |= DDSD_PITCH;
        this.pitch = pitch;
    }

    public void setFourCC(char c0, char c1, char c2, char c3) {
        // set fourcc pixel format.
        pf.flags = DDPF_FOURCC;
        pf.fourcc = MAKEFOURCC(c0, c1, c2, c3);

        if (pf.fourcc == FOURCC_ATI2) {
            pf.bitcount = FOURCC_A2XY;
        } else {
            pf.bitcount = 0;
        }

        pf.rmask = 0;
        pf.gmask = 0;
        pf.bmask = 0;
        pf.amask = 0;
    }

    public void setPixelFormat(int bitcount, int rmask, int gmask, int bmask, int amask) {
        pf.flags = DDPF_RGB;

        if (amask != 0) {
            pf.flags |= DDPF_ALPHAPIXELS;
        }

        if (bitcount == 0) {
            // Compute bit count from the masks.
            int total = rmask | gmask | bmask | amask;
            while (total != 0) {
                bitcount++;
                total >>= 1;
            }
        }

        //nvCheck(bitcount > 0 && bitcount <= 32);

        // Align to 8.
        if (bitcount <= 8) bitcount = 8;
        else if (bitcount <= 16) bitcount = 16;
        else if (bitcount <= 24) bitcount = 24;
        else bitcount = 32;

        pf.fourcc = 0; //findD3D9Format(bitcount, rmask, gmask, bmask, amask);
        pf.bitcount = bitcount;
        pf.rmask = rmask;
        pf.gmask = gmask;
        pf.bmask = bmask;
        pf.amask = amask;
    }

    public void setDX10Format(int format) {
        //pf.flags = 0;
        pf.fourcc = FOURCC_DX10;
        header10.dxgiFormat = format;
    }

    public void setNormalFlag(boolean b) {
        if (b) pf.flags |= DDPF_NORMAL;
        else pf.flags &= ~DDPF_NORMAL;
    }

    public boolean hasDX10Header() {
        return pf.fourcc == FOURCC_DX10;  // @@ This is according to AMD
        //return pf.flags == 0;             // @@ This is according to MS
    }
}
