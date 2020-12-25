package me.fungames.jfortniteparse.fort.objects.rows;

import me.fungames.jfortniteparse.fort.objects.FortMcpQuestObjectiveInfo;
import me.fungames.jfortniteparse.ue4.objects.FTableRowBase;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagContainer;

import java.util.List;

public class FortQuestObjectiveStatTableRow extends FTableRowBase {
    public FortMcpQuestObjectiveInfo.EFortQuestObjectiveStatEvent Type;
    public FGameplayTagContainer TargetTagContainer;
    public FGameplayTagContainer SourceTagContainer;
    public FGameplayTagContainer ContextTagContainer;
    public String Condition;
    public String TemplateId;
    public List<String> AlternateTemplateIds;
    public boolean bIsCached;
}
