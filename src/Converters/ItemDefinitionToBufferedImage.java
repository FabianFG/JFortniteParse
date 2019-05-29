/**
 * 
 */
package Converters;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import Enums.RarityEnum;
import UE4.GameFile;
import UE4.PakFileReader;
import UE4_Packages.AthenaItemDefinition;
import UE4_Packages.DisplayAssetPath;
import UE4_Packages.FortHeroType;
import UE4_Packages.FortWeaponMeleeItemDefinition;
import UE4_Packages.Package;
import UE4_Packages.ReadException;
import UE4_Packages.UTexture2D;
import res.Resources;

/**
 * @author FunGames
 *
 */
public class ItemDefinitionToBufferedImage {

	public static BufferedImage getImage(AthenaItemDefinition itemDefinition, Map<String, Integer> pakFileReaderIndices,
			PakFileReader[] loadedPaks, Map<String, GameFile> gameFiles, String mountPrefix)
			throws IOException, ReadException {
		BufferedImage icon = loadIcon(itemDefinition, pakFileReaderIndices, loadedPaks, gameFiles, mountPrefix);
		if (icon == null) {
			return null;
		}
		if (icon.getWidth() != 512 || icon.getHeight() != 512) {
			icon = scaleImage(icon, 512, 512);
		}
		int additionalHeight = 160;
		BufferedImage result = new BufferedImage(icon.getWidth() + 10, icon.getHeight() + 10,
				BufferedImage.TYPE_INT_ARGB);
		String rarity = itemDefinition.getRarity();
		BufferedImage rarityBackground = RarityEnum.getRarityBackground(rarity);
		Graphics2D g = result.createGraphics();
		g.drawImage(rarityBackground, 0, 0, null);
		g.drawImage(icon, 5, 5, null);

		Font burbank;
		g.setColor(Color.WHITE);
		try {
			burbank = Font.createFont(Font.TRUETYPE_FONT, Resources.getBurbankInputStream());
			// font = font.deriveFont(Font.BOLD, 50);
		} catch (FontFormatException | IOException e) {
			// TODO Auto-generated catch block
			burbank = new Font("SansSerif", Font.BOLD, 50);
			e.printStackTrace();
		}
		Font notoSans;
		try {
			notoSans = Font.createFont(Font.TRUETYPE_FONT, Resources.getNotoSansInputStream());
			// font = font.deriveFont(Font.BOLD, 50);
		} catch (FontFormatException | IOException e) {
			// TODO Auto-generated catch block
			notoSans = new Font("SansSerif", Font.BOLD, 50);
			e.printStackTrace();
		}

		g.setPaint(new Color(0, 0, 0, 100));
		g.fillRect(5, 5 + 512 - additionalHeight, 512, additionalHeight);
		g.setFont(burbank);
		/*
		 * if(RarityEnum.getNameByEnum(rarity) != null) {
		 * g.setColor(rarityColor.brighter().brighter().brighter());
		 * g.setFont(font.deriveFont(Font.BOLD, 25)); FontMetrics fm =
		 * g.getFontMetrics(); String rarityName = RarityEnum.getNameByEnum(rarity); int
		 * x = (result.getWidth() / 4 - fm.stringWidth(rarityName.toUpperCase()) / 2);
		 * int y = icon.getHeight() + 35; g.drawString(rarityName.toUpperCase(), x, y);
		 * } if(itemDefinition.getShortDescription() != null) {
		 * g.setColor(Color.WHITE.darker()); g.setFont(font.deriveFont(Font.BOLD, 25));
		 * FontMetrics fm = g.getFontMetrics(); int x = (result.getWidth() / 4 -
		 * fm.stringWidth(itemDefinition.getShortDescription().toUpperCase()) / 2) +
		 * (result.getWidth() / 2); int y = icon.getHeight() + 35; String rarityName =
		 * RarityEnum.getNameByEnum(rarity);
		 * g.drawString(itemDefinition.getShortDescription().toUpperCase(), x, y); }
		 */
		if (itemDefinition.getDisplayName() != null) {
			g.setColor(Color.WHITE);
			Map<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
			attributes.put(TextAttribute.TRACKING, 0.03);
			g.setFont(burbank.deriveFont(Font.PLAIN, 60).deriveFont(attributes));
			FontMetrics fm = g.getFontMetrics();
			int fontSize = 60;
			while (fm.stringWidth(itemDefinition.getDisplayName().toUpperCase()) > result.getWidth() - 10) {
				fontSize -= 1;
				g.setFont(burbank.deriveFont(Font.PLAIN, fontSize).deriveFont(attributes));
				fm = g.getFontMetrics();
			}
			int x = (result.getWidth() / 2 - fm.stringWidth(itemDefinition.getDisplayName().toUpperCase()) / 2);
			int y = icon.getHeight() - 95;
			g.drawString(itemDefinition.getDisplayName().toUpperCase(), x, y);
		}

		if (itemDefinition.getDescription() != null) {
			g.setColor(Color.LIGHT_GRAY);
			int fontSize = 25;
			g.setFont(notoSans.deriveFont(Font.PLAIN, fontSize));
			FontMetrics fm = g.getFontMetrics();
			int y = icon.getHeight() - 50;
			for (String line : itemDefinition.getDescription().split("\\r?\\n")) {
				fontSize = 25;
				g.setFont(notoSans.deriveFont(Font.PLAIN, fontSize));
				fm = g.getFontMetrics();
				while (fm.stringWidth(line) > result.getWidth()) {
					fontSize -= 1;
					g.setFont(notoSans.deriveFont(Font.PLAIN, fontSize));
					fm = g.getFontMetrics();
				}
				int x = (result.getWidth() / 2 - fm.stringWidth(line) / 2);
				g.drawString(line, x, y);
				y += 35;
			}

		}
		return result;

	}
	
