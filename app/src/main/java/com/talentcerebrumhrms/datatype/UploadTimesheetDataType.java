package com.talentcerebrumhrms.datatype;

/**
 * Created by Harshit on 20-Jun-17.
 */

public class UploadTimesheetDataType {

    private String working_date, working_status, working_hour, leave_type_id, remark;

    public void setWorkingDate(String value) {
        this.working_date = value;
    }

    public void setWorkingStatus(String value) {
        this.working_status = value;
    }

    public void setWorkingHour(String value) {
        this.working_hour = value;
    }

    public void setLeaveTypeId(String value) {
        this.leave_type_id = value;
    }

    public void setRemark(String value) {
        this.remark = value;
    }

    public String getWorkingDate() {
        return working_date;
    }

    public String getWorkingStatus() {
        return working_status;
    }

    public String getWorkingHour() {
        return working_hour;
    }

    public String getLeaveTypeId() {
        return leave_type_id;
    }

    public String getRemark() {
        return remark;
    }

}
