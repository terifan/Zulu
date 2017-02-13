package org.terifan.zulu;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import org.terifan.zulu.loaders.Resource;
import org.terifan.zulu.loaders.VTFLoader;


public class Texture2D
{
	private final static boolean DEBUG_MIPMAP = false;

	private final static int [][] GAUSSIAN_MINIFICATION_FILTER = new int[][]
	{
		{0, 0, 0,  5,  5, 0, 0,0},
		{0, 5,18, 32, 32,18, 5,0},
		{0,18,64, 88, 88,64,18,0},
		{5,32,88,100,100,88,32,5},
		{5,32,88,100,100,88,32,5},
		{0,18,64, 88, 88,64,18,0},
		{0, 5,18, 32, 32,18, 5,0},
		{0, 0, 0,  5,  5, 0, 0,0},
	};


	public TextureData [] maps;


	public Texture2D(int aWidth, int aHeight)
	{
		if (!checkSizePowerOf2(aWidth, aHeight))
		{
			throw new IllegalArgumentException("Size not power of 2: width: "+aWidth+", height: "+aHeight);
		}

		int numLevels = 1 + (int)Math.max(Math.log(aWidth)/Math.log(2), Math.log(aHeight)/Math.log(2));
		maps = new TextureData[numLevels];
		maps[0] = new TextureData(0, aWidth, aHeight, new int[aWidth*aHeight]);
	}


	public Texture2D(Resource aResource)
	{
		try
		{
			InputStream stream = aResource.getStream();

			if (stream == null)
			{
				throw new IllegalArgumentException("Resource not found: resource: " + aResource.getName());
			}

			BufferedImage tmp = null;
			
			if (aResource.getName().toLowerCase().endsWith(".vtf"))
			{
				Texture2D tex = VTFLoader.decode(aResource);
				maps = tex.maps;
				return;
			}
			else
			{
				tmp = ImageIO.read(stream);
			}

			if (tmp == null)
			{
				throw new IllegalArgumentException("Failed to load texture from resource: resource: " + aResource.getName());
			}

			int width = tmp.getWidth();
			int height = tmp.getHeight();

			if (!checkSizePowerOf2(width, height))
			{
				throw new IllegalArgumentException("Size not power of 2: width: "+width+", height: "+height);
			}

			int numLevels = 1 + (int)Math.max(Math.log(width)/Math.log(2), Math.log(height)/Math.log(2));
			maps = new TextureData[numLevels];
			maps[0] = new TextureData(0, width, height, new int[width*height]);

			tmp.getRGB(0, 0, width, height, maps[0].pixels, 0, width);

			buildMipMaps();
		}
		catch (IOException e)
		{
			throw new IllegalStateException(e);
		}
	}


	private static boolean checkSizePowerOf2(int aWidth, int aHeight)
	{
		return (aWidth & -aWidth) == aWidth && (aHeight & -aHeight) == aHeight;
	}


	public void buildMipMaps()
	{
		for (int level = 1; level < maps.length; level++)
		{
			int dstWidth = Math.max(maps[0].width >> level, 1);
			int dstHeight = Math.max(maps[0].height >> level, 1);

//System.out.println("creating mipmap "+level+" "+dstWidth+" "+dstHeight);

			TextureData dstTexture = maps[level];

			if (dstTexture == null)
			{
				maps[level] = dstTexture = new TextureData(level, dstWidth, dstHeight, new int[dstWidth*dstHeight]);
			}

			TextureData srcTexture = maps[level-1];
			int srcWidth = srcTexture.width;
			int srcHeight = srcTexture.height;
			int [] srcPixels = srcTexture.pixels;

			int [] dstPixels = dstTexture.pixels;

			int [][] filter = GAUSSIAN_MINIFICATION_FILTER;

			int filterWidth = filter[0].length;
			int filterHeight = filter.length;
			int fminx = -(filterWidth >> 1);
			int fminy = -(filterHeight >> 1);
			if ((filterWidth & 1) == 0) fminx++;
			if ((filterHeight & 1) == 0) fminy++;

			for (int y = 0, dstOffset = 0; y < srcHeight; y+=2)
			{
				for (int x = 0; x < srcWidth; x+=2, dstOffset++)
				{
					int r = 0, g = 0, b = 0, a = 0, s = 0;
	
					for (int fy = fminy, fyp = 0; fyp < filterHeight; fy++, fyp++)
					{
						int yoff = (y + fy) * srcWidth;
	
						for (int fx = fminx, fxp = 0; fxp < filterWidth; fx++, fxp++)
						{
							if (y + fy >= 0 && x + fx >= 0 && y + fy < srcHeight && x + fx < srcWidth)
							{
								int f = filter[fyp][fxp];
								if (f > 0)
								{
									int src = srcPixels[yoff + x + fx];
									a += f * ((src >> 24) & 255);
									r += f * ((src >> 16) & 255);
									g += f * ((src >>  8) & 255);
									b += f * ((src      ) & 255);
									s += f;
								}
							}
						}
					}
	
					a /= s;
					r /= s;
					g /= s;
					b /= s;

					dstPixels[dstOffset] = (a << 24) + (r << 16) + (g << 8) + b;
				}
			}
		}

		if (DEBUG_MIPMAP)
		{
			int [][] colors = {
				{255,24,5},
				{255,124,17},
				{255,223,5},
				{149,255,5},
				{36,255,17},
				{17,255,255},
				{17,184,255},
				{41,42,252},
				{149,0,250},
				{248,5,255},
				{255,255,255},
				{128,128,128},
				{64,64,64},
				{0,0,0}
			};

			for (int level = 0; level < maps.length; level++)
			{
				int c0 = colors[level][0];
				int c1 = colors[level][1];
				int c2 = colors[level][2];
				int [] p = maps[level].pixels;
				for (int j = 0; j < p.length; j++)
				{
					int c = p[j];
					int r = 255 & (c >> 16);
					int g = 255 & (c >>  8);
					int b = 255 & (c      );
					r = (int)(r + 0.5 * (c0 - r));
					g = (int)(g + 0.5 * (c1 - g));
					b = (int)(b + 0.5 * (c2 - b));
					p[j] = (c&0xff000000) | (r<<16) + (g<<8) + b;
				}
			}
		}

		/*for (int level = 0; level < maps.length; level++)
		{
			int w = maps[level].width;
			int h = maps[level].height;
			BufferedImage im = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
			im.setRGB(0, 0, w, h, maps[level].pixels, 0, w);
			try{ImageIO.write(im,"png",new File("c:/"+Integer.toString(level,16)+".png"));}catch(Exception e){}
		}*/
	}


	public static class TextureData
	{
		public final int [] pixels;
		public final int width;
		public final int height;
		public final int level;

		public TextureData(int aLevel, int aWidth, int aHeight, int [] aPixels)
		{
			level = aLevel;
			width = aWidth;
			height = aHeight;
			pixels = aPixels;
		}
	}
}