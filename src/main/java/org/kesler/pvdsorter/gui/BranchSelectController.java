package org.kesler.pvdsorter.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Window;
import org.kesler.pvdsorter.domain.Branch;
import org.kesler.pvdsorter.repository.BranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;



@Component
public class BranchSelectController extends AbstractController implements Initializable{

    private final ObservableList<Branch> observableBranches = FXCollections.observableArrayList();
    private Branch selectedBranch;

    @Autowired
    private BranchRepository branchRepository;

    @FXML
    protected ListView<Branch> branchesListView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        branchesListView.setItems(observableBranches);
    }

    @Override
    public void showAndWait(Window owner) {
        observableBranches.clear();
        observableBranches.addAll(branchRepository.getAllBrahches());
        selectedBranch = null;
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


}
