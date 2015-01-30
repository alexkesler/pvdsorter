package org.kesler.pvdsorter.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Window;
import org.kesler.pvdsorter.domain.Branch;
import org.kesler.pvdsorter.domain.Record;
import org.kesler.pvdsorter.util.FXUtils;


public class RecordController extends AbstractController {

    @FXML
    protected TextField regnumTextField;
    @FXML
    protected DatePicker regDatePicker;
    @FXML
    protected TextField branchTextField;
    @FXML
    protected TextField mainRegnumTextField;


    private Record record;
    private Branch branch;
    private Record mainRecord;


    @Override
    @Deprecated
    public void show(Window owner) {
        super.show(owner);
    }

    public void showAndWait(Window owner, Record record) {
        this.record = record;
        branch = record.getBranch();
        mainRecord = record.getMainRecord();
        super.showAndWait(owner, "Выберите запись");
    }

    public Record getRecord() {
        return record;
    }

    @Override
    protected void updateContent() {
        regnumTextField.setText(record.getRegnum());
        regDatePicker.setValue(FXUtils.dateToLocalDate(record.getRegdate()));
        branchTextField.setText(branch == null ? "" : branch.getName());
        mainRegnumTextField.setText(mainRecord==null?"":mainRecord.getRegnum());
    }

    @Override
    protected void updateResult() {
        record.setRegnum(regnumTextField.getText());
        record.setRegdate(FXUtils.localDateToDate(regDatePicker.getValue()));
        record.setBranch(branch);
        record.setMainRecord(mainRecord);
        record.setMainRegnum(mainRecord == null ? "" : mainRecord.getRegnum());
    }

    @FXML
    protected void handleSelectBranchButtonAction(ActionEvent event) {

    }

    @FXML
    protected void handleSerectMainRecordButtonAction(ActionEvent event) {

    }
}
