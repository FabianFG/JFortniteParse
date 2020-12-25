package me.fungames.jfortniteparse.ue4.assets.exports.components;

import kotlin.Lazy;
import me.fungames.jfortniteparse.ue4.assets.exports.mats.UMaterialInterface;
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText;
import me.fungames.jfortniteparse.ue4.objects.core.math.FColor;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;

public class UTextRenderComponent extends UPrimitiveComponent {
    public FText Text;
    public Lazy<UMaterialInterface> TextMaterial;
    public FPackageIndex /*Font*/ Font;
    public EHorizTextAligment HorizontalAlignment;
    public EVerticalTextAligment VerticalAlignment;
    public FColor TextRenderColor;
    public Float XScale;
    public Float YScale;
    public Float WorldSize;
    public Float InvDefaultSize;
    public Float HorizSpacingAdjust;
    public Float VertSpacingAdjust;
    public Boolean bAlwaysRenderAsText;

    public enum EHorizTextAligment {
        EHTA_Left,
        EHTA_Center,
        EHTA_Right
    }

    public enum EVerticalTextAligment {
        EVRTA_TextTop,
        EVRTA_TextCenter,
        EVRTA_TextBottom,
        EVRTA_QuadTop
    }
}
