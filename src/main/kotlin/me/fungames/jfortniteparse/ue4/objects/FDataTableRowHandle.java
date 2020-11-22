package me.fungames.jfortniteparse.ue4.objects;

import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.assets.exports.UDataTable;
import me.fungames.jfortniteparse.ue4.assets.exports.UObject;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;

@UStruct
public class FDataTableRowHandle {
    public FPackageIndex /*DataTable*/ DataTable;
    public FName RowName;

    public UObject getRow() {
        if (DataTable != null) {
            UDataTable dataTable = (UDataTable) DataTable.load();
            if (dataTable != null) {
                return dataTable.findRow(RowName);
            }
        }
        return null;
    }

    public <T extends FTableRowBase> T getRowMapped() {
        if (DataTable != null) {
            UDataTable dataTable = (UDataTable) DataTable.load();
            if (dataTable != null) {
                return dataTable.findRowMapped(RowName);
            }
        }
        return null;
    }
}
