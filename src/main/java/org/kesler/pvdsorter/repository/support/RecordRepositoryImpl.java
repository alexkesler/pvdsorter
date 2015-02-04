package org.kesler.pvdsorter.repository.support;

import org.kesler.pvdsorter.domain.Branch;
import org.kesler.pvdsorter.domain.Record;
import org.kesler.pvdsorter.repository.BranchRepository;
import org.kesler.pvdsorter.repository.RecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Iterator;


@Repository
public class RecordRepositoryImpl implements RecordRepository {
    private  final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BranchRepository branchRepository;

    @Override
    public void addRecord(Record record) {
        log.info("Add record: " + record);
        Branch commonBranch = branchRepository.getCommonBranch();
        if (record.getMainRegnum() == null || record.getMainRegnum().isEmpty()) {
            Branch branch = branchRepository.addBranchIfNotExist(record.getBranch());
            record.setBranch(branch);
            commonBranch = branchRepository.getCommonBranch();

            branch.addRecord(record);
            commonBranch.addRecord(record);

            findSubRecords(record);

//            branchRepository.saveBranch(branch);
//            branchRepository.saveBranch(commonBranch);

        } else {
            if (!findMainRecord(record))
                commonBranch.addRecord(record);
        }

    }

    @Override
    public void removeRecord(Record record) {
        log.info("Removing record: " + record);
        if (record.getMainRecord() != null) {
            Record mainRecord = record.getMainRecord();
            log.debug(">> mainRecord is not null -> remove from main record: " + mainRecord);
            mainRecord.getSubRecords().remove(record);
//            branchRepository.saveBranch(mainRecord.getBranch());
            return;
        }

        for (Branch branch : branchRepository.getAllBrahches()) {
            if (branch.getRecords().contains(record)) {
                log.debug(">> remove from branch: " + branch);
                branch.getRecords().remove(record);
//                branchRepository.saveBranch(branch);
            }
        }

        branchRepository.clearEmptyBranches();
    }

    @Override
    public void setMainRecord(Record record, Record mainRecord) {
        log.info("Set for record: " + record + " main record: " + mainRecord);
        record.setMainRecord(mainRecord);
        mainRecord.addSubRecord(record);
//        branchRepository.saveBranch(mainRecord.getBranch());

        deleteMainRecord(record);
    }

    @Override
    public Collection<Record> getAllRecords() {
        log.info("Send all records");
        Branch commonBranch = branchRepository.getCommonBranch();
        return commonBranch.getRecords();
    }


    private void findSubRecords(Record mainRecord) {
        log.info("Find subRecords for record: " + mainRecord);
        Iterator<Record> recordIterator = getAllRecords().iterator();
        while (recordIterator.hasNext()) {
            Record subRecord = recordIterator.next();
            if (subRecord.getMainRecord()==null &&
                    subRecord.getMainRegnum()!=null &&
                    subRecord.getMainRegnum().equals(mainRecord.getRegnum())) {
                log.debug(">> find subRecord "+ subRecord);
                subRecord.setMainRecord(mainRecord);
                mainRecord.addSubRecord(subRecord);
//                branchRepository.saveBranch(mainRecord.getBranch());
                recordIterator.remove();
            }
        }
    }

    private boolean findMainRecord(Record subRecord) {
        log.info("Finding main record for record " + subRecord);
        Branch commonBranch = branchRepository.getCommonBranch();
        for (Record mainRecord : commonBranch.getRecords()) {
            if (mainRecord.getRegnum().equals(subRecord.getMainRegnum())) {
                log.debug(">> find main record: " + mainRecord);
                mainRecord.addSubRecord(subRecord);
                subRecord.setMainRecord(mainRecord);
//                branchRepository.saveBranch(mainRecord.getBranch());
                return true;
            }
        }
        return false;
    }

    private void deleteMainRecord(Record record) {
        log.info("Remove record " + record + " from branches");
        for (Branch branch : branchRepository.getAllBrahches()) {
            if (branch.getRecords().contains(record)) {
                log.debug(">> remove record from branch: " + branch);
                branch.getRecords().remove(record);
//                branchRepository.saveBranch(branch);
            }
        }

        branchRepository.clearEmptyBranches();
    }




}
