package org.kesler.pvdsorter.service.support;

import org.kesler.common.BranchesDTO;
import org.kesler.pvdsorter.domain.Branch;
import org.kesler.pvdsorter.service.BranchService;
import org.kesler.pvdsorter.service.transform.BranchTransform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;



public class BranchRestServiceImpl implements BranchService {
    private static final Logger log = LoggerFactory.getLogger(RecordRestServiceImpl.class);
    private String serverUrl;

    @Autowired
    private RestTemplate restTemplate;

    public BranchRestServiceImpl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    @Override
    public Collection<Branch> getAllBranches() {
        log.debug("Getting branchesDTO ");
        BranchesDTO branchesDTO = restTemplate.getForObject(serverUrl + "/branches", BranchesDTO.class);
        log.debug("Server returned " + branchesDTO.getBranchDTOs().size() + " branches");
        return BranchTransform.transform(branchesDTO.getBranchDTOs());
    }
}
