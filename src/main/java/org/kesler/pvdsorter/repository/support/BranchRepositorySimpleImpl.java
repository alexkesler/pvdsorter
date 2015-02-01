package org.kesler.pvdsorter.repository.support;

import org.kesler.pvdsorter.domain.Branch;
import org.kesler.pvdsorter.repository.BranchRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Repository
public class BranchRepositorySimpleImpl implements BranchRepository {
    private List<Branch> branches;

    @Override
    public void init() {
        branches = new ArrayList<Branch>();
        Branch commonBranch = new Branch();
        commonBranch.setName("Все");
        commonBranch.setCommon(true);
        branches.add(commonBranch);
    }

    @Override
    public Collection<Branch> getAllBrahches() {
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
