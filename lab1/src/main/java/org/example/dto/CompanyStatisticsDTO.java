package org.example.dto;

public class CompanyStatisticsDTO {
    private String companyName;
    private long employeeCount;
    private Double averageSalary;
    private Double highestSalary;
    private String topEarnerName;

    public CompanyStatisticsDTO() {
    }

    public CompanyStatisticsDTO(String companyName, long employeeCount, Double averageSalary, Double highestSalary, String topEarnerName) {
        this.companyName = companyName;
        this.employeeCount = employeeCount;
        this.averageSalary = averageSalary;
        this.highestSalary = highestSalary;
        this.topEarnerName = topEarnerName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public long getEmployeeCount() {
        return employeeCount;
    }

    public void setEmployeeCount(long employeeCount) {
        this.employeeCount = employeeCount;
    }

    public Double getAverageSalary() {
        return averageSalary;
    }

    public void setAverageSalary(Double averageSalary) {
        this.averageSalary = averageSalary;
    }

    public Double getHighestSalary() {
        return highestSalary;
    }

    public void setHighestSalary(Double highestSalary) {
        this.highestSalary = highestSalary;
    }

    public String getTopEarnerName() {
        return topEarnerName;
    }

    public void setTopEarnerName(String topEarnerName) {
        this.topEarnerName = topEarnerName;
    }
}