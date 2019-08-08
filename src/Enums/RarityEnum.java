/**
 * 
 */
package Enums;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import res.Resources;

/**
 * @author FunGames
 *
 */
public class RarityEnum {
	private static final Map<String, Color> rarities = addRarities();
	private static final Map<String, String> raritiyNames = addRaritiyNames();
	private static final Map<String, Integer> rarityNums = addRarityNums();
	
	
	public static int getNumByRarity(String rarityName) {
		if(rarityName == null) {
			return 20; // Green (Uncommon) has no rarity tag
		} else {
			Integer r = rarityNums.get(rarityName);
			if(r !=  null) {
				return r;
			} else {
				return 100;
			}
		}
	}
	
	public static Color getColorByRarity(String rarityName) {
		if(rarityName == null) {
			return Color.decode("#61c038"); // Green (Uncommon) has no rarity tag
		} else {
			Color r = rarities.get(rarityName);
			if(r !=  null) {
				return r;
			} else {
				return new Color(255,255,255);
			}
		}
	}
	public static String getNameByEnum(String rarityEnum) {
		if(rarityEnum == null) {
			return "Uncommon";
		} else {
			String r = raritiyNames.get(rarityEnum);
			if(r !=  null) {
				return r;
			} else {
				return "Unknown";
			}
		}
	}
	
	public static BufferedImage getRarityBackground(String rarityName) {
		if(rarityName == null) {
			return Resources.getUncommonBackground();
		}
		switch(rarityName) {
		case "EFortRarity::Masterwork":
			//Looks the same as Transcendent
			return Resources.getMarvelBackground();
		case "EFortRarity::Elegant":
			return Resources.getMythicBackground();
		case "EFortRarity::Fine":
			return Resources.getLegendaryBackground();
		case "EFortRarity::Quality":
			return Resources.getEpicBackground();
		case "EFortRarity::Sturdy":
			return Resources.getRareBackground();
		case "EFortRarity::Handmade":
			return Resources.getCommonBackground();
		//Fortnite V9.0 introduce new Enums for the same rarities	
		case "EFortRarity::Trancendent":
			return Resources.getMarvelBackground();
		case "EFortRarity::Mythic":
			return Resources.getMythicBackground();
		case "EFortRarity::Legendary":
			return Resources.getLegendaryBackground();
		case "EFortRarity::Epic":
			return Resources.getEpicBackground();
		case "EFortRarity::Rare":
			return Resources.getRareBackground();
		case "EFortRarity::Common":
			return Resources.getCommonBackground();
		default:
			return Resources.getCommonBackground();
		}
	}
	
	public static BufferedImage getRarityVariantBackground(String rarityName) {
		if(rarityName == null) {
			return Resources.getUncommonVariantBackground();
		}
		switch(rarityName) {
		case "EFortRarity::Masterwork":
			//Looks the same as Transcendent
			return Resources.getMarvelVariantBackground();
		case "EFortRarity::Fine":
			return Resources.getLegendaryVariantBackground();
		case "EFortRarity::Quality":
			return Resources.getEpicVariantBackground();
		case "EFortRarity::Sturdy":
			return Resources.getRareVariantBackground();
		case "EFortRarity::Handmade":
			return Resources.getCommonVariantBackground();
		//Fortnite V9.0 introduce new Enums for the same rarities	
		case "EFortRarity::Trancendent":
			return Resources.getMarvelVariantBackground();
		case "EFortRarity::Legendary":
			return Resources.getLegendaryVariantBackground();
		case "EFortRarity::Epic":
			return Resources.getEpicVariantBackground();
		case "EFortRarity::Rare":
			return Resources.getRareVariantBackground();
		case "EFortRarity::Common":
			return Resources.getCommonVariantBackground();
		default:
			return Resources.getCommonVariantBackground();
		}
	}
	
	public static BufferedImage getRarityDaily(String rarityName) {
		if(rarityName == null) {
			return Resources.getUncommonBackgroundDaily();
		}
		switch(rarityName) {
		case "EFortRarity::Masterwork":
			//Looks the same as Transcendent
			return Resources.getMarvelBackgroundDaily();
		case "EFortRarity::Elegant":
			return Resources.getMythicBackgroundDaily();
		case "EFortRarity::Fine":
			return Resources.getLegendaryBackgroundDaily();
		case "EFortRarity::Quality":
			return Resources.getEpicBackgroundDaily();
		case "EFortRarity::Sturdy":
			return Resources.getRareBackgroundDaily();
		case "EFortRarity::Handmade":
			return Resources.getCommonBackgroundDaily();
		//Fortnite V9.0 introduce new Enums for the same rarities	
		case "EFortRarity::Trancendent":
			return Resources.getMarvelBackgroundDaily();
		case "EFortRarity::Mythic":
			return Resources.getMythicBackgroundDaily();
		case "EFortRarity::Legendary":
			return Resources.getLegendaryBackgroundDaily();
		case "EFortRarity::Epic":
			return Resources.getEpicBackgroundDaily();
		case "EFortRarity::Rare":
			return Resources.getRareBackgroundDaily();
		case "EFortRarity::Common":
			return Resources.getCommonBackgroundDaily();
		default:
			return Resources.getCommonBackgroundDaily();
		}
	}
	
