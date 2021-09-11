package org.terifan.zulu.loaders;

import java.awt.Dimension;
import java.io.ByteArrayOutputStream;
import java.util.Stack;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.terifan.vecmath.Vec3f;
import org.terifan.io.Streams;
import org.terifan.zulu.Texture2D;
import org.terifan.zulu.Texture2D.TextureData;
import org.terifan.zeus.util.codecs.ImageDecoder;


public class VTFLoader
{
	private final static int TEXTUREFLAGS_POINTSAMPLE = 0x00000001;
	private final static int TEXTUREFLAGS_TRILINEAR = 0x00000002;
	private final static int TEXTUREFLAGS_CLAMPS = 0x00000004;
	private final static int TEXTUREFLAGS_CLAMPT = 0x00000008;
	private final static int TEXTUREFLAGS_ANISOTROPIC = 0x00000010;
	private final static int TEXTUREFLAGS_HINT_DXT5 = 0x00000020;
	private final static int TEXTUREFLAGS_NOCOMPRESS = 0x00000040;
	private final static int TEXTUREFLAGS_NORMAL = 0x00000080;
	private final static int TEXTUREFLAGS_NOMIP = 0x00000100;
	private final static int TEXTUREFLAGS_NOLOD = 0x00000200;
	private final static int TEXTUREFLAGS_MINMIP = 0x00000400;
	private final static int TEXTUREFLAGS_PROCEDURAL = 0x00000800;
	private final static int TEXTUREFLAGS_ONEBITALPHA = 0x00001000;
	private final static int TEXTUREFLAGS_EIGHTBITALPHA = 0x00002000;
	private final static int TEXTUREFLAGS_ENVMAP = 0x00004000;
	private final static int TEXTUREFLAGS_RENDERTARGET = 0x00008000;
	private final static int TEXTUREFLAGS_DEPTHRENDERTARGET = 0x00010000;
	private final static int TEXTUREFLAGS_NODEBUGOVERRIDE = 0x00020000;
	private final static int TEXTUREFLAGS_SINGLECOPY = 0x00040000;
	private final static int TEXTUREFLAGS_ONEOVERMIPLEVELINALPHA = 0x00080000;
	private final static int TEXTUREFLAGS_PREMULTCOLORBYONEOVERMIPLEVEL = 0x00100000;
	private final static int TEXTUREFLAGS_NORMALTODUDV = 0x00200000;
	private final static int TEXTUREFLAGS_ALPHATESTMIPGENERATION = 0x00400000;
	private final static int TEXTUREFLAGS_NODEPTHBUFFER = 0x00800000;
	private final static int TEXTUREFLAGS_NICEFILTERED = 0x01000000;
	private final static int TEXTUREFLAGS_CLAMPU = 0x02000000;


	private VTFLoader()
	{
	}


	public static Texture2D decode(Resource aResource) throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Streams.transfer(aResource.getStream(), baos);

		ByteBuffer in = ByteBuffer.wrap(baos.toByteArray()).order(ByteOrder.LITTLE_ENDIAN);

		byte[] header = new byte[4];
		in.get(header);
		if (!new String(header).equals("VTF\0"))
		{
			throw new IOException("Not a VTF file.");
		}
		if (in.getInt() != 7)
		{
			throw new IOException("Not a version 7 VTF file.");
		}

		in.getInt(); // minor version

		Vec3f reflectivity = new Vec3f();
		int headerSize = in.getInt();
		int width = 0xffff & in.getShort();
		int height = 0xffff & in.getShort();
		int flags = in.getInt();
		int numFrames = 0xffff & in.getShort();
		int startFrame = 0xffff & in.getShort();
		in.getInt(); // padding
		reflectivity.x = in.getFloat();
		reflectivity.z = in.getFloat();
		reflectivity.y = in.getFloat();
		in.getInt(); // padding
		double bumpScale = in.getFloat();
		int imageFormat = in.getInt();
		int numMipLevels = 0xff & in.get();
		int lowResImageFormat = in.getInt();
		int lowResWidth = 0xff & in.get();
		int lowResHeight = 0xff & in.get();
		int depth = 0xff & in.get();

