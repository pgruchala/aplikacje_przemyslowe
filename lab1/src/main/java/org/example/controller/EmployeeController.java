package org.example.controller;


import org.example.config.EmployeeMapper;
import org.example.dto.EmployeeDTO;
import org.example.exception.EmployeeNotFoundException;
import org.example.model.Employee;
import org.example.model.EmploymentStatus;
import org.example.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    private final EmployeeService employeeService;
    private final EmployeeMapper mapper;

    public EmployeeController(EmployeeService employeeService, EmployeeMapper mapper) {
        this.employeeService = employeeService;
        this.mapper = mapper;
    }
    //get employees
    @GetMapping
    public ResponseEntity<List<EmployeeDTO>> getEmployees(
            @RequestParam(required = false) String company) {

        List<Employee> employees;
        if (company != null && !company.isEmpty()) {
            employees = employeeService.searchByCompany(company);
        } else {
            employees = employeeService.displayEmployees();
        }

        List<EmployeeDTO> dtoList = employees.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/{email}")
    public ResponseEntity<EmployeeDTO> getEmployeeByEmail(@PathVariable String email) {
        return employeeService.getEmployeeByEmail(email)
                .map(mapper::toDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EmployeeNotFoundException("Nie znaleziono pracownika z e-mailem: " + email));
    }
    @PostMapping
    public ResponseEntity<EmployeeDTO> createEmployee(@RequestBody EmployeeDTO employeeDTO) {
        Employee employee = mapper.toEntity(employeeDTO);
        Employee createdEmployee = employeeService.addEmployee(employee);

        EmployeeDTO createdDto = mapper.toDto(createdEmployee);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{email}")
                .buildAndExpand(createdEmployee.getEmail())
                .toUri();

        return ResponseEntity.created(location).body(createdDto);
    }

    @PutMapping("/{email}")
    public ResponseEntity<EmployeeDTO> updateEmployee(@PathVariable String email, @RequestBody EmployeeDTO employeeDTO) {
        Employee employeeData = mapper.toEntity(employeeDTO);
        Employee updatedEmployee = employeeService.updateEmployee(email, employeeData);

        return ResponseEntity.ok(mapper.toDto(updatedEmployee));
    }
    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable String email) {
        boolean deleted = employeeService.deleteEmployee(email);
        if (!deleted) {
            throw new EmployeeNotFoundException("Nie znaleziono pracownika z e-mailem: " + email);
        }
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/{email}/status")
    public ResponseEntity<EmployeeDTO> updateEmployeeStatus(@PathVariable String email, @RequestBody EmployeeDTO partialDto) {
        if (partialDto.getStatus() == null) {
            throw new IllegalArgumentException("Pole 'status' jest wymagane w ciele żądania.");
        }

        return employeeService.updateEmployeeStatus(email, partialDto.getStatus())
                .map(mapper::toDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EmployeeNotFoundException("Nie znaleziono pracownika z e-mailem: " + email));
    }
    @GetMapping("/status/{status}")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesByStatus(@PathVariable EmploymentStatus status) {
        List<EmployeeDTO> dtoList = employeeService.getEmployeesByStatus(status).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }
}
