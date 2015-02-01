package org.kesler.pvdsorter.repository.support;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.kesler.pvdsorter.domain.Branch;
import org.kesler.pvdsorter.repository.BranchRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class BranchRepositorySimpleImpl implements BranchRepository {
    private final ObservableList<Branch> branches = FXCollections.observableArrayList();

    @Override
    public void init() {
        branches.clear();
        Branch commonBranch = new Branch();
        commonBranch.setName("Все");
        commonBranch.setCommon(true);
        branches.add(commonBranch);
    }

    @Override
    public ObservableList<Branch> getAllBrahches() {
        return branches;
    }

    @Override
    public Branch getCommonBranch() {
        Branch commonBranch = null;
        for (Branch branch : branches) {
            if (branch.isCommon()) {
                commonBranch = branch;
            }
        }
        return commonBranch;
    }

    @Override
    public void addBranch(Branch branch) {
        if (!branches.contains(branch)) {
            branches.add(branch);
        }
    }

    @Override
    public void clearEmptyBranches() {
        Iterator<Branch> branchIterator = branches.iterator();
        while (branchIterator.hasNext()) {
            Branch branch = branchIterator.next();
            if (branch.getRecords().size() == 0) {
                branchIterator.remove();
            }
        }
    }
}
