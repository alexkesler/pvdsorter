package org.kesler.pvdsorter.util;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;
import org.kesler.pvdsorter.domain.Record;

/**
 * Created by alex on 25.01.15.
 */
public class RegnumCellValueFactory implements Callback<TreeTableColumn.CellDataFeatures<Record,String>,ObservableValue<String>> {
    @Override
    public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Record, String> param) {
        return new ReadOnlyObjectWrapper<String>(param.getValue().getValue().getMainRecord()==null?
            param.getValue().getValue().getRegnum():"   "+param.getValue().getValue().getRegnum());
    }
}
