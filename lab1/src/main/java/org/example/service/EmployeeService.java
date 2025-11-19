package org.example.service;

import org.example.exception.DuplicateEmailException;
import org.example.exception.EmployeeNotFoundException;
import org.example.model.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


@Service
public class EmployeeService {
    private final Map<String, Employee> employees = new HashMap<>();

    private final Map<String, EmployeeDocument> documentsById = new ConcurrentHashMap<>();
    private final Map<String, List<String>> documentIdsByEmail = new ConcurrentHashMap<>();

    public EmployeeDocument addDocumentMetadata(EmployeeDocument doc) {
        documentsById.put(doc.getId(), doc);
        documentIdsByEmail.computeIfAbsent(doc.getEmployeeEmail(), k ->
                        Collections.synchronizedList(new ArrayList<>()))
                .add(doc.getId());
        return doc;
    }

    public List<EmployeeDocument> getDocumentsByEmail(String email) {
        List<String> ids = documentIdsByEmail.getOrDefault(email, Collections.emptyList());
        return ids.stream()
                .map(documentsById::get) // Pobierz obiekt dokumentu po ID
                .filter(Objects::nonNull) // Odfiltruj jeśli jakimś cudem null
                .collect(Collectors.toList());
    }

    public Optional<EmployeeDocument> getDocumentById(String documentId) {
        return Optional.ofNullable(documentsById.get(documentId));
    }

    public Optional<EmployeeDocument> deleteDocumentMetadata(String documentId) {
        EmployeeDocument doc = documentsById.remove(documentId);
        if (doc != null) {
            List<String> ids = documentIdsByEmail.get(doc.getEmployeeEmail());
            if (ids != null) {
                ids.remove(documentId);
            }
            return Optional.of(doc);
        }
        return Optional.empty();
    }


    public Employee addEmployee(Employee e) {
        if (e == null || e.getEmail() == null || e.getCompany() == null || e.getName() == null || e.getSurname() == null || e.getPosition() == null) {
            throw new IllegalArgumentException("Nieprawidłowe dane pracownika. Wszystkie pola są wymagane.");
        }

        if (employees.containsKey(e.getEmail().toLowerCase())) {
            throw new DuplicateEmailException("Pracownik z adresem e-mail '" + e.getEmail() + "' już istnieje.");
        }

        employees.put(e.getEmail().toLowerCase(), e);
        return e;
    }

    public List<Employee> displayEmployees() {
        return new ArrayList<>(employees.values());
    }

    public Optional<Employee> getEmployeeByEmail(String email) {
        return Optional.ofNullable(employees.get(email.toLowerCase()));
    }

    public Employee updateEmployee(String email, Employee updatedEmployeeData) {
        Employee existingEmployee = employees.get(email.toLowerCase());

        if (existingEmployee == null) {
            throw new EmployeeNotFoundException("Nie znaleziono pracownika z e-mailem: " + email);
        }

        existingEmployee.setName(updatedEmployeeData.getName());
        existingEmployee.setSurname(updatedEmployeeData.getSurname());
        existingEmployee.setCompany(updatedEmployeeData.getCompany());
        existingEmployee.setPosition(updatedEmployeeData.getPosition());
        existingEmployee.setSalary(updatedEmployeeData.getSalary());
        existingEmployee.setStatus(updatedEmployeeData.getStatus());

        employees.put(email.toLowerCase(), existingEmployee);
        return existingEmployee;
    }

    public boolean deleteEmployee(String email) {
        Employee removed = employees.remove(email.toLowerCase());
        return removed != null;
    }

    public Optional<Employee> updateEmployeeStatus(String email, EmploymentStatus status) {
        Employee employee = employees.get(email.toLowerCase());
        if (employee != null) {
            employee.setStatus(status);
            employees.put(email.toLowerCase(), employee);
            return Optional.of(employee);
        }
        return Optional.empty();
    }

    public List<Employee> getEmployeesByStatus(EmploymentStatus status) {
        return employees.values().stream()
                .filter(e -> e.getStatus() == status)
                .collect(Collectors.toList());
    }

    public Map<EmploymentStatus, Long> countByStatus() {
        return employees.values().stream()
                .collect(Collectors.groupingBy(
                        Employee::getStatus,
                        Collectors.counting()
                ));
    }


    public List<Employee> searchByCompany(String companyName) {
        return employees.values().stream()
                .filter(e -> Objects.equals(e.getCompany(), companyName))
                .collect(Collectors.toList());
    }

    public List<Employee> groupBySurname() {
        return this.employees.values().stream()
                .sorted(Comparator.comparing(Employee::getSurname)
                        .thenComparing(Employee::getName))
                .collect(Collectors.toList());
    }

    public Map<POSITION, List<Employee>> groupByPosition() {
        return this.employees.values().stream()
                .collect(Collectors.groupingBy(
                        Employee::getPosition
                ));
    }

    public Map<POSITION, Long> countByPosition() {
        return this.employees.values().stream()
                .collect(Collectors.groupingBy(
                        Employee::getPosition,
                        Collectors.counting()
                ));
    }

    public double calcAvgSalary() {
        return this.employees.values().stream()
                .mapToDouble(Employee::getSalary)
                .average()
                .orElse(0.0);
    }

    public double calcAvgSalary(String company) {
        return this.employees.values().stream()
                .filter(e -> Objects.equals(e.getCompany(), company))
                .mapToDouble(Employee::getSalary)
                .average()
                .orElse(0.0);
    }

    public Optional<Employee> findEmployeeWithHighestSalary() {
        return this.employees.values().stream()
                .max(Comparator.comparingDouble(Employee::getSalary));
    }

    public Optional<Employee> findEmployeeWithHighestSalary(String company) {
        return this.employees.values().stream()
                .filter(e -> Objects.equals(e.getCompany(), company))
                .max(Comparator.comparingDouble(Employee::getSalary));
    }
    public List<Employee> validateSalaryConsistency(){
        return this.employees.values().stream()
                .filter(e -> e.getSalary() < e.getPosition().getBaseSalary())
                .collect(Collectors.toList());
    }
    public Map<String, CompanyStatistics> getCompanyStatistics() {
        return employees.values().stream()
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