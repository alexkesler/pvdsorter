package org.kesler.pvdsorter.util;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import org.kesler.pvdsorter.domain.Record;

/**
 * Created by alex on 25.01.15.
 */
public class NumberedCellValueFactory implements Callback<TableColumn.CellDataFeatures<Record,Record>,ObservableValue<Record>> {
    @Override
    public ObservableValue<Record> call(TableColumn.CellDataFeatures<Record, Record> param) {
        return new ReadOnlyObjectWrapper<Record>(param.getValue());
    }
}
