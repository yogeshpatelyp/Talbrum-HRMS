package com.talentcerebrumhrms.datatype;

/**
 * Created by saransh on 22-10-2016.
 */

public class RembursementDatatype {
    private String amount = "-", details = "-", date = "-", action = "-", bill_no = "-", issued_by = "-", authorised_by = "-";


    public void setAmount(String amouunt) {
        this.amount = amouunt;
    }

    public void setDetails(String value) {
        this.details = value;
    }

    public void setDate(String value) {
        this.date = value;
    }

    public void setAction(String value) {
        this.action = value;
    }

    public void setBill_no(String value) {
        this.bill_no = value;
    }

    public void setIssued_by(String value) {
        this.issued_by = value;
    }

    public void setAuthorised_by(String value) {
        this.authorised_by = value;
    }

    public String getAmount() {
        return amount;
    }

    public String getDetails() {
        return details;
    }

    public String getDate() {
        return date;
    }

    public String getAction() {
        return action;
    }

    public String getBill_no() {
        return bill_no;
    }

    public String getIssued_by() {
        return issued_by;
    }

    public String getAuthorised_by() {
        return authorised_by;
    }


}

