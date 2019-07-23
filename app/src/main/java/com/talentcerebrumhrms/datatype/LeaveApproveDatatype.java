package com.talentcerebrumhrms.datatype;

/**
 * Created by saransh on 29-10-2016.
 */

public class LeaveApproveDatatype {

    private String leave_id, name, date_from, date_to, reason, profile;

    public void setLeave_id(String value) {
        this.leave_id = value;
    }

    public void setName(String value) {
        this.name = value;
    }

    public void setProfile(String value) {
        this.profile = value;
    }

    public void setDate_from(String value) {
        this.date_from = value;
    }

    public void setDate_to(String value) {
        this.date_to = value;
    }

    public void setReason(String value) {
        this.reason = value;
    }

    public String getLeave_id() {
        return leave_id;
    }

    public String getName() {
        return name;
    }

    public String getDate_from() {
        return date_from;
    }

    public String getDate_to() {
        return date_to;
    }

    public String getReason() {
        return reason;
    }

    public String getProfile() {
        return profile;
    }
}
