package me.fungames.jfortniteparse.ue4.objects;

import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;

@UStruct
public class FDataTableCategoryHandle {
    public FPackageIndex /*DataTable*/ DataTable;
    public FName ColumnName;
    public FName RowContents;
}
