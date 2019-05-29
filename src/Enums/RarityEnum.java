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
	
	public static Color getColorByRarity(String rarityName) {
		if(rarityName == null) {
			return new Color(000, 51, 1); // Green (Uncommon) has no rarity tag
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
		Map<String, Color> rarities = new HashMap();
		//rarities.put("EFortRarity::Legendary", new Color(255,153,000)); //Impossible (T9)
		rarities.put("EFortRarity::Masterwork", new Color(255,000,000)); // Transcendent
		rarities.put("EFortRarity::Elegant", new Color(204,153,000)); //Mythic
		rarities.put("EFortRarity::Fine", new Color(92,34,000)); //Legendary
		rarities.put("EFortRarity::Quality", new Color(48,14,92)); //Epic
		rarities.put("EFortRarity::Sturdy", new Color(000,32,78)); //Rare
		rarities.put("EFortRarity::Handmade", new Color(53,57,60)); //Common
		
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
	private static Map<String, String> addRaritiyNames() {
		Map<String, String> raritiyNames = new HashMap();
		raritiyNames.put("EFortRarity::Quality", "Epic"); //Epic
		//raritiyNames.put("EFortRarity::Legendary", "Impossible (T9)"); //Impossible (T9)
		raritiyNames.put("EFortRarity::Masterwork", "Transcendent"); // Transcendent
		raritiyNames.put("EFortRarity::Elegant", "Mythic"); //Mythic
		raritiyNames.put("EFortRarity::Fine", "Legendary"); //Legendary
		raritiyNames.put("EFortRarity::Sturdy", "Rare"); //Rare
		raritiyNames.put("EFortRarity::Handmade", "Common"); //Gray
		
		//Fortnite V9.0 introduce new Enums for the same rarities
		raritiyNames.put("EFortRarity::Trancendent", "Transcendent");
		raritiyNames.put("EFortRarity::Mythic", "Mythic");
		raritiyNames.put("EFortRarity::Legendary", "Legendary");
		raritiyNames.put("EFortRarity::Epic", "Epic");
		raritiyNames.put("EFortRarity::Rare", "Rare");
		raritiyNames.put("EFortRarity::Common", "Common");
		
		//Green (Uncommon) has no rarity enum
		return raritiyNames;
	}
}
