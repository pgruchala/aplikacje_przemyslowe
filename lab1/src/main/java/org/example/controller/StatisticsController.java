package org.example.controller;


import org.example.dto.CompanyStatisticsDTO;
import org.example.exception.EmployeeNotFoundException;
import org.example.model.CompanyStatistics;
import org.example.model.Employee;
import org.example.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {
    private final EmployeeService employeeService;

    public StatisticsController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/salary/average")
    public ResponseEntity<Map<String, Double>> getAverageSalary(
            @RequestParam(required = false) String company) {

        double avgSalary;
        if (company != null && !company.isEmpty()) {
            avgSalary = employeeService.calcAvgSalary(company);
        } else {
            avgSalary = employeeService.calcAvgSalary();
        }

        return ResponseEntity.ok(Map.of("averageSalary", avgSalary));
    }
    private Double findHighestSalaryInCompany(String companyName) {
        return employeeService.findEmployeeWithHighestSalary(companyName)
                .map(Employee::getSalary)
                .orElse(0.0);
    }
    @GetMapping("/company/{companyName}")
    public ResponseEntity<CompanyStatisticsDTO> getCompanyStatistics(@PathVariable String companyName) {
        CompanyStatistics stats = employeeService.getCompanyStatistics().get(companyName);

        if (stats == null) {
            throw new EmployeeNotFoundException("Nie znaleziono statystyk dla firmy: " + companyName);
        }

        CompanyStatisticsDTO dto = new CompanyStatisticsDTO(
                companyName,
                stats.getEmployeeCount(),
                stats.getAvgSalary(),
                findHighestSalaryInCompany(companyName),
                stats.getHighestPaid()
        );

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/positions")
    public ResponseEntity<Map<String, Long>> getPositionCounts() {
        Map<String, Long> positionCounts = employeeService.countByPosition().entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().getName(),
                        Map.Entry::getValue
                ));
        return ResponseEntity.ok(positionCounts);
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Long>> getStatusCounts() {
        Map<String, Long> statusCounts = employeeService.countByStatus().entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().name(),
                        Map.Entry::getValue
                ));
        return ResponseEntity.ok(statusCounts);
    }
}
