package org.kesler.pvdsorter.gui;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.controlsfx.dialog.Dialogs;
import org.kesler.pvdsorter.domain.Branch;
import org.kesler.pvdsorter.domain.Record;
import org.kesler.pvdsorter.export.RecordsExporter;
import org.kesler.pvdsorter.service.RecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.concurrent.ExecutionException;

@Component
public class MainController
{
    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    private Stage stage;

    @FXML
    protected Parent root;

    @FXML
    protected TextField searchTextField;
    @FXML
    protected Button searchButton;
    @FXML
    protected ListView<Branch> branchesListView;
    @FXML
    protected TableView<Record> recordsTableView;
    @FXML
    protected ProgressIndicator searchProgressIndicator;
    @FXML
    protected Button exportButton;
    @FXML
    protected ProgressIndicator exportProgressIndicator;

    @Autowired
    private RecordSelectController recordSelectController;

    @Autowired
    private AboutController aboutController;

    private final ObservableList<Branch> observableBranches = FXCollections.observableArrayList();
    private final ObservableList<Record> observableRecords = FXCollections.observableArrayList();

    private BranchesProcessor branchesProcessor = new BranchesProcessor(observableBranches);

    @Autowired
    private RecordService recordService;

    @FXML
    protected void initialize() {
        branchesListView.setItems(observableBranches);
        recordsTableView.setItems(observableRecords);
        initLists();

        branchesListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Branch>() {
            @Override
            public void changed(ObservableValue<? extends Branch> observable, Branch oldValue, Branch newValue) {
                observableRecords.clear();
                observableRecords.addAll(newValue.getRecords());
            }
        });
    }


    public Parent getRoot() {
        return root;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    protected void handleSearchButtonAction(ActionEvent ev) {
        log.info("Find record by code: " + searchTextField.getText());
        findRecord(searchTextField.getText());
    }


    @FXML
    protected void handlePrintButtonAction(ActionEvent ev) {
        Branch selectedBranch = branchesListView.getSelectionModel().getSelectedItem();
        if (selectedBranch == null || selectedBranch.isAll()) {
            Notifications.create()
                    .owner(stage)
                    .title("Внимание")
                    .text("Выберите филиал")
                    .position(Pos.CENTER)
                    .hideAfter(new Duration(1000))
                    .showWarning();
            return;
        }

        exportBranch(selectedBranch);
    }


    @FXML
    protected void handleClearMenuItemAction(ActionEvent event) {
        log.info("Clear lists");
        initLists();
    }

    @FXML
    protected void handleCloseMenuItemAction(ActionEvent event) {
        log.info("Close Main Window");
        stage.hide();
    }

    @FXML
    protected void handleAboutMenuItemAction(ActionEvent event) {
        aboutController.show(stage);
    }

    private void initLists() {
        observableBranches.clear();
        observableRecords.clear();
        Branch allBranch = new Branch();
        allBranch.setName("Все");
        allBranch.setAll(true);
        observableBranches.add(allBranch);
    }

    private void findRecord(String code) {
        RecordsReader recordsReader = new RecordsReader(code);
        BooleanBinding runningBinding = recordsReader.stateProperty().isEqualTo(Task.State.RUNNING);
        searchProgressIndicator.visibleProperty().bind(runningBinding);

        disableControls();
        new Thread(recordsReader).start();
    }

    private void exportBranch(Branch branch) {
        log.info("exporting branch: "+ branch.getName());
        if (branch.getRecords().size() == 0) {
            Notifications.create()
                    .owner(stage)
                    .title("Внимание")
                    .text("Список для выгрузки пуст")
                    .position(Pos.CENTER)
                    .hideAfter(new Duration(1000))
                    .showWarning();
            return;
        }


        RecordsExporterWorker exporterWorker = new RecordsExporterWorker(new RecordsExporter(branch.getRecords()));
        BooleanBinding runningBinding = exporterWorker.stateProperty().isEqualTo(Task.State.RUNNING);
        exportProgressIndicator.visibleProperty().bind(runningBinding);

        disableControls();
        new Thread(exporterWorker).start();

    }


    private void disableControls() {
        searchButton.disableProperty().setValue(true);
        exportButton.disableProperty().setValue(true);
        searchTextField.disableProperty().setValue(true);
    }

    private void enableControls() {
        searchButton.disableProperty().setValue(false);
        exportButton.disableProperty().setValue(false);
        searchTextField.disableProperty().setValue(false);

    }

    ///// Вспомогательные классы

    class BranchesProcessor {
        private final ObservableList<Branch> obervableBranches;

        BranchesProcessor(ObservableList<Branch> observableBranches) {
            this.obervableBranches = observableBranches;
        }

        Branch getAllBranch() {
            Branch allBranch = null;
            for (Branch branch : obervableBranches) {
                if (branch.isAll()) {
                    allBranch=branch;
                }
            }
            return allBranch;
        }

        Branch addBranchIfNotExist(Branch branch) {
            int branchIndex = observableBranches.indexOf(branch);
            Branch storedBranch;
            if (branchIndex>0) {
                storedBranch = observableBranches.get(branchIndex);
            } else {
                storedBranch = branch;
                observableBranches.addAll(branch);
            }
            return storedBranch;
        }


    }


    /// Классы для отработки логики в отдельном потоке

    class RecordsReader extends Task<Collection<Record>> {
        private final Logger log = LoggerFactory.getLogger(getClass());
        private String code;

        public RecordsReader(String code) {
            this.code = code;
        }

        @Override
        protected Collection<Record> call() throws Exception {
            return recordService.getRecordsByCode(code);
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            enableControls();

            Collection<Record> records;
            try {
                records = get();
            } catch (InterruptedException e) {
                log.error("Interrupted",e);
                return;
            } catch (ExecutionException e) {
                log.error("Error reading records: " + e,e);
                Dialogs.create()
                        .owner(stage)
                        .title("Ошибка")
                        .message("Ошибка при чтении данных: "+e.getMessage())
                        .showException(e);
                return;
            }

            if (records.size() == 0) {
                Notifications.create()
                        .owner(stage)
                        .title("Внимание")
                        .text("Не найдено")
                        .position(Pos.CENTER)
                        .hideAfter(new Duration(1000))
                        .showWarning();
                searchTextField.requestFocus();
                searchTextField.selectAll();
                return;
            }

            Record record;

            if (records.size() > 1) {
                recordSelectController.showAndWait(stage,records);
                if (recordSelectController.getResult() == AbstractController.Result.OK) {
                    record = recordSelectController.getSelectedRecord();
                }
                else {
                    return;
                }
            } else {
                record = records.iterator().next();
            }

            Branch allBranch = branchesProcessor.getAllBranch();
            Branch branch = branchesProcessor.addBranchIfNotExist(record.getBranch());

            allBranch.addRecord(record);
            branch.addRecord(record);

            branchesListView.getSelectionModel().select(allBranch);
            observableRecords.clear();
            observableRecords.addAll(allBranch.getRecords());
            recordsTableView.getSelectionModel().select(record);

            Notifications.create()
                    .owner(stage)
                    .title("Добавлено")
                    .text("Дело " + record.getRegnum() + "\n" +
                    " филиал: "+ branch.getName())
                    .position(Pos.CENTER)
            .hideAfter(new Duration(1700))
            .showInformation();

            searchTextField.requestFocus();
            searchTextField.selectAll();
        }

        @Override
        protected void failed() {
            super.failed();
            Throwable exception = getException();
            log.error("Error reading records: "+exception,exception);
            Dialogs.create()
                    .owner(stage)
                    .title("Ошибка")
                    .message("Ошибка при чтении данных: " + exception)
            .showException(exception);

        }


     }


    class RecordsExporterWorker extends Task<Void> {
        private RecordsExporter recordsExporter;

        public RecordsExporterWorker(RecordsExporter recordsExporter) {
            this.recordsExporter = recordsExporter;
        }

        @Override
        protected Void call() throws Exception {
            recordsExporter.prepare();
            return null;
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            log.info("Preparing successfull. Saving..");
            enableControls();
            recordsExporter.save(stage);
        }

        @Override
        protected void failed() {
            Throwable exception = getException();
            log.error("Error updating list: " + exception, exception);
            Dialogs.create()
                    .owner(stage)
                    .title("Ошибка")
                    .message("Ошибка при получении списка: " + exception)
                    .showException(exception);
        }
    }

}
