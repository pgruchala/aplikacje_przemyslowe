package org.example.model;

public class CompanyStatistics {
    private final long employeeCount;
    private final Double avgSalary;
    private final String highestPaid;

    public CompanyStatistics(long employeeCount, Double avgSalary, String highestPaid) {
        this.employeeCount = employeeCount;
        this.avgSalary = avgSalary;
        this.highestPaid = highestPaid;
    }

    public Double getAvgSalary() {
        return avgSalary;
    }

    public long getEmployeeCount() {
        return employeeCount;
    }

    public String getHighestPaid() {
        return highestPaid;
    }
    @Override
    public String toString() {
        return String.format("CompanyStatistics{employeeCount=%d, averageSalary=%.2f, highestPaidEmployee='%s'}",
                employeeCount, avgSalary, highestPaid);
    }
}
