/**
 * 
 */
package Converters;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import Enums.RarityEnum;
import UE4_Assets.AthenaItemDefinition;
import UE4_Assets.CosmeticVariantContainer;
import UE4_Assets.DisplayAssetPath;
import UE4_Assets.FGameplayTagContainer;
import UE4_Assets.FPropertyTag;
import UE4_Assets.FPropertyTagType;
import UE4_Assets.FPropertyTagType.ArrayProperty;
import UE4_Assets.FPropertyTagType.ObjectProperty;
import UE4_Assets.FPropertyTagType.TextProperty;
import UE4_Assets.FStructFallback;
import UE4_Assets.FText;
import UE4_Assets.FortCosmeticVariant;
import UE4_Assets.FortHeroType;
import UE4_Assets.FortWeaponMeleeItemDefinition;
import UE4_Assets.Package;
import UE4_Assets.ReadException;
import UE4_Assets.UDataTable;
import UE4_Assets.UObject;
import UE4_Assets.UScriptArray;
import UE4_Assets.UScriptStruct;
import UE4_Assets.UTexture2D;
import UE4_PakFile.GameFile;
import UE4_PakFile.PakFileReader;
import res.Resources;

/**
 * @author FunGames
 *
 */
public class ItemDefinitionToBufferedImage {

	public static Map<String, FText> sets = new ConcurrentHashMap<>();
	public static Map<String, BufferedImage> userFacingFlags = new ConcurrentHashMap<>();

	public static final String partOfSetTemplateDEFAULT = "Part of the <SetName>{0}</> set.";
	
	private static boolean firstRun = true;

	public static ItemDefinitionContainer createContainer(AthenaItemDefinition itemDefinition,
			Map<String, Integer> pakFileReaderIndices, PakFileReader[] loadedPaks, Map<String, GameFile> gameFiles,
			String mountPrefix, Optional<BufferedImage> overrideIcon, boolean loadVariants)
			throws ReadException, IOException {
		if(firstRun) {
			firstRun = false;
			loadSets(pakFileReaderIndices, loadedPaks, gameFiles);
			loadUserFacingFlags(pakFileReaderIndices, loadedPaks, gameFiles, mountPrefix);	
		}
		BufferedImage icon = overrideIcon
				.orElse(loadIcon(itemDefinition, pakFileReaderIndices, loadedPaks, gameFiles, mountPrefix));

		// Prod Use: Deactivate loadVariants
		// loadVariants = false;

		if (loadVariants) {
			itemDefinition.getVariants().forEach(variants -> {
				loadVariantIcons(pakFileReaderIndices, loadedPaks, gameFiles, mountPrefix, variants);
			});
			if (itemDefinition.getVariants().stream().anyMatch(variant -> {
				return variant.getVariants().stream().anyMatch(vContainer -> {
					return vContainer.getPreviewIcon() != null;
				});
			})) {
				itemDefinition.setVariantsLoaded(true);
			}
		}
		loadSets(pakFileReaderIndices, loadedPaks, gameFiles);
		loadUserFacingFlags(pakFileReaderIndices, loadedPaks, gameFiles, mountPrefix);
		return new ItemDefinitionContainer(icon, itemDefinition, sets);
	}

