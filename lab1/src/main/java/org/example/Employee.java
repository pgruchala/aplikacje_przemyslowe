package org.example;

import java.util.Objects;

public class Employee {
    private String name;
    private String surname;
    private String email;
    private String company;
    private POSITION position;
    private double salary;

    public void Employee(String name, String surname, String email, String company, POSITION position){
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.company = company;
        this.position = position;
        this.salary = position.getBaseSalary();
    }

    public String getName() {
        return name;
    }

    public POSITION getPosition() {
        return position;
    }

    public String getCompany() {
        return company;
    }

    public String getEmail() {
        return email;
    }

    public String getSurname() {
        return surname;
    }

    public double getSalary() {
        return salary;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setPosition(POSITION position) {
        this.position = position;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }
    @Override
    public int hashCode(){
        return Objects.hash(email);
    }
    @Override
    public boolean equals(Object o){
        if (this==o) return true;
        if (o==null) return false;
        Employee employee = (Employee) o;
        return Objects.equals(email,employee.email);
    }
    @Override
    public String toString(){
        return String.format("Employee{name: '%s %s',\n email: '%s',\n company: '%s',\n position: '%s',\n salary: '%.2f'}",
                name,surname,email,company,position,salary);
    }
}
