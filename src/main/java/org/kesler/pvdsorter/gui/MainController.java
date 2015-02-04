package org.kesler.pvdsorter.gui;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.*;
import org.controlsfx.dialog.Dialog;
import org.kesler.pvdsorter.domain.Branch;
import org.kesler.pvdsorter.domain.Record;
import org.kesler.pvdsorter.export.RecordsExporter;
import org.kesler.pvdsorter.repository.BranchRepository;
import org.kesler.pvdsorter.repository.RecordRepository;
import org.kesler.pvdsorter.service.RecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Component
public class MainController  implements Initializable
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
    protected TreeTableView<Record> recordsTreeTableView;
    @FXML
    protected ProgressIndicator searchProgressIndicator;
    @FXML
    protected Button exportButton;
    @FXML
    protected ProgressIndicator exportProgressIndicator;

    @Autowired
    private RecordSelectController recordSelectController;

    @Autowired
    private RecordController recordController;

    @Autowired
    private AboutController aboutController;

    private ObservableList<Branch> observableBranches = FXCollections.observableArrayList();
    private final ObservableList<Record> observableDdRecords = FXCollections.observableArrayList();


    private RecordsTreeProcessor recordsTreeProcessor = new RecordsTreeProcessor();

    @Autowired
    private RecordService recordService;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private RecordRepository recordRepository;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        branchesListView.setItems(observableBranches);
        recordsTreeTableView.setShowRoot(false);

        branchesListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Branch>() {
            @Override
            public void changed(ObservableValue<? extends Branch> observable, Branch oldValue, Branch newValue) {
                if (newValue!=null)
                    updateRecordsTreeView(newValue);
            }
        });
    }


    public Parent getRoot() {
        return root;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        updateBranchesList();
        stage.show();
    }

    @FXML
    protected void handleSearchButtonAction(ActionEvent ev) {
        log.info("Find record by code: " + searchTextField.getText());
        findRecord(searchTextField.getText());
    }


    @FXML
    protected void handlePrintButtonAction(ActionEvent ev) {
        Branch selectedBranch = branchesListView.getSelectionModel().getSelectedItem();
        if (selectedBranch == null || selectedBranch.isCommon()) {
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
        branchRepository.init();
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

    @FXML
    protected void handleDeletePopupMenuItemAction(ActionEvent ev) {
        Record selectedRecord = recordsTreeTableView.getSelectionModel().getSelectedItem().getValue();
        if (selectedRecord != null) {
            recordRepository.removeRecord(selectedRecord);
            updateBranchesList();
            updateRecordsTreeView(selectedRecord.getMainRecord()==null?selectedRecord.getBranch():selectedRecord.getMainRecord().getBranch());
        }

    }

    @FXML
    protected void handleSelectMainPopupMenuItemAction(ActionEvent ev) {
        Record selectedRecord = recordsTreeTableView.getSelectionModel().getSelectedItem().getValue();
        if (selectedRecord!=null)
            selectMain(selectedRecord);
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

    // методы для включения / выключения элементов управления
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

    private void updateBranchesList() {
        observableBranches.clear();
        observableBranches.addAll(branchRepository.getAllBrahches());
    }


    // обновляет дерево записей
    private void updateRecordsTreeView(Branch branch) {
        if (branch == null) return;
        recordsTreeTableView.setRoot(recordsTreeProcessor.getRootForRecords(branch.getRecords()));
        for (TreeItem<Record> treeItem:recordsTreeTableView.getRoot().getChildren()) {
            treeItem.setExpanded(true);
        }
        branchesListView.getSelectionModel().select(branch);
    }

    // выбираем основную запись для записи
    private void selectMain(Record record) {
        Collection<Record> records = new ArrayList<Record>(recordRepository.getAllRecords());
        records.remove(record);  /// убираем это дело из списка
        recordSelectController.showAndWait(stage,records);  // открываем контроллер выбора осн дела
        if (recordSelectController.getResult() == AbstractController.Result.OK) {
            Record mainRecord = recordSelectController.getSelectedRecord();
            recordRepository.setMainRecord(record, mainRecord);

            updateRecordsTreeView(mainRecord.getBranch());
        }
    }

    // добавляем новую запись вручную
    private void inputRecord(String regnum) {
        Record record = new Record();
        record.setRegnum(regnum);
        recordController.showAndWait(stage, record);
        if (recordController.getResult() == AbstractController.Result.OK) {

            recordRepository.addRecord(record);
//            branchRepository.saveBranch(commonBranch);
            updateBranchesList();
            updateRecordsTreeView(record.getBranch());
        }
    }

    ///// Вспомогательные классы


    class RecordsTreeProcessor {
        private final Logger log = LoggerFactory.getLogger(this.getClass());

        TreeItem<Record> getRootForRecords(Collection<Record> records) {
            log.info("Building tree for " + records.size() + " records");
            TreeItem<Record> rootTreeItem = new TreeItem<Record>(new Record());

            for (Record record : records) {
                log.debug("Adding record: " + record.getRegnum());
                TreeItem<Record> recordTreeItem = new TreeItem<Record>(record);
                rootTreeItem.getChildren().add(recordTreeItem);
                for (Record subRecord : record.getSubRecords()) {
                    log.debug("Adding subRecord: " + subRecord);
                    recordTreeItem.getChildren().add(new TreeItem<Record>(subRecord));
                }
            }

            return rootTreeItem;
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
                Action response = Dialogs.create()
                        .owner(stage)
                        .title("Внимание")
                        .message("Не найдено. Ввести вручную?")
                        .actions(Dialog.ACTION_YES,Dialog.ACTION_NO)
                        .showConfirm();

                if (response == Dialog.ACTION_YES) {
                    inputRecord(code);
                }

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

            Branch commonBranch = branchRepository.getCommonBranch();

            Branch branch = record.getBranch();

            recordRepository.addRecord(record);
            updateBranchesList();

            branchesListView.getSelectionModel().select(commonBranch);
            recordsTreeTableView.setRoot(recordsTreeProcessor.getRootForRecords(commonBranch.getRecords()));
            for (TreeItem<Record> treeItem : recordsTreeTableView.getRoot().getChildren()) {
                treeItem.setExpanded(true);
            }
//            recordsTreeTableView.getSelectionModel().select();

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
