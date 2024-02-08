package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.ue4.assets.UProperty;
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText;

public class ItemDefinitionBase extends McpItemDefinitionBase {
    @UProperty(name = "ItemName")
    public FText DisplayName;
    @UProperty(name = "ItemDescription")
    public FText Description;
    @UProperty(name = "ItemShortDescription")
    public FText ShortDescription;
    // public ItemComponentContainer ComponentContainer;
}
