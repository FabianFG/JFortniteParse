package me.fungames.jfortniteparse.fort.objects.rows;

import me.fungames.jfortniteparse.ue4.objects.FTableRowBase;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;

public class FortQuestRewardTableRow extends FTableRowBase {
    public String QuestTemplateId;
    public FName TemplateId;
    public int Quantity;
    public boolean Hidden;
    public boolean Feature;
    public boolean Selectable;
}
