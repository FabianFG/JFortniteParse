package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.ue4.objects.core.math.FLinearColor;
import me.fungames.jfortniteparse.ue4.objects.core.math.FVector2D;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

public class AthenaLoadingScreenItemDefinition extends AthenaCosmeticItemDefinition {
	public FSoftObjectPath BackgroundImage;
	public FSoftObjectPath BackgroundMaterialOrTexture;
	public FSoftObjectPath /*SoftClassPath*/ BackgroundWidget;
	public FVector2D BackgroundDesiredSize;
	public FLinearColor BackgroundColor;
}
