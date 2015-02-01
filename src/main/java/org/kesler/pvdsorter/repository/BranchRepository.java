package org.kesler.pvdsorter.repository;

import org.kesler.pvdsorter.domain.Branch;

import java.util.Collection;


public interface BranchRepository {
    public void init();
    public Collection<Branch> getAllBrahches();
    public Branch getCommonBranch();
    public void addBranch(Branch branch);
    public void clearEmptyBranches();

}
