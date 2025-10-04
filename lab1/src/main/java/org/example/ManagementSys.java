package org.example;

import java.util.*;
import java.util.stream.Collectors;

public class ManagementSys {
    private Map<String, Employee> employees;

    public ManagementSys(){
        this.employees = new HashMap<>();
    }

    public boolean addEmployee(Employee e){
        if (e == null || e.getEmail() == null) {
            System.out.println("Invalid employee data");
        }
        for (String mail : this.employees.keySet()) {
            if (Objects.equals(e.getEmail(), mail)){
                System.out.println("Email has already been registered in system");
                return false;
            }
        }
        this.employees.put(e.getEmail(),e);
        System.out.println("Employee added");
        return true;
    }

    public void displayEmployees(){
        if (employees.isEmpty()){
            System.out.println("No employees registered");
        }
        System.out.println("====Employees listed:=====");
        for (Employee e : employees.values()){
            System.out.println(e.toString());
        }
    }

    public List<Employee> searchByCompany(String companyName){
        return employees.values().stream().filter(e -> Objects.equals(e.getCompany(), companyName))
                .collect(Collectors.toList());
    }

    public List<Employee> groupBySurname() {
        return this.employees.values().stream().sorted(Comparator.comparing(Employee::getSurname)
                        .thenComparing(Employee::getName))
                .collect(Collectors.toList());
    }

    public Map<POSITION, List<Employee>> groupByPosition(){
        return this.employees.values().stream()
                .collect(Collectors.groupingBy(
                        Employee::getPosition
                ));
    }
    public Map<POSITION,Long> countByPosition(){
        return this.employees.values().stream()
                .collect(Collectors.groupingBy(
                        Employee::getPosition,
                        Collectors.counting()
                ));
    }

    public double calcAvgSalary(){
        return this.employees.values().stream()
                .mapToDouble(Employee::getSalary)
                .average()
                .orElse(0.0);
    }

    public Optional<Employee> findEmployeeWithHighestSalary(){
        return this.employees.values().stream()
                .max(Comparator.comparingDouble(Employee::getSalary));
    }

}
