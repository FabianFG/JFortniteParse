package me.fungames.jfortniteparse.fort.objects;

import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.assets.exports.UDataTable;
import me.fungames.jfortniteparse.ue4.assets.exports.UObject;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;

@UStruct
public class FDataTableRowHandle {
    public UDataTable DataTable;
    public FName RowName;

    public UObject getRow() {
        if (DataTable != null) {
            return DataTable.findRow(RowName);
        }
        return null;
    }
}
