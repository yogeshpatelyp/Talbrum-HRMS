package com.talentcerebrumhrms.datatype;

/**
 * Created by saransh on 24-10-2016.
 */

public class LeavesDatatype {

    private String eligible, used, type_of_leave, remaining, admissible, id;

    public void setEligible(String value) {
        this.eligible = value;
    }

    public void setId(String value) {
        this.id = value;
    }

    public void setUsed(String value) {
        this.used = value;
    }

    public void setType_of_leave(String value) {
        this.type_of_leave = value;
    }

    public void setRemaining(String value) {
        this.remaining = value;
    }

    public void setAdmissible(String value) {
        this.admissible = value;
    }

    public String getEligible() {
        return eligible;
    }

    public String getUsed() {
        return used;
    }

    public String getType_of_leave() {
        return type_of_leave;
    }

    public String getRemaining() {
        return remaining;
    }

    public String getAdmissible() {
        return admissible;
    }

    public String getId() {
        return id;
    }

}