	public static void loadVariantIcons(Map<String, Integer> pakFileReaderIndices, PakFileReader[] loadedPaks,
			Map<String, GameFile> gameFiles, String mountPrefix, FortCosmeticVariant variants) {
		variants.getVariants().forEach(variant -> {
			String gameFilePath = Package.toGameSpecificName(variant.getPreviewImage(), mountPrefix);
			try {
				Package iconP = Package.loadPackageByName(gameFilePath, pakFileReaderIndices, loadedPaks, gameFiles);
				if (iconP == null)
					return;
				for (Object export : iconP.getExports()) {
					if (export instanceof UTexture2D) {
						BufferedImage icon = Texture2DToBufferedImage.readTexture((UTexture2D) export);
						if (icon != null) {
							variant.setPreviewIcon(icon);
						}
					}
				}
			} catch (ReadException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}
	
	public static void loadSets(Map<String, Integer> pakFileReaderIndices, PakFileReader[] loadedPaks,
			Map<String, GameFile> gameFiles) {
		if (!sets.isEmpty()) {
			return;
		}
		String assetPath = "FortniteGame/Content/Athena/Items/Cosmetics/Metadata/CosmeticSets.uasset";
		try {
			Package setsPackage = Package.loadPackageByName(assetPath, pakFileReaderIndices, loadedPaks, gameFiles);
			if (setsPackage != null) {

				for (Object export : setsPackage.getExports()) {
					if (export instanceof UDataTable) {
						Map<String, FText> setMap = new HashMap<>();
						UDataTable table = (UDataTable) export;
						Map<String, UObject> entries = table.getRows();
						for (Entry<String, UObject> entry : entries.entrySet()) {
							String setName = entry.getKey();
							UObject setObject = entry.getValue();
							FPropertyTagType tag = setObject.getPropertyByName("DisplayName");
							if (tag != null) {
								if (tag instanceof FPropertyTagType.TextProperty) {
									FPropertyTagType.TextProperty text = (TextProperty) tag;
									FText ftextObject = text.getText();
									setMap.put(setName, ftextObject);
								}
							}
						}
						sets = setMap;
					}
				}
			}
		} catch (ReadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	@SuppressWarnings("unused")
	private static void loadUserFacingFlags(Map<String, Integer> pakFileReaderIndices, PakFileReader[] loadedPaks,
			Map<String, GameFile> gameFiles, String mountPrefix) {
		if (!userFacingFlags.isEmpty()) {
			return;
		}
		String assetPath = "FortniteGame/Content/Items/ItemCategories.uasset";
		try {
			Package itemsPackage = Package.loadPackageByName(assetPath, pakFileReaderIndices, loadedPaks, gameFiles);
			if (itemsPackage != null) {
				for (Object export : itemsPackage.getExports()) {
					if (export instanceof UObject) {
						UObject uObject = (UObject) export;
						FPropertyTagType tertiaryCategories = uObject.getPropertyByName("TertiaryCategories");
						if (tertiaryCategories instanceof FPropertyTagType.ArrayProperty) {
							FPropertyTagType.ArrayProperty categories = (ArrayProperty) tertiaryCategories;
							UScriptArray array = categories.getNumber();
							for (FPropertyTagType entry : array.getData()) {
								if (entry instanceof FPropertyTagType.StructProperty) {
									Object structEntry = ((FPropertyTagType.StructProperty) entry).getStruct()
											.getStructType();
									if (structEntry instanceof FStructFallback) {
										// This contains the data we actually need
										FStructFallback fallback = (FStructFallback) structEntry;

										// TagContainer: Contains the tags needed, e.g.
										// Cosmetics.UserFacingFlags.HasVariants
										List<String> tags = new ArrayList<>();
										Optional<FPropertyTag> tagContainer = fallback
												.getPropertyByName("TagContainer");
										tagContainer.ifPresent(tag -> {
											if (tag.getTag() instanceof FPropertyTagType.StructProperty) {
												if (((FPropertyTagType.StructProperty) tag.getTag()).getStruct()
														.getStructType() instanceof FGameplayTagContainer) {
													FGameplayTagContainer tagContainers = (FGameplayTagContainer) ((FPropertyTagType.StructProperty) tag
															.getTag()).getStruct().getStructType();
													tags.addAll(tagContainers.getGameplayTags());
												}
											}
										});

										// Only proceed if we have user facing flags
										if (tags.stream().filter(tag -> {
											return tag.startsWith("Cosmetics.UserFacingFlags");
										}).findFirst().isPresent()) {
											// CategoryName: Contains the tags FText
											Optional<FPropertyTag> nameTag = fallback.getPropertyByName("CategoryName");
											FText name = null;
											if (nameTag.isPresent()) {
												FPropertyTag tag = nameTag.get();
												if (tag.getTag() instanceof FPropertyTagType.TextProperty) {
													name = ((FPropertyTagType.TextProperty) tag.getTag()).getText();
												}
											}

											// CategoryBrush: Contains the tags resources such as icons etc.
											Optional<FPropertyTag> brushTag = fallback
													.getPropertyByName("CategoryBrush");
											String resourceObject = null;
											if (brushTag.isPresent()) {
												FPropertyTag tag = brushTag.get();
												if (tag.getTag() instanceof FPropertyTagType.StructProperty) {
													UScriptStruct brushStruct = ((FPropertyTagType.StructProperty) tag
															.getTag()).getStruct();
													if (brushStruct.getStructType() instanceof FStructFallback) {
														FStructFallback brushFallback = (FStructFallback) brushStruct
																.getStructType();
														FPropertyTag resourceTag = brushFallback
																.getPropertyByName("Brush_XL")
																.orElse(brushFallback.getPropertyByName("Brush_L")
																		.orElse(brushFallback
																				.getPropertyByName("Brush_M")
																				.orElse(brushFallback
																						.getPropertyByName("Brush_S")
																						.orElse(brushFallback
																								.getPropertyByName(
																										"Brush_XS")
																								.orElse(brushFallback
																										.getPropertyByName(
																												"Brush_XXS")
																										.orElse(null))))));
														if (resourceTag != null) {
															if (resourceTag
																	.getTag() instanceof FPropertyTagType.StructProperty
																	&& ((FPropertyTagType.StructProperty) resourceTag
																			.getTag()).getStruct()
																					.getStructType() instanceof FStructFallback) {
																FStructFallback slateBrush = (FStructFallback) ((FPropertyTagType.StructProperty) resourceTag
																		.getTag()).getStruct().getStructType();
																Optional<FPropertyTag> resourceObjectTag = slateBrush
																		.getPropertyByName("ResourceObject");
																if (resourceObjectTag.isPresent() && resourceObjectTag
																		.get()
																		.getTag() instanceof FPropertyTagType.ObjectProperty) {
																	FPropertyTagType.ObjectProperty object = (ObjectProperty) resourceObjectTag
																			.get().getTag();
																	Optional<String> packagePath = object.getStruct()
																			.getPackagePath(
																					itemsPackage.getImportMap());
																	packagePath.ifPresent(iconPath -> {
																		String iconFileName = Package
																				.toGameSpecificName(iconPath,
																						mountPrefix);
																		try {
																			Package iconPackage = Package
																					.loadPackageByName(iconFileName,
																							pakFileReaderIndices,
																							loadedPaks, gameFiles);
																			for (Object iconExport : iconPackage
																					.getExports()) {
																				if (iconExport instanceof UTexture2D) {
																					BufferedImage icon = Texture2DToBufferedImage
																							.readTexture(
																									(UTexture2D) iconExport);
																					tags.stream()
																							.filter(userFacingFlag -> {
																								return userFacingFlag
																										.startsWith(
																												"Cosmetics.UserFacingFlags");
																							})
																							.forEach(userFacingFlag -> {
																								userFacingFlags.put(
																										userFacingFlag,
																										icon);
																							});
																				}
																			}
																		} catch (ReadException e) {
																			// TODO Auto-generated catch block
																			e.printStackTrace();
																		} catch (IOException e) {
																			// TODO Auto-generated catch block
																			e.printStackTrace();
																		}

																	});
																}
															}
														}
													}
												}
											}
										}

									}
								}
							}
						}
					}
				}
			}
		} catch (ReadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static BufferedImage getImageNoVariants(ItemDefinitionContainer container,
			Optional<String> partOfSetTemplateOverride) {
		BufferedImage icon = container.getIcon();
		AthenaItemDefinition itemDefinition = container.getItemDefinition();
		if (icon == null) {
			icon = Resources.getFallbackIcon();
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
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
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
			String[] lines = itemDefinition.getDescription().split("\\r?\\n");
			if (lines.length > 1 && container.getSetNameFText().isPresent()) {
				lines[1] = container.getSetName();
			} else if (lines.length == 1 && container.getSetNameFText().isPresent()) {
				String baseLine = lines[0];
				lines = new String[2];
				lines[0] = baseLine;
				String setText = partOfSetTemplateOverride.orElse(partOfSetTemplateDEFAULT);
				setText = setText.replace("<SetName>{0}</>", container.getSetName());
				lines[1] = setText;
			}
			for (String line : lines) {
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
		itemDefinition.getUserFacingTag().ifPresent(userFacingTag -> {
			if (!userFacingFlags.containsKey(userFacingTag))
				return;
			BufferedImage flagIcon = userFacingFlags.get(userFacingTag);
			if (flagIcon == null)
				return;
			if (flagIcon.getWidth() != 64 && flagIcon.getHeight() != 64)
				flagIcon = scaleImage(flagIcon, 64, 64);
			g.drawImage(flagIcon, 10, 10, null);
		});
		return result;
	}

	public static BufferedImage getImage(ItemDefinitionContainer container, Optional<String> partOfSetTemplateOverride)
			throws IOException, ReadException {
		return container.getItemDefinition().isVariantsLoaded()
				? getImageWithVariants(container, partOfSetTemplateOverride)
				: getImageNoVariants(container, partOfSetTemplateOverride);
	}

	static int totalX = 500;
	static int perRow = 7;
	static int spaceBetween = 5;
	static int vSize = 121;
	static int totalY = 350;
	static int defaultBeginX = 11;

	/**
	 * @param container
	 * @param partOfSetTemplateOverride
	 * @return
	 */
	@SuppressWarnings("unused")
	public static BufferedImage getImageWithVariants(ItemDefinitionContainer container,
			Optional<String> partOfSetTemplateOverride) {
		BufferedImage icon = container.getIcon();
		AthenaItemDefinition itemDefinition = container.getItemDefinition();
		if (icon == null) {
			icon = Resources.getFallbackIcon();
		}
		int iconS = 180;
		if (icon.getWidth() != iconS || icon.getHeight() != iconS) {
			icon = scaleImage(icon, iconS, iconS);
		}

		String rarity = itemDefinition.getRarity();
		BufferedImage rarityBackground = RarityEnum.getRarityVariantBackground(rarity);
		BufferedImage result = new BufferedImage(rarityBackground.getWidth(), rarityBackground.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = result.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.drawImage(rarityBackground, 0, 0, null);

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

		// Variants

		for (int i = 0; i < itemDefinition.getVariants().size(); i++) {
			if (itemDefinition.getVariants().get(i).getVariantChannelTag().equals("Cosmetics.Variant.Channel.Pattern")
					|| itemDefinition.getVariants().get(i).getVariantChannelTag()
							.equals("Cosmetics.Variant.Channel.Numeric") || StringUtils.containsIgnoreCase(itemDefinition.getVariants().get(i).getVariantChannelTag(), "PATTERN") || StringUtils.containsIgnoreCase(itemDefinition.getVariants().get(i).getVariantChannelTag(), "NUMBER")) {
				itemDefinition.getVariants().remove(i);
				i--;
			}
		}

		ToIntFunction<FortCosmeticVariant> intFunction = (num) -> num.getVariants().size();
		int totalVarCount = itemDefinition.getVariants().stream().collect(Collectors.summingInt(intFunction));
		int numChannels = itemDefinition.getVariants().size();
		if (numChannels > 2)
			numChannels = 2;
		int cY = 35;
		int beginX = defaultBeginX;

		g.setFont(burbank.deriveFont(25f));
		ToIntFunction<FortCosmeticVariant> rowCount = (num) -> {
			int varCount = num.getVariants().size();
			if (varCount < perRow)
				return 1;
			while (varCount % perRow != 0)
				varCount++;
			return varCount / perRow;

		};
		int totalRows = itemDefinition.getVariants().stream().collect(Collectors.summingInt(rowCount));
		int maxVarSize = (totalY - (numChannels * (g.getFontMetrics().getHeight()) - ((numChannels - 1) * 10)))
				/ totalRows;

		for (int i = 0; i < numChannels; i++) {
			FortCosmeticVariant var = itemDefinition.getVariants().get(i);
			int varCount = var.getVariants().size();
			g.setFont(burbank.deriveFont(25f));
			g.setPaint(Color.WHITE);
			g.drawString(var.getVariantChannelName().getString(), beginX, cY + 20);
			cY += g.getFontMetrics().getHeight();
			int cX = beginX;
			int varSize = maxVarSize;
			int perRowCount = var.getVariants().size() > perRow ? perRow : var.getVariants().size();
			if (varSize * perRowCount + (perRowCount - 1) * spaceBetween > totalX)
				varSize = (totalX - (perRowCount - 1) * spaceBetween) / perRowCount;
			for (int j = 0; j < var.getVariants().size(); j++) {
				CosmeticVariantContainer varContainer = var.getVariants().get(j);
				BufferedImage varIcon = varContainer.getPreviewIcon();
				if (varIcon == null)
					continue;
				if (varIcon.getWidth() != varSize || varIcon.getHeight() != varSize)
					varIcon = scaleImage(varIcon, varSize, varSize);
				if (cX + varIcon.getWidth() > beginX + totalX) {
					cX = beginX;
					cY += varIcon.getHeight() + spaceBetween;
				}
				g.setPaint(new Color(255, 255, 255, 70));
				g.fillRect(cX, cY, varSize, varSize);
				g.drawImage(varIcon, cX, cY, null);
				g.setPaint(new Color(0, 0, 0, 100));
				int rectHeight = (varIcon.getHeight() / 4);
				g.fillRect(cX, cY + (varIcon.getHeight() - rectHeight), varSize, rectHeight);

				g.setPaint(Color.WHITE);
				g.setFont(burbank.deriveFont(rectHeight / 1.2f));
				drawCenteredString(g, varContainer.getVariantName().getString(), new Rectangle(cX,
						cY + (varIcon.getHeight() - (varIcon.getHeight() / 4)), varSize, (varIcon.getHeight() / 4)),
						g.getFont());
				cX += varIcon.getWidth() + spaceBetween;
				if (cX > totalX) {
					cX = beginX;
					if (j + 1 < var.getVariants().size())
						cY += varIcon.getHeight() + spaceBetween;
				}
			}
			cY += varSize + 10;

		}

		g.drawImage(icon, result.getWidth() - 5 - iconS, result.getHeight() - 5 - iconS, null);

		if (itemDefinition.getDisplayName() != null) {
			g.setColor(Color.WHITE);
			g.setFont(burbank.deriveFont(Font.PLAIN, 50));
			FontMetrics fm = g.getFontMetrics();
			int fontSize = 50;
			while (fm.stringWidth(itemDefinition.getDisplayName().toUpperCase()) > result.getWidth() - iconS - 40) {
				fontSize -= 1;
				g.setFont(burbank.deriveFont(Font.PLAIN, fontSize));
				fm = g.getFontMetrics();
			}
			int x = result.getWidth() - 40 - iconS - fm.stringWidth(itemDefinition.getDisplayName());
			int y = result.getHeight() - 52;
			g.drawString(itemDefinition.getDisplayName().toUpperCase(), x, y);
		}
		if (itemDefinition.getDescription() != null) {
			g.setColor(Color.WHITE);
			int fontSize = 15;
			g.setFont(notoSans.deriveFont(Font.PLAIN, fontSize));
			FontMetrics fm = g.getFontMetrics();
			int y = result.getHeight() - 30;
			String[] lines = itemDefinition.getDescription().split("\\r?\\n");
			if (lines.length > 1 && container.getSetNameFText().isPresent()) {
				lines[1] = container.getSetName();
			} else if (lines.length == 1 && container.getSetNameFText().isPresent()) {
				String baseLine = lines[0];
				lines = new String[2];
				lines[0] = baseLine;
				String setText = partOfSetTemplateOverride.orElse(partOfSetTemplateDEFAULT);
				setText = setText.replace("<SetName>{0}</>", container.getSetName());
				lines[1] = setText;
			}
			if (lines.length == 1)
				y += 7;
			for (String line : lines) {
				fontSize = 15;
				g.setFont(notoSans.deriveFont(Font.PLAIN, fontSize));
				fm = g.getFontMetrics();
				while (fm.stringWidth(line) > result.getWidth() - iconS - 30) {
					fontSize -= 1;
					g.setFont(notoSans.deriveFont(Font.PLAIN, fontSize));
					fm = g.getFontMetrics();
				}
				int x = result.getWidth() - 30 - iconS - fm.stringWidth(line);
				g.drawString(line, x, y);
				y += 18;
			}

		}

		return /* getImageNoVariants(container, partOfSetTemplateOverride) */result;
	}

	/**
	 * Draw a String centered in the middle of a Rectangle.
	 *
	 * @param g    The Graphics instance.
	 * @param text The String to draw.
	 * @param rect The Rectangle to center the text in.
	 */
	private static void drawCenteredString(Graphics g, String text, Rectangle rect, Font font) {
		// Get the FontMetrics
		FontMetrics metrics = g.getFontMetrics(font);
		// Determine the X coordinate for the text
		int fontSize = font.getSize();
		while (g.getFontMetrics(font.deriveFont((float) fontSize)).stringWidth(text) > rect.width) {
			fontSize--;
		}
		font = font.deriveFont((float) fontSize);
		metrics = g.getFontMetrics(font);
		int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
		// Determine the Y coordinate for the text (note we add the ascent, as in java
		// 2d 0 is top of the screen)
		int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
		// Set the font
		g.setFont(font);
		// Draw the String
		g.drawString(text, x, y);
	}

	public static BufferedImage getIcon(ItemDefinitionContainer container) throws IOException, ReadException {
		BufferedImage icon = container.getIcon();
		if (icon == null) {
			return null;
		}
		if (icon.getWidth() != 512 || icon.getHeight() != 512) {
			icon = scaleImage(icon, 512, 512);
		}
		return icon;
	}

	@SuppressWarnings("unused")
	public static BufferedImage getImageNoDesc(ItemDefinitionContainer container) throws IOException, ReadException {
		BufferedImage icon = container.getIcon();
		AthenaItemDefinition itemDefinition = container.getItemDefinition();
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
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
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

	public static BufferedImage getAsFeaturedImage(ItemDefinitionContainer container, BufferedImage vbucksIcon,
			int price) {
		BufferedImage icon = container.getIcon();
		AthenaItemDefinition itemDefinition = container.getItemDefinition();
		icon = icon.getWidth() != 1024 || icon.getHeight() != 1024 ? scaleImage(icon, 1024, 1024) : icon;
		int additionalHeight = 200;
		int barHeight = 131;

		BufferedImage background = RarityEnum.getRarityFeatured(itemDefinition.getRarity());
		BufferedImage result = new BufferedImage(background.getWidth(), background.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = result.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
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

		int addImageX = (int) (result.getWidth() * 0.28);
		int additionalDrawPosX = result.getWidth() - 11 - addImageX;
		int additionalDrawPosY = 11;
		for (BufferedImage additionalIcon : container.getAdditionalIcons()) {
			int addImageY = additionalIcon.getHeight() / additionalIcon.getWidth() * addImageX;
			additionalIcon = scaleImage(additionalIcon, addImageX, addImageY);
			g.drawImage(additionalIcon, additionalDrawPosX, additionalDrawPosY, null);
			additionalDrawPosY += additionalIcon.getHeight();
		}

		return result;
	}

	public static BufferedImage getAsDailyImage(ItemDefinitionContainer container, BufferedImage vbucksIcon,
			int price) {
		BufferedImage icon = container.getIcon();
		AthenaItemDefinition itemDefinition = container.getItemDefinition();
		icon = icon.getWidth() != 512 || icon.getHeight() != 512 ? scaleImage(icon, 512, 512) : icon;
		int barHeight = 83;

		BufferedImage background = RarityEnum.getRarityDaily(itemDefinition.getRarity());
		BufferedImage result = new BufferedImage(background.getWidth(), background.getHeight(),
				BufferedImage.TYPE_INT_ARGB);

		int additionalHeight = 160;
		Graphics2D g = result.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
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

		int addImageX = (int) (result.getWidth() * 0.28);
		int additionalDrawPosX = result.getWidth() - 5 - addImageX;
		int additionalDrawPosY = 5;
		for (BufferedImage additionalIcon : container.getAdditionalIcons()) {
			int addImageY = additionalIcon.getHeight() / additionalIcon.getWidth() * addImageX;
			additionalIcon = scaleImage(additionalIcon, addImageX, addImageY);
			g.drawImage(additionalIcon, additionalDrawPosX, additionalDrawPosY, null);
			additionalDrawPosY += additionalIcon.getHeight();
		}

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

	@SuppressWarnings("unused")
	public static BufferedImage getAsShopImage(ItemDefinitionContainer container, BufferedImage vbucksIcon, int price,
			boolean featured) throws IOException, ReadException {
		BufferedImage icon = container.getIcon();
		AthenaItemDefinition itemDefinition = container.getItemDefinition();
		if (icon == null) {

			icon = Resources.getFallbackIcon();

		}

		String rarityName = itemDefinition.getRarity();

		return featured ? getAsFeaturedImage(container, vbucksIcon, price)
				: getAsDailyImage(container, vbucksIcon, price);

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

	public static BufferedImage loadFeaturedIcon(AthenaItemDefinition itemDefinition,
			Map<String, Integer> pakFileReaderIndices, PakFileReader[] loadedPaks, Map<String, GameFile> gameFiles,
			String mountPrefix) throws ReadException, IOException {
		BufferedImage icon = null;
		// Load icon from SoftObjectProperty
		if (itemDefinition.usesDisplayPath()) {
			String displayAssetName = Package.toGameSpecificName(itemDefinition.getDisplayAssetPath(), mountPrefix);
			Package displayAssetPath = Package.loadPackageByName(displayAssetName, pakFileReaderIndices, loadedPaks,
					gameFiles);
			if(displayAssetPath == null)
				return null;
			if (displayAssetPath.getExports().size() > 0
					&& displayAssetPath.getExports().get(0) instanceof DisplayAssetPath) {
				DisplayAssetPath d = (DisplayAssetPath) displayAssetPath.getExports().get(0);
				String fileName = Package.toGameSpecificName(d.getDetailsImage(), mountPrefix);
				if (!fileName.contains("Athena/Prototype/Textures")
						|| !fileName.contains("Placeholder")) {
					Package iconImage = Package.loadPackageByName(fileName, pakFileReaderIndices, loadedPaks,
							gameFiles);
					if (iconImage != null) {
						if (iconImage.getExports().size() > 0) {
							if (iconImage.getExports().get(0) instanceof UTexture2D) {
								UTexture2D t = (UTexture2D) iconImage.getExports().get(0);
								try {
									icon = Texture2DToBufferedImage.readTexture(t);
								} catch (ReadException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					}
				}

			}
		}
		return icon;
	}
	
	public static BufferedImage loadNotFeaturedIcon(AthenaItemDefinition itemDefinition,
			Map<String, Integer> pakFileReaderIndices, PakFileReader[] loadedPaks, Map<String, GameFile> gameFiles,
			String mountPrefix) throws ReadException, IOException {
		BufferedImage icon = null;
		boolean iconLoaded = false;
		if (itemDefinition.hasIcons() && !iconLoaded) {
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
		}
		if (itemDefinition.usesHeroDefinition() && !iconLoaded) {
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

		}
		if (itemDefinition.usesWeaponDefinition() && !iconLoaded) {
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

		}
		return icon;
	}

	public static BufferedImage loadIcon(AthenaItemDefinition itemDefinition, Map<String, Integer> pakFileReaderIndices,
			PakFileReader[] loadedPaks, Map<String, GameFile> gameFiles, String mountPrefix)
			throws ReadException, IOException {
		BufferedImage icon = null;
		boolean iconLoaded = false;
		// Load icon from SoftObjectProperty
		try {
			icon = loadFeaturedIcon(itemDefinition, pakFileReaderIndices, loadedPaks, gameFiles, mountPrefix);
			if (icon != null) {
				iconLoaded = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(!iconLoaded) {
			icon = loadNotFeaturedIcon(itemDefinition, pakFileReaderIndices, loadedPaks, gameFiles, mountPrefix);
		}
		
		return icon;
	}

	public static Map<IconType, BufferedImage> generateAllIcons(AthenaItemDefinition itemDefinition,
			Map<String, Integer> pakFileReaderIndices, PakFileReader[] loadedPaks, Map<String, GameFile> gameFiles,
			String mountPrefix) throws ReadException, IOException {
		Map<IconType, BufferedImage> icon = new LinkedHashMap<>();
		boolean iconLoaded = false;
		// Load icon from SoftObjectProperty
		if (itemDefinition.usesDisplayPath() && !iconLoaded) {
			String displayAssetName = Package.toGameSpecificName(itemDefinition.getDisplayAssetPath(), mountPrefix);
			Package displayAssetPath = Package.loadPackageByName(displayAssetName, pakFileReaderIndices, loadedPaks,
					gameFiles);
			if (displayAssetPath.getExports().size() > 0
					&& displayAssetPath.getExports().get(0) instanceof DisplayAssetPath) {
				DisplayAssetPath d = (DisplayAssetPath) displayAssetPath.getExports().get(0);
				String fileName = Package.toGameSpecificName(d.getDetailsImage(), mountPrefix);
				if (!itemDefinition.getDisplayAssetPath().contains("Athena/Prototype/Textures")
						&& !itemDefinition.getDisplayAssetPath().contains("Placeholder")) {
					Package iconImage = Package.loadPackageByName(fileName, pakFileReaderIndices, loadedPaks,
							gameFiles);
					if (iconImage != null) {
						if (iconImage.getExports().size() > 0) {
							if (iconImage.getExports().get(0) instanceof UTexture2D) {
								UTexture2D t = (UTexture2D) iconImage.getExports().get(0);
								try {
									icon.putIfAbsent(IconType.FEATURED_IMAGE, Texture2DToBufferedImage.readTexture(t));
									iconLoaded = true;
								} catch (ReadException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					}
				}

			}
		}
		if (itemDefinition.hasIcons() && !iconLoaded) {
			String fileName = Package.toGameSpecificName(itemDefinition.getLargePreviewImage(), mountPrefix);
			Package largePreviewImage = Package.loadPackageByName(fileName, pakFileReaderIndices, loadedPaks,
					gameFiles);
			if (largePreviewImage != null) {
				if (largePreviewImage.getExports().size() > 0) {
					if (largePreviewImage.getExports().get(0) instanceof UTexture2D) {
						UTexture2D t = (UTexture2D) largePreviewImage.getExports().get(0);
						try {
							icon.putIfAbsent(IconType.NORMAL_ICON, Texture2DToBufferedImage.readTexture(t));
							iconLoaded = true;
						} catch (ReadException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
		if (itemDefinition.usesHeroDefinition() && !iconLoaded) {
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
								icon.putIfAbsent(IconType.NORMAL_ICON, Texture2DToBufferedImage.readTexture(t));
								iconLoaded = true;
							} catch (ReadException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
			}

		}
		if (itemDefinition.usesWeaponDefinition() && !iconLoaded) {
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
								icon.putIfAbsent(IconType.NORMAL_ICON, Texture2DToBufferedImage.readTexture(t));
								iconLoaded = true;
							} catch (ReadException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
			}

		}
		if (iconLoaded) {
			BufferedImage generatedImageIcon = icon.containsKey(IconType.FEATURED_IMAGE)
					? icon.get(IconType.FEATURED_IMAGE)
					: icon.get(IconType.NORMAL_ICON);
			ItemDefinitionContainer container = ItemDefinitionToBufferedImage.createContainer(itemDefinition,
					pakFileReaderIndices, loadedPaks, gameFiles, mountPrefix, Optional.ofNullable(generatedImageIcon),
					true);
			if (container.getItemDefinition().isVariantsLoaded())
				icon.put(IconType.GENERATED_WITH_VARIANTS,
						ItemDefinitionToBufferedImage.getImageWithVariants(container, Optional.empty()));
			icon.put(IconType.GENERATED_NO_VARIANTS,
					ItemDefinitionToBufferedImage.getImageNoVariants(container, Optional.empty()));
			return icon;
		} else {
			return null;
		}
	}

}
