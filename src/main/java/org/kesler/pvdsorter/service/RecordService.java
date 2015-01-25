package org.kesler.pvdsorter.service;

import org.kesler.pvdsorter.domain.Record;

import java.util.Collection;
import java.util.List;

/**
 * Created by alex on 25.01.15.
 */
public interface RecordService {
    public Collection<Record> getRecordsByCode(String code);
}
