package me.fungames.jfortniteparse.fort.exports;

import kotlin.LazyKt;
import me.fungames.jfortniteparse.ue4.assets.ObjectTypeRegistry;
import me.fungames.jfortniteparse.ue4.assets.exports.UDataTable;
import me.fungames.jfortniteparse.ue4.assets.exports.UScriptStruct;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;

import java.util.List;

public class FortHomebaseNodeGameplayEffectDataTable extends UDataTable {
    public List<FPackageIndex /*Class*/> TemplateAttributeGEs;

    public FortHomebaseNodeGameplayEffectDataTable() {
        RowStruct = LazyKt.lazy(() -> new UScriptStruct(ObjectTypeRegistry.INSTANCE.getStructs().get("HomebaseNodeGameplayEffectDataTableRow")));
    }
}
