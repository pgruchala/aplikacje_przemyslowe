package org.example.service;

import org.example.model.CompanyStatistics;
import org.example.model.Employee;
import org.example.model.POSITION;

import java.util.*;
import java.util.stream.Collectors;

public class EmployeeService {
    private final List<Employee> employees = new ArrayList<>();// lista hashująca?

    public boolean addEmployee(Employee e){
        if (e == null || e.getEmail() == null || e.getCompany() == null || e.getName() == null || e.getSurname() == null || e.getPosition() == null) {
            System.out.println("Invalid employee data");
        }
        boolean emailExist = employees.stream()
                        .anyMatch(employee -> e.getEmail().equalsIgnoreCase(employee.getEmail()));
        if (emailExist) {
            System.out.println("nie mozna dodać pracownika ponieważ e mail został już użyty");
            return false;
        } else{
            employees.add(e);
        }
        return true;
    }

    public List<Employee> displayEmployees(){
        return new ArrayList<>(employees);
    }

    public List<Employee> searchByCompany(String companyName){
        return employees.stream().filter(e -> Objects.equals(e.getCompany(), companyName))
                .collect(Collectors.toList());
    }

    public List<Employee> groupBySurname() {
        return this.employees.stream().sorted(Comparator.comparing(Employee::getSurname)
                        .thenComparing(Employee::getName))
                .collect(Collectors.toList());
    }

    public Map<POSITION, List<Employee>> groupByPosition(){
        return this.employees.stream()
                .collect(Collectors.groupingBy(
                        Employee::getPosition
                ));
    }
    public Map<POSITION,Long> countByPosition(){
        return this.employees.stream()
                .collect(Collectors.groupingBy(
                        Employee::getPosition,
                        Collectors.counting()
                ));
    }

    public double calcAvgSalary(){
        return this.employees.stream()
                .mapToDouble(Employee::getSalary)
                .average()
                .orElse(0.0);
    }
    public double calcAvgSalary(String company){
        return this.employees.stream().filter(e -> Objects.equals(e.getCompany(), company))
                .mapToDouble(Employee::getSalary)
                .average()
                .orElse(0.0);
    }

    public Optional<Employee> findEmployeeWithHighestSalary(){
        return this.employees.stream()
                .max(Comparator.comparingDouble(Employee::getSalary));
    }
    public Optional<Employee> findEmployeeWithHighestSalary(String company){
        return this.employees.stream()
                .filter(e -> Objects.equals(e.getCompany(), company))
                .max(Comparator.comparingDouble(Employee::getSalary));
    }
    public List<Employee> validateSalaryConsistency(){
        return this.employees.stream()
                .filter(e -> e.getSalary() < e.getPosition().getBaseSalary())
                .collect(Collectors.toList());
    }
    public Map<String, CompanyStatistics> getCompanyStatistics() {
        return employees.stream()
                .collect(Collectors.groupingBy(
                        Employee::getCompany,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                empList -> {
                                    long count = empList.size();
                                    double avgSalary = empList.stream()
                                            .mapToDouble(Employee::getSalary)
                                            .average()
                                            .orElse(0.0);
                                    String highestPaid = empList.stream()
                                            .max(Comparator.comparingDouble(Employee::getSalary))
                                            .map(e -> e.getName() + " " + e.getSurname())
                                            .orElse("N/A");
                                    return new CompanyStatistics(count, avgSalary, highestPaid);
                                }
                        )
                ));
    }

}
