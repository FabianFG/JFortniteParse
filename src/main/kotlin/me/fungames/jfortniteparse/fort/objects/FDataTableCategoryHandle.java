package me.fungames.jfortniteparse.fort.objects;

import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.assets.exports.UDataTable;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;

@UStruct
public class FDataTableCategoryHandle {
    public UDataTable DataTable;
    public FName ColumnName;
    public FName RowContents;
}
