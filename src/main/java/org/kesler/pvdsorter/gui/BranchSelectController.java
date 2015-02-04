package org.kesler.pvdsorter.gui;

import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Window;
import org.controlsfx.dialog.Dialogs;
import org.kesler.pvdsorter.domain.Branch;
import org.kesler.pvdsorter.repository.BranchRepository;
import org.kesler.pvdsorter.service.BranchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;


@Component
public class BranchSelectController extends AbstractController implements Initializable{
    private  final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ObservableList<Branch> observableBranches = FXCollections.observableArrayList();
    private Branch selectedBranch;

    @Autowired
    private BranchRepository branchRepository;


    @Autowired
    private BranchService branchService;

    @FXML
    protected ListView<Branch> branchesListView;

    @FXML
    protected ProgressIndicator branchesLoadingIndicator;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        branchesListView.setItems(observableBranches);
    }

    @Override
    public void showAndWait(Window owner) {
        observableBranches.clear();
        observableBranches.addAll(branchRepository.getAllBrahches());
        observableBranches.removeAll(branchRepository.getCommonBranch());
        if (observableBranches.size() > 0) {
            branchesListView.getSelectionModel().select(0);
            branchesListView.requestFocus();
        }
        super.showAndWait(owner, "Выберите филиал");
    }


    @Override
    protected void updateResult() {
        selectedBranch = branchesListView.getSelectionModel().getSelectedItem();
    }

    public Branch getSelectedBranch() {
        return selectedBranch;
    }

    @FXML
    protected void handleBranchesListMouseClick(MouseEvent event) {
        if (event.getClickCount() == 2) {
            handleOk();
        }
    }

    @FXML
    protected void handleBranchesListViewKeyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            handleOk();
        }
    }


    @FXML
    protected void handleLoadAllBranchesButtonAction(ActionEvent event) {
        BranchesLoader branchesLoader = new BranchesLoader();
        BooleanBinding runningBinding = branchesLoader.stateProperty().isEqualTo(Task.State.RUNNING);

        branchesLoadingIndicator.visibleProperty().bind(runningBinding);
        new Thread(branchesLoader).start();
    }


    class BranchesLoader extends Task<Collection<Branch>> {
        @Override
        protected Collection<Branch> call() throws Exception {
            return branchService.getAllBranches();
        }

        @Override
        protected void failed() {
            super.failed();
            Throwable exception = getException();
            log.error("Error updating list: " + exception, exception);
            Dialogs.create()
                    .owner(stage)
                    .title("Ошибка")
                    .message("Ошибка при получении списка: " + exception)
                    .showException(exception);
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            try {
                Collection<Branch> branches = get();
                observableBranches.clear();
                observableBranches.addAll(branches);
            } catch (InterruptedException e) {
                log.error("Interrupted",e);
            } catch (ExecutionException e) {
                log.error("Error updating list: " + e, e);
                Dialogs.create()
                        .owner(stage)
                        .title("Ошибка")
                        .message("Ошибка при получении списка: " + e)
                        .showException(e);
            }
        }
    }

}
