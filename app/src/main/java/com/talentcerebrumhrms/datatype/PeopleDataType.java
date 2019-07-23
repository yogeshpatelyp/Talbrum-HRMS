package com.talentcerebrumhrms.datatype;

import java.util.Comparator;

/**
 * Created by saransh on 14-11-2016.
 */

public class PeopleDataType {

    private String photo, name, email, phone, division, designation;

    public void setPhoto(String value) {
        this.photo = value;
    }

    public void setName(String value) {
        this.name = value;
    }

    public void setEmail(String value) {
        this.email = value;
    }

    public void setPhone(String value) {
        this.phone = value;
    }

    public void setDivision(String value) {
        this.division = value;
    }

    public void setDesignation(String value) {
        this.designation = value;
    }

    public String getPhoto() {
        return this.photo;
    }

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPhone() {
        return this.phone;
    }

    public String getDivision() {
        return this.division;
    }

    public String getDesignation() {
        return this.designation;
    }

    public static Comparator<PeopleDataType> listComparator = new Comparator<PeopleDataType>() {
        @Override
        public int compare(PeopleDataType jc1, PeopleDataType jc2) {
            return (int) (jc1.getName().compareTo(jc2.getName()));
        }
    };
}
