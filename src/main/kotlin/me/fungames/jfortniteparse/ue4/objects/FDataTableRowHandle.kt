package me.fungames.jfortniteparse.ue4.objects;

import kotlin.Lazy;
import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.assets.exports.UDataTable;
import me.fungames.jfortniteparse.ue4.assets.exports.UObject;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;

@UStruct
public class FDataTableRowHandle {
    public Lazy<UDataTable> DataTable;
    public FName RowName;

    public UObject getRow() {
        if (DataTable != null) {
            UDataTable dataTable = DataTable.getValue();
            if (dataTable != null) {
                return dataTable.findRow(RowName);
            }
        }
        return null;
    }

    public <T extends FTableRowBase> T getRowMapped() {
        if (DataTable != null) {
            UDataTable dataTable = DataTable.getValue();
            if (dataTable != null) {
                return dataTable.findRowMapped(RowName);
            }
        }
        return null;
    }
}
