package org.kesler.pvdsorter.service.transform;


import org.kesler.common.BranchDTO;
import org.kesler.pvdsorter.domain.Branch;

import java.util.ArrayList;
import java.util.Collection;

public abstract class BranchTransform {

    public static Branch transform(BranchDTO branchDTO) {
        Branch branch = new Branch();

        branch.setId(branchDTO.getId());
        branch.setUuid(branchDTO.getUuid());
        branch.setCode(branchDTO.getCode());
        branch.setName(branchDTO.getName());


        return branch;
    }

    public static Collection<Branch> transform(Collection<BranchDTO> branchDTOs) {
        Collection<Branch> branches = new ArrayList<Branch>();

        for (BranchDTO branchDTO:branchDTOs) {
            branches.add(transform(branchDTO));
        }

        return branches;
    }

    public static BranchDTO transform(Branch branch) {
        BranchDTO branchDTO = new BranchDTO();

        branchDTO.setId(branch.getId());
        branchDTO.setUuid(branch.getUuid());
        branchDTO.setCode(branch.getCode());
        branchDTO.setName(branch.getName());

        return branchDTO;
    }
}
