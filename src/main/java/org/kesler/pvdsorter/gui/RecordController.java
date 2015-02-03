package org.kesler.pvdsorter.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Window;
import org.kesler.pvdsorter.domain.Branch;
import org.kesler.pvdsorter.domain.Record;
import org.kesler.pvdsorter.repository.BranchRepository;
import org.kesler.pvdsorter.util.FXUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;


public class RecordController extends AbstractController {

    @FXML
    protected TextField regnumTextField;
    @FXML
    protected DatePicker regDatePicker;
    @FXML
    protected TextField branchTextField;
    @FXML
    protected TextField mainRegnumTextField;

    @Autowired
    private RecordSelectController recordSelectController;

    @Autowired
    private BranchRepository branchRepository;


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
        mainRecord.getSubRecords().add(record);
        record.setMainRegnum(mainRecord == null ? "" : mainRecord.getRegnum());
    }

    @FXML
    protected void handleSelectBranchButtonAction(ActionEvent event) {

    }

    @FXML
    protected void handleSelectMainRecordButtonAction(ActionEvent event) {
        Collection<Record> allRecords = branchRepository.getCommonBranch().getRecords();
        recordSelectController.showAndWait(stage,allRecords);
        if (recordSelectController.getResult() == Result.OK) {
            mainRecord = recordSelectController.getSelectedRecord();
            mainRegnumTextField.setText(mainRecord.getRegnum());
            branch = mainRecord.getBranch();
            branchTextField.setText(branch.getName());
        }
    }
}