	public static BufferedImage getIcon(AthenaItemDefinition itemDefinition,
			Map<String, Integer> pakFileReaderIndices, PakFileReader[] loadedPaks, Map<String, GameFile> gameFiles,
			String mountPrefix) throws IOException, ReadException {
		BufferedImage icon = loadIcon(itemDefinition, pakFileReaderIndices, loadedPaks, gameFiles, mountPrefix);
		if (icon == null) {
			return null;
		}
		if (icon.getWidth() != 512 || icon.getHeight() != 512) {
			icon = scaleImage(icon, 512, 512);
		}
		return icon;
	}

	public static BufferedImage getImageNoDesc(AthenaItemDefinition itemDefinition,
			Map<String, Integer> pakFileReaderIndices, PakFileReader[] loadedPaks, Map<String, GameFile> gameFiles,
			String mountPrefix) throws IOException, ReadException {
		BufferedImage icon = loadIcon(itemDefinition, pakFileReaderIndices, loadedPaks, gameFiles, mountPrefix);
		if (icon == null) {
			return null;
		}
		if (icon.getWidth() != 512 || icon.getHeight() != 512) {
			icon = scaleImage(icon, 512, 512);
		}
		int additionalHeight = 90;
		BufferedImage result = new BufferedImage(icon.getWidth() + 10, icon.getHeight() + 10,
				BufferedImage.TYPE_INT_ARGB);
		String rarity = itemDefinition.getRarity();
		BufferedImage rarityBackground = RarityEnum.getRarityBackground(rarity);
		Graphics2D g = result.createGraphics();
		g.drawImage(rarityBackground, 0, 0, null);
		g.drawImage(icon, 5, 5, null);

		Font burbank;
		g.setColor(Color.WHITE);
		try {
			burbank = Font.createFont(Font.TRUETYPE_FONT, Resources.getBurbankInputStream());
			// font = font.deriveFont(Font.BOLD, 50);
		} catch (FontFormatException | IOException e) {
			// TODO Auto-generated catch block
			burbank = new Font("SansSerif", Font.BOLD, 50);
			e.printStackTrace();
		}
		Font notoSans;
		try {
			notoSans = Font.createFont(Font.TRUETYPE_FONT, Resources.getNotoSansInputStream());
			// font = font.deriveFont(Font.BOLD, 50);
		} catch (FontFormatException | IOException e) {
			// TODO Auto-generated catch block
			notoSans = new Font("SansSerif", Font.BOLD, 50);
			e.printStackTrace();
		}

		g.setPaint(new Color(0, 0, 0, 100));
		g.fillRect(5, 5 + 512 - additionalHeight, 512, additionalHeight);
		g.setFont(burbank);
		/*
		 * if(RarityEnum.getNameByEnum(rarity) != null) {
		 * g.setColor(rarityColor.brighter().brighter().brighter());
		 * g.setFont(font.deriveFont(Font.BOLD, 25)); FontMetrics fm =
		 * g.getFontMetrics(); String rarityName = RarityEnum.getNameByEnum(rarity); int
		 * x = (result.getWidth() / 4 - fm.stringWidth(rarityName.toUpperCase()) / 2);
		 * int y = icon.getHeight() + 35; g.drawString(rarityName.toUpperCase(), x, y);
		 * } if(itemDefinition.getShortDescription() != null) {
		 * g.setColor(Color.WHITE.darker()); g.setFont(font.deriveFont(Font.BOLD, 25));
		 * FontMetrics fm = g.getFontMetrics(); int x = (result.getWidth() / 4 -
		 * fm.stringWidth(itemDefinition.getShortDescription().toUpperCase()) / 2) +
		 * (result.getWidth() / 2); int y = icon.getHeight() + 35; String rarityName =
		 * RarityEnum.getNameByEnum(rarity);
		 * g.drawString(itemDefinition.getShortDescription().toUpperCase(), x, y); }
		 */
		if (itemDefinition.getDisplayName() != null) {
			g.setColor(Color.WHITE);
			Map<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
			attributes.put(TextAttribute.TRACKING, 0.03);
			g.setFont(burbank.deriveFont(Font.PLAIN, 80).deriveFont(attributes));
			FontMetrics fm = g.getFontMetrics();
			int fontSize = 80;
			while (fm.stringWidth(itemDefinition.getDisplayName().toUpperCase()) > result.getWidth() - 10) {
				fontSize -= 1;
				g.setFont(burbank.deriveFont(Font.PLAIN, fontSize).deriveFont(attributes));
				fm = g.getFontMetrics();
			}
			int x = (result.getWidth() / 2 - fm.stringWidth(itemDefinition.getDisplayName().toUpperCase()) / 2);
			int y = icon.getHeight() - 15;
			g.drawString(itemDefinition.getDisplayName().toUpperCase(), x, y);
		}
		return result;
	}

