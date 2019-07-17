/**
 * 
 */
package res;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

/**
 * @author FunGames
 *
 */
public class Resources {
	public static BufferedImage getVBucksIcon() {
		InputStream in = Resources.class.getResourceAsStream("vbucks.png");
		if(in != null) {
			try {
				BufferedImage vbucks = ImageIO.read(in);
				return vbucks;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	public static BufferedImage getCommonBackground() {
		InputStream in = Resources.class.getResourceAsStream("C512.png");
		if(in != null) {
			try {
				BufferedImage vbucks = ImageIO.read(in);
				return vbucks;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	public static BufferedImage getEpicBackground() {
		InputStream in = Resources.class.getResourceAsStream("E512.png");
		if(in != null) {
			try {
				BufferedImage vbucks = ImageIO.read(in);
				return vbucks;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	public static BufferedImage getImpossibleT9Background() {
		InputStream in = Resources.class.getResourceAsStream("I512.png");
		if(in != null) {
			try {
				BufferedImage vbucks = ImageIO.read(in);
				return vbucks;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	public static BufferedImage getLegendaryBackground() {
		InputStream in = Resources.class.getResourceAsStream("L512.png");
		if(in != null) {
			try {
				BufferedImage vbucks = ImageIO.read(in);
				return vbucks;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	public static BufferedImage getMythicBackground() {
		InputStream in = Resources.class.getResourceAsStream("M512.png");
		if(in != null) {
			try {
				BufferedImage vbucks = ImageIO.read(in);
				return vbucks;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	public static BufferedImage getMarvelBackground() {
		InputStream in = Resources.class.getResourceAsStream("Marvel512.png");
		if(in != null) {
			try {
				BufferedImage vbucks = ImageIO.read(in);
				return vbucks;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	public static BufferedImage getRareBackground() {
		InputStream in = Resources.class.getResourceAsStream("R512.png");
		if(in != null) {
			try {
				BufferedImage vbucks = ImageIO.read(in);
				return vbucks;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	public static BufferedImage getUncommonBackground() {
		InputStream in = Resources.class.getResourceAsStream("U512.png");
		if(in != null) {
			try {
				BufferedImage vbucks = ImageIO.read(in);
				return vbucks;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static BufferedImage getCommonVariantBackground() {
		InputStream in = Resources.class.getResourceAsStream("vC512.png");
		if(in != null) {
			try {
				BufferedImage vbucks = ImageIO.read(in);
				return vbucks;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	public static BufferedImage getEpicVariantBackground() {
		InputStream in = Resources.class.getResourceAsStream("vE512.png");
		if(in != null) {
			try {
				BufferedImage vbucks = ImageIO.read(in);
				return vbucks;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	public static BufferedImage getLegendaryVariantBackground() {
		InputStream in = Resources.class.getResourceAsStream("vL512.png");
		if(in != null) {
			try {
				BufferedImage vbucks = ImageIO.read(in);
				return vbucks;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	public static BufferedImage getMarvelVariantBackground() {
		InputStream in = Resources.class.getResourceAsStream("vMarvel512.png");
		if(in != null) {
			try {
				BufferedImage vbucks = ImageIO.read(in);
				return vbucks;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	public static BufferedImage getRareVariantBackground() {
		InputStream in = Resources.class.getResourceAsStream("vR512.png");
		if(in != null) {
			try {
				BufferedImage vbucks = ImageIO.read(in);
				return vbucks;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	public static BufferedImage getUncommonVariantBackground() {
		InputStream in = Resources.class.getResourceAsStream("vU512.png");
		if(in != null) {
			try {
				BufferedImage vbucks = ImageIO.read(in);
				return vbucks;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
	
	
	
	
	public static BufferedImage getCommonBackgroundDaily() {
		InputStream in = Resources.class.getResourceAsStream("CDail.png");
		if(in != null) {
			try {
				BufferedImage vbucks = ImageIO.read(in);
				return vbucks;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	public static BufferedImage getEpicBackgroundDaily() {
		InputStream in = Resources.class.getResourceAsStream("EDail.png");
		if(in != null) {
			try {
				BufferedImage vbucks = ImageIO.read(in);
				return vbucks;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	public static BufferedImage getLegendaryBackgroundDaily() {
		InputStream in = Resources.class.getResourceAsStream("LDail.png");
		if(in != null) {
			try {
				BufferedImage vbucks = ImageIO.read(in);
				return vbucks;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	public static BufferedImage getMythicBackgroundDaily() {
		InputStream in = Resources.class.getResourceAsStream("MDail.png");
		if(in != null) {
			try {
				BufferedImage vbucks = ImageIO.read(in);
				return vbucks;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	public static BufferedImage getMarvelBackgroundDaily() {
		InputStream in = Resources.class.getResourceAsStream("MarvelDail.png");
		if(in != null) {
			try {
				BufferedImage vbucks = ImageIO.read(in);
				return vbucks;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	public static BufferedImage getRareBackgroundDaily() {
		InputStream in = Resources.class.getResourceAsStream("RDail.png");
		if(in != null) {
			try {
				BufferedImage vbucks = ImageIO.read(in);
				return vbucks;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	public static BufferedImage getUncommonBackgroundDaily() {
		InputStream in = Resources.class.getResourceAsStream("UDail.png");
		if(in != null) {
			try {
				BufferedImage vbucks = ImageIO.read(in);
				return vbucks;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
	
	
	
	
	public static BufferedImage getCommonBackgroundFeatured() {
		InputStream in = Resources.class.getResourceAsStream("CFeat.png");
		if(in != null) {
			try {
				BufferedImage vbucks = ImageIO.read(in);
				return vbucks;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	public static BufferedImage getEpicBackgroundFeatured() {
		InputStream in = Resources.class.getResourceAsStream("EFeat.png");
		if(in != null) {
			try {
				BufferedImage vbucks = ImageIO.read(in);
				return vbucks;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	public static BufferedImage getLegendaryBackgroundFeatured() {
		InputStream in = Resources.class.getResourceAsStream("LFeat.png");
		if(in != null) {
			try {
				BufferedImage vbucks = ImageIO.read(in);
				return vbucks;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	public static BufferedImage getMythicBackgroundFeatured() {
		InputStream in = Resources.class.getResourceAsStream("MFeat.png");
		if(in != null) {
			try {
				BufferedImage vbucks = ImageIO.read(in);
				return vbucks;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	public static BufferedImage getMarvelBackgroundFeatured() {
		InputStream in = Resources.class.getResourceAsStream("MarvelFeat.png");
		if(in != null) {
			try {
				BufferedImage vbucks = ImageIO.read(in);
				return vbucks;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	public static BufferedImage getRareBackgroundFeatured() {
		InputStream in = Resources.class.getResourceAsStream("RFeat.png");
		if(in != null) {
			try {
				BufferedImage vbucks = ImageIO.read(in);
				return vbucks;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	public static BufferedImage getUncommonBackgroundFeatured() {
		InputStream in = Resources.class.getResourceAsStream("UFeat.png");
		if(in != null) {
			try {
				BufferedImage vbucks = ImageIO.read(in);
				return vbucks;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static BufferedImage getFallbackIcon() {
		InputStream in = Resources.class.getResourceAsStream("NoIcon.png");
		if(in != null) {
			try {
				BufferedImage vbucks = ImageIO.read(in);
				return vbucks;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	public static InputStream getBurbankInputStream() {
		return Resources.class.getResourceAsStream("BurbankBigCondensed-Black.ttf");
	}
	public static InputStream getNotoSansInputStream() {
		return Resources.class.getResourceAsStream("NotoSans-Regular.ttf");
	}
	public static InputStream getNotoSansBoldInputStream() {
		return Resources.class.getResourceAsStream("NotoSans-Bold.ttf");
	}
}
