package org.kesler.pvdsorter.repository;

import javafx.collections.ObservableList;
import org.kesler.pvdsorter.domain.Branch;


public interface BranchRepository {
    public void init();
    public ObservableList<Branch> getAllBrahches();
    public Branch getCommonBranch();
    public void addBranch(Branch branch);
    public void clearEmptyBranches();

}
