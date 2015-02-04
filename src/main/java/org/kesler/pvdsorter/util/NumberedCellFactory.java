package org.kesler.pvdsorter.util;

import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;
import org.kesler.pvdsorter.domain.Record;

/**
 * Created by alex on 25.01.15.
 */
public class NumberedCellFactory implements Callback<TreeTableColumn<Record,Record>,TreeTableCell<Record,Record>>{
    @Override
    public TreeTableCell<Record, Record> call(TreeTableColumn<Record, Record> param) {
        return new TreeTableCell<Record, Record>(){
            @Override
            protected void updateItem(Record item, boolean empty) {
                super.updateItem(item, empty);

                if (this.getChildren().size()==0 && item != null) {
                    setText(this.getIndex() + 1 + "");
                } else {
                    setText("");
                }
            }
        };
    }
}
