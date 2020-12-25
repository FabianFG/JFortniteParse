package me.fungames.jfortniteparse.ue4.objects.gameplaytags;

import kotlin.UByte;
import me.fungames.jfortniteparse.ue4.assets.UStruct;

import java.util.List;

@UStruct
public class FGameplayTagQuery {
    public Integer TokenStreamVersion;
    public List<FGameplayTag> TagDictionary;
    public List<UByte> QueryTokenStream;
    public String UserDescription;
    public String AutoDescription;
}
