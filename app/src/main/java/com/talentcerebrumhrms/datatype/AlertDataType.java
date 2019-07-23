package com.talentcerebrumhrms.datatype;

/**
 * Created by saransh on 03-11-2016.
 */

public class AlertDataType {

    private String read, sender_name, receiver_name, created_date, alert_type, flag, description, id, sender_image;

    public void setRead(String value) {
        this.read = value;
    }

    public void setSender_image(String value) {
        this.sender_image = value;
    }

    public void setSender_name(String value) {
        this.sender_name = value;
    }

    public void setReceiver_name(String value) {
        this.receiver_name = value;
    }

    public void setCreated_date(String value) {
        this.created_date = value;
    }

    public void setAlert_type(String value) {
        this.alert_type = value;
    }

    public void setFlag(String value) {
        this.flag = value;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    public void setId(String value) {
        this.id = value;
    }

    public String getRead() {
        return this.read;
    }

    public String getSender_name() {
        return this.sender_name;
    }

    public String getReceiver_name() {
        return this.receiver_name;
    }

    public String getCreated_date() {
        return this.created_date;
    }

    public String getAlert_type() {
        return this.alert_type;
    }

    public String getSender_image() {
        return this.sender_image;
    }

    public String getFlag() {
        return this.flag;
    }

    public String getDescription() {
        return this.description;
    }

    public String getId() {
        return this.id;
    }

}
