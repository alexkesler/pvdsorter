package org.kesler.pvdsorter.util;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;
import org.kesler.pvdsorter.domain.Record;


public class RegnumCellValueFactory implements Callback<TreeTableColumn.CellDataFeatures<Record,String>,ObservableValue<String>> {
    @Override
    public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Record, String> param) {
        Record record = param.getValue().getValue();
        String value;
        if (record.getMainRecord()==null) {
            if (record.getMainRegnum() == null || record.getMainRegnum().isEmpty()) {
                value = record.getRegnum();
            } else {
                value = "! " + record.getRegnum();
            }
        } else {
            value = "   "+record.getRegnum();
        }

        return new ReadOnlyObjectWrapper<String>(value);
    }
}
