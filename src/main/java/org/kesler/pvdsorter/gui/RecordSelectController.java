package org.kesler.pvdsorter.gui;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Window;
import org.kesler.pvdsorter.domain.Record;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class RecordSelectController extends AbstractController {

    @FXML
    protected ListView<Record> recordsListView;

    private final ObservableList<Record> observableRecords = FXCollections.observableArrayList();
    private Record selectedRecord;

    @FXML
    protected void initialize() {
        recordsListView.setItems(observableRecords);
        recordsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    public void showAndWait(Window owner, Collection<Record> records) {
        observableRecords.clear();
        observableRecords.addAll(records);
        if (observableRecords.size() > 0) {
            recordsListView.getSelectionModel().select(0);
            recordsListView.requestFocus();
        }

        super.showAndWait(owner,"Выберите нужное из списка");
    }


    @Override
    protected void updateResult() {
        selectedRecord = recordsListView.getSelectionModel().getSelectedItem();
    }

    public Record getSelectedRecord() {
        return selectedRecord;
    }

    @FXML
    protected void handleRecordsListMouseClick(MouseEvent ev) {
        if (ev.getClickCount() == 2) {
            handleOk();
        }
    }

    @FXML
    protected void handleRecordsListViewKeyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            handleOk();
        }
    }
}
