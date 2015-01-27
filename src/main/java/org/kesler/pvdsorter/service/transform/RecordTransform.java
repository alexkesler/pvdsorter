package org.kesler.pvdsorter.service.transform;


import org.kesler.common.RecordDTO;
import org.kesler.pvdsorter.domain.Record;

import java.util.ArrayList;
import java.util.Collection;

public abstract class RecordTransform {

    public static Record transform(RecordDTO recordDTO) {
        Record record = new Record();

        record.setId(recordDTO.getId());
        record.setBranch(BranchTransform.transform(recordDTO.getBranchDTO()));
        record.setRegnum(recordDTO.getRegnum());
        record.setRegdate(recordDTO.getRegdate());
        record.setPrevRegnum(recordDTO.getPrevRegnum());

        return record;
    }

    public static Collection<Record> transform(Collection<RecordDTO> recordDTOs) {
        Collection<Record> records = new ArrayList<Record>();

        for (RecordDTO recordDTO:recordDTOs) {
            records.add(transform(recordDTO));
        }

        return records;
    }

    public static RecordDTO transform(Record record) {
        RecordDTO recordDTO = new RecordDTO();

        recordDTO.setId(record.getId());
        recordDTO.setBranchDTO(BranchTransform.transform(record.getBranch()));
        recordDTO.setRegnum(record.getRegnum());
        recordDTO.setRegdate(record.getRegdate());
        recordDTO.setPrevRegnum(record.getPrevRegnum());

        return recordDTO;
    }
}