	public static BufferedImage getRarityFeatured(String rarityName) {
		if(rarityName == null) {
			return Resources.getUncommonBackgroundFeatured();
		}
		switch(rarityName) {
		case "EFortRarity::Masterwork":
			//Looks the same as Transcendent
			return Resources.getMarvelBackgroundFeatured();
		case "EFortRarity::Elegant":
			return Resources.getMythicBackgroundFeatured();
		case "EFortRarity::Fine":
			return Resources.getLegendaryBackgroundFeatured();
		case "EFortRarity::Quality":
			return Resources.getEpicBackgroundFeatured();
		case "EFortRarity::Sturdy":
			return Resources.getRareBackgroundFeatured();
		case "EFortRarity::Handmade":
			return Resources.getCommonBackgroundFeatured();
		//Fortnite V9.0 introduce new Enums for the same rarities	
		case "EFortRarity::Trancendent":
			return Resources.getMarvelBackgroundFeatured();
		case "EFortRarity::Mythic":
			return Resources.getMythicBackgroundFeatured();
		case "EFortRarity::Legendary":
			return Resources.getLegendaryBackgroundFeatured();
		case "EFortRarity::Epic":
			return Resources.getEpicBackgroundFeatured();
		case "EFortRarity::Rare":
			return Resources.getRareBackgroundFeatured();
		case "EFortRarity::Common":
			return Resources.getCommonBackgroundFeatured();
		default:
			return Resources.getCommonBackgroundFeatured();
		}
	}
	
	private static Map<String, Color> addRarities() {
		Map<String, Color> rarities = new HashMap<>();
		//rarities.put("EFortRarity::Legendary", new Color(255,153,000)); //Impossible (T9)
		rarities.put("EFortRarity::Masterwork", new Color(255,000,000)); // Transcendent
		rarities.put("EFortRarity::Elegant", Color.decode("#ae9131")); //Mythic
		rarities.put("EFortRarity::Fine", Color.decode("#c66e3a")); //Legendary
		rarities.put("EFortRarity::Quality", Color.decode("#7733b4")); //Epic
		rarities.put("EFortRarity::Sturdy", Color.decode("#396ec4")); //Rare
		rarities.put("EFortRarity::Handmade", Color.decode("#838383")); //Common
		
		//Fortnite V9.0 introduce new Enums for the same rarities
		rarities.put("EFortRarity::Trancendent", rarities.get("EFortRarity::Masterwork"));
		rarities.put("EFortRarity::Mythic", rarities.get("EFortRarity::Elegant"));
		rarities.put("EFortRarity::Legendary", rarities.get("EFortRarity::Fine"));
		rarities.put("EFortRarity::Epic", rarities.get("EFortRarity::Quality"));
		rarities.put("EFortRarity::Rare", rarities.get("EFortRarity::Sturdy"));
		rarities.put("EFortRarity::Common", rarities.get("EFortRarity::Handmade"));
		//Green has no rarity enum
		return rarities;
	}
	
	public final static String toHexString(Color colour) throws NullPointerException {
		  String hexColour = Integer.toHexString(colour.getRGB() & 0xffffff);
		  if (hexColour.length() < 6) {
		    hexColour = "000000".substring(0, 6 - hexColour.length()) + hexColour;
		  }
		  return "#" + hexColour;
	}
	private static Map<String, String> addRaritiyNames() {
		Map<String, String> rarityNames = new HashMap<>();
		rarityNames.put("EFortRarity::Quality", "Epic"); //Epic
		//raritiyNames.put("EFortRarity::Legendary", "Impossible (T9)"); //Impossible (T9)
		rarityNames.put("EFortRarity::Masterwork", "Transcendent"); // Transcendent
		rarityNames.put("EFortRarity::Elegant", "Mythic"); //Mythic
		rarityNames.put("EFortRarity::Fine", "Legendary"); //Legendary
		rarityNames.put("EFortRarity::Sturdy", "Rare"); //Rare
		rarityNames.put("EFortRarity::Handmade", "Common"); //Gray
		
		//Fortnite V9.0 introduce new Enums for the same rarities
		rarityNames.put("EFortRarity::Trancendent", "Transcendent");
		rarityNames.put("EFortRarity::Mythic", "Mythic");
		rarityNames.put("EFortRarity::Legendary", "Legendary");
		rarityNames.put("EFortRarity::Epic", "Epic");
		rarityNames.put("EFortRarity::Rare", "Rare");
		rarityNames.put("EFortRarity::Common", "Common");
		
		//Green (Uncommon) has no rarity enum
		return rarityNames;
	}
	
	private static Map<String, Integer> addRarityNums() {
		Map<String, Integer> rarityNums = new HashMap<>();
		rarityNums.put("EFortRarity::Quality", 400); //Epic
		//raritiyNames.put("EFortRarity::Legendary", "Impossible (T9)"); //Impossible (T9)
		rarityNums.put("EFortRarity::Masterwork", 700000); // Transcendent
		rarityNums.put("EFortRarity::Elegant", 60000); //Mythic
		rarityNums.put("EFortRarity::Fine", 5000); //Legendary
		rarityNums.put("EFortRarity::Sturdy", 30); //Rare
		rarityNums.put("EFortRarity::Handmade", 1); //Gray
		
		//Fortnite V9.0 introduce new Enums for the same rarities
		rarityNums.put("EFortRarity::Trancendent", 700000);
		rarityNums.put("EFortRarity::Mythic", 60000);
		rarityNums.put("EFortRarity::Legendary", 5000);
		rarityNums.put("EFortRarity::Epic", 400);
		rarityNums.put("EFortRarity::Rare", 30);
		rarityNums.put("EFortRarity::Common", 1);
		
		return rarityNums;
	}
}
