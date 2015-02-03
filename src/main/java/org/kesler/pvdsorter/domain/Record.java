package org.kesler.pvdsorter.domain;

import java.text.SimpleDateFormat;
import java.util.*;

public class Record {

    private Long id;

    private Branch branch;
    private String regnum;
    private Date regdate;
    private String mainRegnum;
    private Record mainRecord;
    private List<Record> subRecords = new ArrayList<Record>();

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Branch getBranch() { return branch; }
    public void setBranch(Branch branch) { this.branch = branch; }

    public String getRegnum() { return regnum; }
    public void setRegnum(String regnum) { this.regnum = regnum; }

    public Date getRegdate() { return regdate; }
    public void setRegdate(Date regdate) { this.regdate = regdate; }
    public String getRegdateString() {
        return simpleDateFormat.format(regdate);
    }

    public String getMainRegnum() { return mainRegnum; }
    public void setMainRegnum(String mainRegnum) { this.mainRegnum = mainRegnum; }

    public Record getMainRecord() { return mainRecord; }
    public void setMainRecord(Record mainRecord) { this.mainRecord = mainRecord; }

    public List<Record> getSubRecords() { return subRecords; }

    public void addSubRecord(Record record) {
        if (!subRecords.contains(record)) {
            subRecords.add(record);
        }
    }

    @Override
    public String toString() {

        return regnum + " от " + simpleDateFormat.format(regdate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Record record = (Record) o;

        if (regdate != null ? !regdate.equals(record.regdate) : record.regdate != null) return false;
        if (regnum != null ? !regnum.equals(record.regnum) : record.regnum != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = regnum != null ? regnum.hashCode() : 0;
        result = 31 * result + (regdate != null ? regdate.hashCode() : 0);
        return result;
    }
}
