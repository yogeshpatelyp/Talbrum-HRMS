package com.talentcerebrumhrms.datatype;

/**
 * Created by saransh on 04-11-2016.
 */

public class AttendanceFragmentDataType {

    private String startMonth = "00", monthlyleaveDays = "-", monthlyabsentDays = "-", monthlypresentDays = "-";
    private String allLeaveDays = "-", allAbsentDays = "-", allWorkingDays = "-";
    private String inTime = "-", outTime = "-", salaryRange = "";
    private String income_tax = "", professional_tax = "", total_deduction = "", net_credit = "";

    public void setIncome_tax(String value) {
        this.income_tax = value;
    }

    public void setProfessional_tax(String value) {
        this.professional_tax = value;
    }

    public void setSalaryRange(String value) {
        this.salaryRange = value;
    }

    public void setTotal_deduction(String value) {
        this.total_deduction = value;
    }

    public void setNet_credit(String value) {
        this.net_credit = value;
    }

    public void setAllLeaveDays(String value) {
        this.allLeaveDays = value;
    }

    public void setAllAbsentDays(String value) {
        this.allAbsentDays = value;
    }

    public void setMonthlyLeaveDays(String value) {
        this.monthlyleaveDays = value;
    }

    public void setMonthlyAbsentDays(String value) {
        this.monthlyabsentDays = value;
    }

    public void setMonthlyPresentDays(String value) {
        this.monthlypresentDays = value;
    }

    public void setAllWorkingDays(String value) {
        this.allWorkingDays = value;
    }

    public void setInTime(String value) {
        this.inTime = value;
    }

    public void setOutTime(String value) {
        this.outTime = value;
    }

    public String getAllLeaveDays() {
        return this.allLeaveDays;
    }

    public String getAllAbsentDays() {
        return this.allAbsentDays;
    }

    public String getMonthlyLeaveDays() {
        return this.monthlyleaveDays;
    }

    public String getMonthlyAbsentDays() {
        return this.monthlyabsentDays;
    }

    public String getMonthlyPresentDays() {
        return this.monthlypresentDays;
    }

    public String getAllWorkingDays() {
        return this.allWorkingDays;
    }

    public String getInTime() {
        return this.inTime;
    }

    public String getOutTime() {
        return this.outTime;
    }

    public String getSalaryRange() {
        return this.salaryRange;
    }

    public String getIncome_tax() {
        return this.income_tax;
    }

    public String getProfessional_tax() {
        return this.professional_tax;
    }

    public String getTotal_deduction() {
        return this.total_deduction;
    }

    public String getNet_credit() {
        return this.net_credit;
    }

    public void setStartMonth(String value) {
        this.startMonth = value;
    }

    public String getStartMonth() {
        return this.startMonth;
    }
}
