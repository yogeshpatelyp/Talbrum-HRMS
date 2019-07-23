package com.talentcerebrumhrms.attandanceoffline;

/**
 * Created by Belal on 1/27/2017.
 */

public class Name {
    private String attandancestatus;
    private String address;
    private String lat;
    private String lon;
    private String time;
    private byte[] image;
    private int status;

    public Name(String attandancestatus,String address,String lat,String lon,String time,byte[] image, int status) {
        this.attandancestatus = attandancestatus;
        this.address = address;
        this.lat = lat;
        this.lon = lon;
        this.time = time;
        this.image = image;
        this.status = status;
    }

    public String getAttandancestatus() {
        return attandancestatus;
    }

    public String getAddress() {
        return address;
    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }

    public String getTime() {
        return time;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public int getStatus() {
        return status;
    }


}
