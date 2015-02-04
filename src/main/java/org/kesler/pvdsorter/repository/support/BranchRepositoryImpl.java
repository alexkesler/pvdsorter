package org.kesler.pvdsorter.repository.support;

import org.kesler.pvdsorter.domain.Branch;
import org.kesler.pvdsorter.repository.BranchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class BranchRepositoryImpl implements BranchRepository {
    private  final Logger log = LoggerFactory.getLogger(this.getClass());
    private final List<Branch> branches;

    public BranchRepositoryImpl() {
        branches = new ArrayList<Branch>();
        init();
    }

    @Override
    public void init() {
        log.info("Init branches..");

        branches.clear();
        Branch commonBranch = new Branch();
        commonBranch.setName("Все");
        commonBranch.setCommon(true);
        branches.add(commonBranch);
    }

    @Override
    public Collection<Branch> getAllBrahches() {
        log.info("Send all branches");
        return branches;
    }

    @Override
    public Branch addBranchIfNotExist(Branch branch) {
        int index = branches.indexOf(branch);
        if (index >= 0) {
            return branches.get(index);
        } else {
            if (branch != null) branches.add(branch);
            return branch;
        }
    }

    @Override
    public Branch getCommonBranch() {
        log.info("Send common branch");
        Branch commonBranch = null;
        for (Branch branch : branches) {
            if (branch.isCommon()) {
                commonBranch = branch;
            }
        }
        return commonBranch;
    }

    @Override
    public void saveBranch(Branch branch) {
        log.info("Saving branch: "+ branch);
        int index = branches.indexOf(branch);
        if (index >= 0) {
            log.debug(">> branch exist, remove before adding");
            branches.remove(branch);
            branches.add(index, branch);
        } else {
            branches.add(branch);
        }

    }

    @Override
    public void clearEmptyBranches() {
        log.info("Clearing empty branches");
        Iterator<Branch> branchIterator = branches.iterator();
        while (branchIterator.hasNext()) {
            Branch branch = branchIterator.next();
            if (branch.getRecords().size() == 0) {
                log.debug(">> clear empty branch " + branch);
                branchIterator.remove();
            }
        }
    }
}
