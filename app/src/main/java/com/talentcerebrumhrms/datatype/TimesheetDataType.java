package com.talentcerebrumhrms.datatype;

/**
 * Created by Harshit on 15-Jun-17.
 */

public class TimesheetDataType {

    private String id, from_date, to_date, submitted_date, total_days, signed_by, signed_on;

    public void setId(String value) {
        this.id = value;
    }

    public void setFromDate(String value) {
        this.from_date = value;
    }

    public void setToDate(String value) {
        this.to_date = value;
    }

    public void setSubmittedDate(String value) {
        this.submitted_date = value;
    }

    public void setTotalDays(String value) {
        this.total_days = value;
    }

    public void setSignedBy(String value) {
        this.signed_by = value;
    }

    public void setSignedOn(String value) {
        this.signed_on = value;
    }

    public String getId() {
        return id;
    }

    public String getFromDate() {
        return from_date;
    }

    public String getToDate() {
        return to_date;
    }

    public String getSubmittedDate() {
        return submitted_date;
    }

    public String getTotalDays() {
        return total_days;
    }

    public String getSignedBy() {
        return signed_by;
    }

    public String getSignedOn() {
        return signed_on;
    }

}
