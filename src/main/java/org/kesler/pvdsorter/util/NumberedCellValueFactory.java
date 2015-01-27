package org.kesler.pvdsorter.util;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;
import org.kesler.pvdsorter.domain.Record;

/**
 * Created by alex on 25.01.15.
 */
public class NumberedCellValueFactory implements Callback<TreeTableColumn.CellDataFeatures<Record,Record>,ObservableValue<Record>> {
    @Override
    public ObservableValue<Record> call(TreeTableColumn.CellDataFeatures<Record, Record> param) {
        return new ReadOnlyObjectWrapper<Record>(param.getValue().getValue());
    }
}
