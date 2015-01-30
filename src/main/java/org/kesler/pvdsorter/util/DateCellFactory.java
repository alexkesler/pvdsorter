package org.kesler.pvdsorter.util;

import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;
import org.kesler.pvdsorter.domain.Record;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by alex on 25.01.15.
 */
public class DateCellFactory implements Callback<TreeTableColumn<Record,Date>,TreeTableCell<Record,Date>>{
    @Override
    public TreeTableCell<Record, Date> call(TreeTableColumn<Record, Date> param) {
        return new TreeTableCell<Record, Date>(){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);

                if ( item != null) {
                    setText(simpleDateFormat.format(item));
                } else {
                    setText("");
                }
            }
        };
    }
}
