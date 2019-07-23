package com.talentcerebrumhrms.attandanceoffline;

/**
 * Created by Belal on 1/27/2017.
 */

public class OutTime {

    private String address;
    private String lat;
    private String lon;
    private String time;

    private int status;

    public OutTime(String address,String lat,String lon,String time, int status) {

        this.address = address;
        this.lat = lat;
        this.lon = lon;
        this.time = time;

        this.status = status;
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

    public int getStatus() {
        return status;
    }


}
