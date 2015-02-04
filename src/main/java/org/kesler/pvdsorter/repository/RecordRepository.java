package org.kesler.pvdsorter.repository;

import org.kesler.pvdsorter.domain.Record;

import java.util.Collection;


public interface RecordRepository {
    public void addRecord(Record record);
    public void removeRecord(Record record);
    public void setMainRecord(Record record, Record mainRecord);
    public Collection<Record> getAllRecords();

}
