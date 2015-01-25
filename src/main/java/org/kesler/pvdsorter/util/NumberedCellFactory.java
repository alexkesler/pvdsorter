package org.kesler.pvdsorter.util;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import org.kesler.pvdsorter.domain.Record;

/**
 * Created by alex on 25.01.15.
 */
public class NumberedCellFactory implements Callback<TableColumn<Record,Record>,TableCell<Record,Record>>{
    @Override
    public TableCell<Record, Record> call(TableColumn<Record, Record> param) {
        return new TableCell<Record, Record>(){
            @Override
            protected void updateItem(Record item, boolean empty) {
                super.updateItem(item, empty);

                if (this.getTableRow() != null && item != null) {
                    setText(this.getTableRow().getIndex() + 1 + "");
                } else {
                    setText("");
                }
            }
        };
    }
}
