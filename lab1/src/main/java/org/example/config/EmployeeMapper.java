package org.example.config;

import org.example.dto.EmployeeDTO;
import org.example.model.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {
    public EmployeeDTO toDto(Employee employee){
        return new EmployeeDTO(
                employee.getName(),
                employee.getSurname(),
                employee.getEmail(),
                employee.getCompany(),
                employee.getPosition(),
                employee.getSalary(),
                employee.getStatus()
        );
    }
    public Employee toEntity(EmployeeDTO dto) {
        Employee employee = new Employee(
                dto.getFirstName(),
                dto.getLastName(),
                dto.getEmail(),
                dto.getCompany(),
                dto.getPosition()
        );
        employee.setSalary(dto.getSalary());
        employee.setStatus(dto.getStatus());
        return employee;
    }
}
