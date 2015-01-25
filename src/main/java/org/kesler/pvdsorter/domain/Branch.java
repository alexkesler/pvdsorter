package org.kesler.pvdsorter.domain;

import java.util.ArrayList;
import java.util.List;

public class Branch {

    private Long id;
    private String uuid;
    private String name;
    private String code;
    private List<Record> records;
    private boolean all = false;


    public Branch() {
        records = new ArrayList<Record>();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public List<Record> getRecords() {
        return records;
    }

    public boolean isAll() { return all; }
    public void setAll(boolean all) { this.all = all; }

    public void addRecord(Record record) {
        if (!records.contains(record)) records.add(record);
    }

    public void clearRecords() { records.clear(); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Branch branch = (Branch) o;

        if (uuid != null ? !uuid.equals(branch.uuid) : branch.uuid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return uuid != null ? uuid.hashCode() : 0;
    }

    @Override
    public String toString() {
        return name;
    }
}
