package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.ue4.assets.exports.UDataTable;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;

import java.util.List;

public class FortHomebaseNodeGameplayEffectDataTable extends UDataTable {
    public List<FPackageIndex /*Class*/> TemplateAttributeGEs;

    public FortHomebaseNodeGameplayEffectDataTable() {
        rowStructName = FName.dummy("HomebaseNodeGameplayEffectDataTableRow");
    }
}