	public static BufferedImage getAsFeaturedImage(BufferedImage icon, BufferedImage vbucksIcon, int price,
			AthenaItemDefinition itemDefinition) {
		
		icon = icon.getWidth() != 1024 || icon.getHeight() != 1024 ? scaleImage(icon, 1024, 1024) : icon;
		int additionalHeight = 200;
		int barHeight = 131;

		BufferedImage background = RarityEnum.getRarityFeatured(itemDefinition.getRarity());
		BufferedImage result = new BufferedImage(background.getWidth(), background.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = result.createGraphics();
		g.drawImage(background, 0, 0, null);
		g.drawImage(cutIcon(icon, background.getWidth() - 22), 11, 11, null);

		g.setPaint(new Color(0, 0, 0, 100));
		g.fillRect(11, 11 + icon.getHeight() - additionalHeight, background.getWidth() - 22, additionalHeight);

		Font burbank;
		g.setColor(Color.WHITE);
		try {
			burbank = Font.createFont(Font.TRUETYPE_FONT, Resources.getBurbankInputStream());
			// font = font.deriveFont(Font.BOLD, 50);
		} catch (FontFormatException | IOException e) {
			// TODO Auto-generated catch block
			burbank = new Font("SansSerif", Font.BOLD, 50);
			e.printStackTrace();
		}
		Font notoSans;
		try {
			notoSans = Font.createFont(Font.TRUETYPE_FONT, Resources.getNotoSansInputStream());
			// font = font.deriveFont(Font.BOLD, 50);
		} catch (FontFormatException | IOException e) {
			// TODO Auto-generated catch block
			notoSans = new Font("SansSerif", Font.BOLD, 50);
			e.printStackTrace();
		}
		Font notoSansBold;
		try {
			notoSansBold = Font.createFont(Font.TRUETYPE_FONT, Resources.getNotoSansBoldInputStream());
			// font = font.deriveFont(Font.BOLD, 50);
		} catch (FontFormatException | IOException e) {
			// TODO Auto-generated catch block
			notoSansBold = new Font("SansSerif", Font.BOLD, 50);
			e.printStackTrace();
		}

		if (itemDefinition.getDisplayName() != null) {
			g.setColor(Color.WHITE);
			Map<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
			attributes.put(TextAttribute.TRACKING, 0.03);
			g.setFont(burbank.deriveFont(Font.PLAIN, 80).deriveFont(attributes));
			FontMetrics fm = g.getFontMetrics();
			int fontSize = 80;
			while (fm.stringWidth(itemDefinition.getDisplayName().toUpperCase()) > result.getWidth() - 10) {
				fontSize -= 1;
				g.setFont(burbank.deriveFont(Font.PLAIN, fontSize).deriveFont(attributes));
				fm = g.getFontMetrics();
			}
			int x = (result.getWidth() / 2 - fm.stringWidth(itemDefinition.getDisplayName().toUpperCase()) / 2);
			int y = icon.getHeight() - 95;
			g.drawString(itemDefinition.getDisplayName().toUpperCase(), x, y);

		}

		if (itemDefinition.getShortDescription() != null) {
			g.setColor(Color.LIGHT_GRAY);
			Map<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
			attributes.put(TextAttribute.TRACKING, 0.03);
			g.setFont(notoSans.deriveFont(Font.PLAIN, 40).deriveFont(attributes));
			FontMetrics fm = g.getFontMetrics();
			int fontSize = 40;
			while (fm.stringWidth(itemDefinition.getShortDescription()) > result.getWidth() - 10) {
				fontSize -= 1;
				g.setFont(notoSans.deriveFont(Font.PLAIN, fontSize).deriveFont(attributes));
				fm = g.getFontMetrics();
			}
			int x = (result.getWidth() / 2 - fm.stringWidth(itemDefinition.getShortDescription()) / 2);
			int y = icon.getHeight() - 20;
			g.drawString(itemDefinition.getShortDescription(), x, y);
			
			
		}
		
		String priceS = printPrice(price);
		
		g.setColor(Color.WHITE);
		g.setFont(notoSansBold.deriveFont(Font.PLAIN, 60));
		FontMetrics fm = g.getFontMetrics();
		vbucksIcon = scaleImage(vbucksIcon, 75, 75);
		
		int vbucksX = (result.getWidth() / 2 - fm.stringWidth(priceS) / 2 - (vbucksIcon.getWidth() / 2));
		int vbucksY = result.getHeight() - 11 - (barHeight / 2) - (vbucksIcon.getHeight() / 2);
		g.drawImage(vbucksIcon, vbucksX, vbucksY, null);
		
		g.drawString(priceS, vbucksX + vbucksIcon.getWidth() + 12, vbucksY + fm.getHeight() - 20);

		return result;
	}
	
	public static BufferedImage getAsDailyImage(BufferedImage icon, BufferedImage vbucksIcon, int price,
			AthenaItemDefinition itemDefinition) {
		
		icon = icon.getWidth() != 512 || icon.getHeight() != 512 ? scaleImage(icon, 512, 512) : icon;
		int barHeight = 83;

		BufferedImage background = RarityEnum.getRarityDaily(itemDefinition.getRarity());
		BufferedImage result = new BufferedImage(background.getWidth(), background.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		
		
		
		
		
		
		int additionalHeight = 160;
		Graphics2D g = result.createGraphics();
		g.drawImage(background, 0, 0, null);
		g.drawImage(icon, 5, 5, null);

		Font burbank;
		g.setColor(Color.WHITE);
		try {
			burbank = Font.createFont(Font.TRUETYPE_FONT, Resources.getBurbankInputStream());
			// font = font.deriveFont(Font.BOLD, 50);
		} catch (FontFormatException | IOException e) {
			// TODO Auto-generated catch block
			burbank = new Font("SansSerif", Font.BOLD, 50);
			e.printStackTrace();
		}
		Font notoSans;
		try {
			notoSans = Font.createFont(Font.TRUETYPE_FONT, Resources.getNotoSansInputStream());
			// font = font.deriveFont(Font.BOLD, 50);
		} catch (FontFormatException | IOException e) {
			// TODO Auto-generated catch block
			notoSans = new Font("SansSerif", Font.BOLD, 50);
			e.printStackTrace();
		}
		
		Font notoSansBold;
		try {
			notoSansBold = Font.createFont(Font.TRUETYPE_FONT, Resources.getNotoSansBoldInputStream());
			// font = font.deriveFont(Font.BOLD, 50);
		} catch (FontFormatException | IOException e) {
			// TODO Auto-generated catch block
			notoSansBold = new Font("SansSerif", Font.BOLD, 50);
			e.printStackTrace();
		}
		g.setPaint(new Color(0, 0, 0, 100));
		g.fillRect(5, 5 + 512 - additionalHeight, 512, additionalHeight);
		g.setFont(burbank);
		/*
		 * if(RarityEnum.getNameByEnum(rarity) != null) {
		 * g.setColor(rarityColor.brighter().brighter().brighter());
		 * g.setFont(font.deriveFont(Font.BOLD, 25)); FontMetrics fm =
		 * g.getFontMetrics(); String rarityName = RarityEnum.getNameByEnum(rarity); int
		 * x = (result.getWidth() / 4 - fm.stringWidth(rarityName.toUpperCase()) / 2);
		 * int y = icon.getHeight() + 35; g.drawString(rarityName.toUpperCase(), x, y);
		 * } if(itemDefinition.getShortDescription() != null) {
		 * g.setColor(Color.WHITE.darker()); g.setFont(font.deriveFont(Font.BOLD, 25));
		 * FontMetrics fm = g.getFontMetrics(); int x = (result.getWidth() / 4 -
		 * fm.stringWidth(itemDefinition.getShortDescription().toUpperCase()) / 2) +
		 * (result.getWidth() / 2); int y = icon.getHeight() + 35; String rarityName =
		 * RarityEnum.getNameByEnum(rarity);
		 * g.drawString(itemDefinition.getShortDescription().toUpperCase(), x, y); }
		 */
		if (itemDefinition.getDisplayName() != null) {
			g.setColor(Color.WHITE);
			Map<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
			attributes.put(TextAttribute.TRACKING, 0.03);
			g.setFont(burbank.deriveFont(Font.PLAIN, 60).deriveFont(attributes));
			FontMetrics fm = g.getFontMetrics();
			int fontSize = 60;
			while (fm.stringWidth(itemDefinition.getDisplayName().toUpperCase()) > result.getWidth() - 10) {
				fontSize -= 1;
				g.setFont(burbank.deriveFont(Font.PLAIN, fontSize).deriveFont(attributes));
				fm = g.getFontMetrics();
			}
			int x = (result.getWidth() / 2 - fm.stringWidth(itemDefinition.getDisplayName().toUpperCase()) / 2);
			int y = icon.getHeight() - 85;
			g.drawString(itemDefinition.getDisplayName().toUpperCase(), x, y);
		}

		if (itemDefinition.getShortDescription() != null) {
			g.setColor(Color.LIGHT_GRAY);
			Map<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
			attributes.put(TextAttribute.TRACKING, 0.03);
			g.setFont(notoSans.deriveFont(Font.PLAIN, 40).deriveFont(attributes));
			FontMetrics fm = g.getFontMetrics();
			int fontSize = 40;
			while (fm.stringWidth(itemDefinition.getShortDescription()) > result.getWidth() - 10) {
				fontSize -= 1;
				g.setFont(notoSans.deriveFont(Font.PLAIN, fontSize).deriveFont(attributes));
				fm = g.getFontMetrics();
			}
			int x = (result.getWidth() / 2 - fm.stringWidth(itemDefinition.getShortDescription()) / 2);
			int y = icon.getHeight() - 20;
			g.drawString(itemDefinition.getShortDescription(), x, y);
			
			
		}
		
		g.setFont(notoSansBold.deriveFont(Font.PLAIN, 45));
		g.setColor(Color.WHITE);
		FontMetrics fm = g.getFontMetrics();
		vbucksIcon = scaleImage(vbucksIcon, 75, 75);
		
		String priceS = printPrice(price);
		
		int vbucksX = (result.getWidth() / 2 - fm.stringWidth(priceS) / 2 - (vbucksIcon.getWidth() / 2) - 20);
		int vbucksY = result.getHeight() - 11 - (barHeight / 2) - (vbucksIcon.getHeight() / 2) + 5;
		g.drawImage(vbucksIcon, vbucksX, vbucksY, null);
		
		g.drawString(priceS, vbucksX + vbucksIcon.getWidth() + 12, vbucksY + fm.getHeight() - 10);
		
		
		return result;
	}
	
	public static String printPrice(int price) {
		NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
		return numberFormat.format(price);
	}

	public static BufferedImage cutIcon(BufferedImage icon, int targetX) {
		int startX = (icon.getWidth() / 2) - (targetX / 2);
		return icon.getSubimage(startX, 0, targetX, icon.getHeight());
	}

	public static BufferedImage getAsShopImage(AthenaItemDefinition itemDefinition,
			Map<String, Integer> pakFileReaderIndices, PakFileReader[] loadedPaks, Map<String, GameFile> gameFiles,
			String mountPrefix, BufferedImage vbucksIcon, int price, BufferedImage iconOverride, boolean featured)
			throws IOException, ReadException {
		BufferedImage icon = iconOverride;
		if (icon == null) {
			icon = loadIcon(itemDefinition, pakFileReaderIndices, loadedPaks, gameFiles, mountPrefix);
			if (icon == null) {
				icon = Resources.getFallbackIcon();
			}
		}

		String rarityName = itemDefinition.getRarity();

		return featured ? getAsFeaturedImage(icon, vbucksIcon, price, itemDefinition) : getAsDailyImage(icon, vbucksIcon, price, itemDefinition);

	}

	public static BufferedImage scaleImage(BufferedImage i, int x, int y) {
		java.awt.Image scaledTmp = i.getScaledInstance(x, y, java.awt.Image.SCALE_SMOOTH);
		i = new BufferedImage(x, y, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = i.createGraphics();
		g2d.drawImage(scaledTmp, 0, 0, null);
		g2d.dispose();
		return i;
	}

	public static BufferedImage scaleImageByFactor(BufferedImage i, double factor) {
		int x = (int) (i.getWidth() * factor);
		int y = (int) (i.getHeight() * factor);
		java.awt.Image scaledTmp = i.getScaledInstance(x, y, java.awt.Image.SCALE_SMOOTH);
		i = new BufferedImage(x, y, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = i.createGraphics();
		g2d.drawImage(scaledTmp, 0, 0, null);
		g2d.dispose();
		return i;
	}

	public static BufferedImage loadIcon(AthenaItemDefinition itemDefinition, Map<String, Integer> pakFileReaderIndices,
			PakFileReader[] loadedPaks, Map<String, GameFile> gameFiles, String mountPrefix)
			throws ReadException, IOException {
		BufferedImage icon = null;
		boolean iconLoaded = false;
		// Load icon from SoftObjectProperty
		if (itemDefinition.hasIcons()) {
			String fileName = Package.toGameSpecificName(itemDefinition.getLargePreviewImage(), mountPrefix);
			Package largePreviewImage = Package.loadPackageByName(fileName, pakFileReaderIndices, loadedPaks,
					gameFiles);
			if (largePreviewImage != null) {
				if (largePreviewImage.getExports().size() > 0) {
					if (largePreviewImage.getExports().get(0) instanceof UTexture2D) {
						UTexture2D t = (UTexture2D) largePreviewImage.getExports().get(0);
						try {
							icon = Texture2DToBufferedImage.readTexture(t);
							iconLoaded = true;
						} catch (ReadException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		} else if (itemDefinition.usesHeroDefinition()) {
			String heroDefinitionfileName = Package.toGameSpecificName(itemDefinition.getHeroDefinitionPackage(),
					mountPrefix);
			Package heroDefinition = Package.loadPackageByName(heroDefinitionfileName, pakFileReaderIndices, loadedPaks,
					gameFiles);
			if (heroDefinition != null && heroDefinition.getExports().size() > 0
					&& heroDefinition.getExports().get(0) instanceof FortHeroType) {
				FortHeroType heroType = (FortHeroType) heroDefinition.getExports().get(0);
				String iconName = Package.toGameSpecificName(heroType.getIconName(), mountPrefix);
				Package largePreviewImage = Package.loadPackageByName(iconName, pakFileReaderIndices, loadedPaks,
						gameFiles);
				if (largePreviewImage != null) {
					if (largePreviewImage.getExports().size() > 0) {
						if (largePreviewImage.getExports().get(0) instanceof UTexture2D) {
							UTexture2D t = (UTexture2D) largePreviewImage.getExports().get(0);
							try {
								icon = Texture2DToBufferedImage.readTexture(t);
								iconLoaded = true;
							} catch (ReadException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
			}

		} else if (itemDefinition.usesWeaponDefinition()) {
			String weaponDefinitionfileName = Package.toGameSpecificName(itemDefinition.getWeaponDefinitionPackage(),
					mountPrefix);
			Package weaponDefinition = Package.loadPackageByName(weaponDefinitionfileName, pakFileReaderIndices,
					loadedPaks, gameFiles);
			if (weaponDefinition != null && weaponDefinition.getExports().size() > 0
					&& weaponDefinition.getExports().get(0) instanceof FortWeaponMeleeItemDefinition) {
				FortWeaponMeleeItemDefinition weaponItemDefinition = (FortWeaponMeleeItemDefinition) weaponDefinition
						.getExports().get(0);
				String iconName = Package.toGameSpecificName(weaponItemDefinition.getIconName(), mountPrefix);
				Package largePreviewImage = Package.loadPackageByName(iconName, pakFileReaderIndices, loadedPaks,
						gameFiles);
				if (largePreviewImage != null) {
					if (largePreviewImage.getExports().size() > 0) {
						if (largePreviewImage.getExports().get(0) instanceof UTexture2D) {
							UTexture2D t = (UTexture2D) largePreviewImage.getExports().get(0);
							try {
								icon = Texture2DToBufferedImage.readTexture(t);
								iconLoaded = true;
							} catch (ReadException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
			}

		} else if (itemDefinition.usesDisplayPath()) {
			String displayAssetName = Package.toGameSpecificName(itemDefinition.getDisplayAssetPath(), mountPrefix);
			Package displayAssetPath = Package.loadPackageByName(displayAssetName, pakFileReaderIndices, loadedPaks,
					gameFiles);
			if (displayAssetPath.getExports().size() > 0
					&& displayAssetPath.getExports().get(0) instanceof DisplayAssetPath) {
				DisplayAssetPath d = (DisplayAssetPath) displayAssetPath.getExports().get(0);
				String fileName = Package.toGameSpecificName(d.getDetailsImage(), mountPrefix);
				Package iconImage = Package.loadPackageByName(fileName, pakFileReaderIndices, loadedPaks, gameFiles);
				if (iconImage != null) {
					if (iconImage.getExports().size() > 0) {
						if (iconImage.getExports().get(0) instanceof UTexture2D) {
							UTexture2D t = (UTexture2D) iconImage.getExports().get(0);
							try {
								icon = Texture2DToBufferedImage.readTexture(t);
								iconLoaded = true;
							} catch (ReadException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
			}
		} else {
			icon = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);
			iconLoaded = false;
		}
		if (icon == null) {
			icon = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);
			iconLoaded = false;
		}
		if (iconLoaded) {
			// System.out.println("Failed to read icon for item definition");
			// TODO not really sure whether to keep it like this
			return icon;
		} else {
			return null;
		}
	}

}
