package me.fungames.jfortniteparse.fort.exports;

import kotlin.Lazy;
import kotlin.LazyKt;
import me.fungames.jfortniteparse.fort.objects.rows.HomebaseNodeGameplayEffectDataTableRow;
import me.fungames.jfortniteparse.ue4.assets.exports.UClass;
import me.fungames.jfortniteparse.ue4.assets.exports.UDataTable;
import me.fungames.jfortniteparse.ue4.assets.exports.UScriptStruct;

import java.util.List;

public class FortHomebaseNodeGameplayEffectDataTable extends UDataTable {
    public List<Lazy<UClass>> TemplateAttributeGEs;

    public FortHomebaseNodeGameplayEffectDataTable() {
        RowStruct = LazyKt.lazy(() -> new UScriptStruct(HomebaseNodeGameplayEffectDataTableRow.class));
    }
}