		in.get(new byte[headerSize-64]); // ?????????
/*
		System.out.println("HeaderSize="+headerSize);
		System.out.println("Width="+width);
		System.out.println("Height="+height);
		System.out.print("Flags="+flags+" ( ");
		if ((flags & TEXTUREFLAGS_POINTSAMPLE) != 0) System.out.print("POINTSAMPLE ");
		if ((flags & TEXTUREFLAGS_TRILINEAR) != 0) System.out.print("TRILINEAR ");
		if ((flags & TEXTUREFLAGS_CLAMPS) != 0) System.out.print("CLAMPS ");
		if ((flags & TEXTUREFLAGS_CLAMPT) != 0) System.out.print("CLAMPT ");
		if ((flags & TEXTUREFLAGS_ANISOTROPIC) != 0) System.out.print("ANISOTROPIC ");
		if ((flags & TEXTUREFLAGS_HINT_DXT5) != 0) System.out.print("HINT_DXT5 ");
		if ((flags & TEXTUREFLAGS_NOCOMPRESS) != 0) System.out.print("NOCOMPRESS ");
		if ((flags & TEXTUREFLAGS_NORMAL) != 0) System.out.print("NORMAL ");
		if ((flags & TEXTUREFLAGS_NOMIP) != 0) System.out.print("NOMIP ");
		if ((flags & TEXTUREFLAGS_NOLOD) != 0) System.out.print("NOLOD ");
		if ((flags & TEXTUREFLAGS_MINMIP) != 0) System.out.print("MINMIP ");
		if ((flags & TEXTUREFLAGS_PROCEDURAL) != 0) System.out.print("PROCEDURAL ");
		if ((flags & TEXTUREFLAGS_ONEBITALPHA) != 0) System.out.print("ONEBITALPHA ");
		if ((flags & TEXTUREFLAGS_EIGHTBITALPHA) != 0) System.out.print("EIGHTBITALPHA ");
		if ((flags & TEXTUREFLAGS_ENVMAP) != 0) System.out.print("ENVMAP ");
		if ((flags & TEXTUREFLAGS_RENDERTARGET) != 0) System.out.print("RENDERTARGET ");
		if ((flags & TEXTUREFLAGS_DEPTHRENDERTARGET) != 0) System.out.print("DEPTHRENDERTARGET ");
		if ((flags & TEXTUREFLAGS_NODEBUGOVERRIDE) != 0) System.out.print("NODEBUGOVERRIDE ");
		if ((flags & TEXTUREFLAGS_SINGLECOPY) != 0) System.out.print("SINGLECOPY ");
		if ((flags & TEXTUREFLAGS_ONEOVERMIPLEVELINALPHA) != 0) System.out.print("ONEOVERMIPLEVELINALPHA ");
		if ((flags & TEXTUREFLAGS_PREMULTCOLORBYONEOVERMIPLEVEL) != 0) System.out.print("PREMULTCOLORBYONEOVERMIPLEVEL ");
		if ((flags & TEXTUREFLAGS_NORMALTODUDV) != 0) System.out.print("NORMALTODUDV ");
		if ((flags & TEXTUREFLAGS_ALPHATESTMIPGENERATION) != 0) System.out.print("ALPHATESTMIPGENERATION ");
		if ((flags & TEXTUREFLAGS_NODEPTHBUFFER) != 0) System.out.print("NODEPTHBUFFER ");
		if ((flags & TEXTUREFLAGS_NICEFILTERED) != 0) System.out.print("NICEFILTERED ");
		if ((flags & TEXTUREFLAGS_CLAMPU) != 0) System.out.print("CLAMPU ");
		System.out.println(")");
		System.out.println("NumFrames="+numFrames);
		System.out.println("StartFrame="+startFrame);
		System.out.println("Reflectivity="+reflectivity);
		System.out.println("BumpScale="+bumpScale);
		System.out.println("ImageFormat="+imageFormat);
		System.out.println("NumMipLevels="+numMipLevels);
		System.out.println("LowResImageFormat="+lowResImageFormat);
		System.out.println("LowResWidth="+lowResWidth);
		System.out.println("LowResHeight="+lowResHeight);
		System.out.println("Depth="+depth);
*/
		// lowres always DXT1
		in.get(new byte[lowResWidth * lowResHeight / 2]);

		Texture2D texture = new Texture2D(width, height);

		Stack<Dimension> dimensions = new Stack<Dimension>();

		for (int i = 0, w = width, h = height; i < numMipLevels; i++)
		{
			dimensions.push(new Dimension(Math.max(w,1), Math.max(h,1)));
			w /= 2;
			h /= 2;
		}

		for (int i = 0; i < numMipLevels; i++)
		{
			Dimension d = dimensions.pop();

			int [] raster;
			if (d.width < 4 || d.height < 4)
			{
				raster = new int[Math.max(4,d.width) * Math.max(4,d.height)];
			}
			else
			{
				raster = new int[d.width * d.height];
			}

			for (int j = 0; j < numFrames; j++)
			{
				switch (imageFormat)
				{
					case 14: // DXT1
						ImageDecoder.decodeDXT1(d.width, d.height, raster, in);
						break;
					case 15: // DXT3
						ImageDecoder.decodeDXT3(d.width, d.height, raster, in);
						break;
					case 16: // DXT5
						ImageDecoder.decodeDXT5(d.width, d.height, raster, in);
						break;
					case 4: // BGR888
						ImageDecoder.decodeBGR888(d.width, d.height, raster, in);
						break;
					case 25: // RGBA16161616F
						ImageDecoder.decodeRGBA16161616F(d.width, d.height, raster, in);
						break;
					default:
						throw new IOException("Unsupported image format: " + imageFormat);
				}
			}

			if (d.width < 4 || d.height < 4)
			{
				int [] dst = new int[d.width * d.height];
				for (int y = 0; y < d.height; y++)
				{
					System.arraycopy(raster, y * 4, dst, y * d.width, d.width);
				}
				raster = dst;
			}

			texture.maps[numMipLevels-i-1] = new TextureData(i, d.width, d.height, raster);
		}
		return texture;
	}
}