package com.talentcerebrumhrms.datatype;

/**
 * Created by saransh on 05-11-2016.
 */

public class LeaveAppliedDataType {
    String from_date, to_date, number_of_days, applied_on, status, action_taken_by, action_taken_on;


    public void setFrom_date(String value) {
        this.from_date = value;
    }

    public void setTo_date(String value) {
        this.to_date = value;
    }

    public void setNumber_of_days(String value) {
        this.number_of_days = value;
    }

    public void setApplied_on(String value) {
        this.applied_on = value;
    }

    public void setStatus(String value) {
        this.status = value;
    }

    public void setAction_taken_by(String value) {
        this.action_taken_by = value;
    }

    public void setAction_taken_on(String value) {
        this.action_taken_on = value;
    }

    public String getFrom_date() {
        return from_date;
    }

    public String getTo_date() {
        return to_date;
    }

    public String getNumber_of_days() {
        return number_of_days;
    }

    public String getApplied_on() {
        return applied_on;
    }

    public String getStatus() {
        return status;
    }

    public String getAction_taken_by() {
        return action_taken_by;
    }

    public String getAction_taken_on() {
        return action_taken_on;
    }
}
