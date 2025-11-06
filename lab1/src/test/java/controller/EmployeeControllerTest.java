package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.config.EmployeeMapper;
import org.example.controller.EmployeeController;
import org.example.dto.EmployeeDTO;
import org.example.exception.DuplicateEmailException;
import org.example.exception.EmployeeNotFoundException;
import org.example.model.Employee;
import org.example.model.EmploymentStatus;
import org.example.model.POSITION;
import org.example.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
public class EmployeeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmployeeService employeeService;

    @MockBean
    private EmployeeMapper mapper;

    private Employee employee;
    private EmployeeDTO employeeDTO;
    @BeforeEach
    void setUp() {
        employee = new Employee("Jan", "Kowalski", "jan@example.com", "TechCorp", POSITION.PROGRAMISTA, 8000);

        employeeDTO = new EmployeeDTO();
        employeeDTO.setFirstName("Jan");
        employeeDTO.setLastName("Kowalski");
        employeeDTO.setEmail("jan@example.com");
        employeeDTO.setCompany("TechCorp");
        employeeDTO.setPosition(POSITION.PROGRAMISTA);
        employeeDTO.setSalary(8000);
        employeeDTO.setStatus(EmploymentStatus.ACTIVE);

        when(mapper.toDto(any(Employee.class))).thenReturn(employeeDTO);
        when(mapper.toEntity(any(EmployeeDTO.class))).thenReturn(employee);
    }
    @Test
    void testGetAllEmployees() throws Exception {
        when(employeeService.displayEmployees()).thenReturn(List.of(employee));

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].email").value("jan@example.com"))
                .andExpect(jsonPath("$[0].firstName").value("Jan"));
    }

    @Test
    void testGetEmployeesByCompany() throws Exception {
        when(employeeService.searchByCompany("TechCorp")).thenReturn(List.of(employee));

        mockMvc.perform(get("/api/employees").param("company", "TechCorp"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].company").value("TechCorp"));
    }

    @Test
    void testGetEmployeeByEmail_Success() throws Exception {
        when(employeeService.getEmployeeByEmail("jan@example.com")).thenReturn(Optional.of(employee));

        mockMvc.perform(get("/api/employees/{email}", "jan@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("jan@example.com"));
    }

    @Test
    void testGetEmployeeByEmail_NotFound() throws Exception {
        when(employeeService.getEmployeeByEmail(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/employees/{email}", "bad@example.com"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Nie znaleziono pracownika z e-mailem: bad@example.com"));
    }

    @Test
    void testCreateEmployee_Success() throws Exception {
        when(employeeService.addEmployee(any(Employee.class))).thenReturn(employee);
        String employeeJson = objectMapper.writeValueAsString(employeeDTO);

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(employeeJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("jan@example.com"))
                .andExpect(header().string("Location", "http://localhost/api/employees/jan@example.com"));
    }

    @Test
    void testCreateEmployee_DuplicateEmail() throws Exception {
        when(employeeService.addEmployee(any(Employee.class)))
                .thenThrow(new DuplicateEmailException("Email już istnieje"));
        String employeeJson = objectMapper.writeValueAsString(employeeDTO);

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(employeeJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email już istnieje"));
    }

    @Test
    void testDeleteEmployee_Success() throws Exception {
        when(employeeService.deleteEmployee("jan@example.com")).thenReturn(true);

        mockMvc.perform(delete("/api/employees/{email}", "jan@example.com"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteEmployee_NotFound() throws Exception {
        when(employeeService.deleteEmployee("bad@example.com")).thenReturn(false);

        mockMvc.perform(delete("/api/employees/{email}", "bad@example.com"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testPatchEmployeeStatus() throws Exception {
        // Given
        Employee updatedEmployee = new Employee("Jan", "Kowalski", "jan@example.com", "TechCorp", POSITION.PROGRAMISTA, 8000);
        updatedEmployee.setStatus(EmploymentStatus.ON_LEAVE);

        EmployeeDTO updatedDto = new EmployeeDTO();
        updatedDto.setFirstName("Jan");
        updatedDto.setLastName("Kowalski");
        updatedDto.setEmail("jan@example.com");
        updatedDto.setCompany("TechCorp");
        updatedDto.setPosition(POSITION.PROGRAMISTA);
        updatedDto.setSalary(8000);
        updatedDto.setStatus(EmploymentStatus.ON_LEAVE);

        Map<String, String> requestBody = Map.of("status", "ON_LEAVE");
        String statusJson = objectMapper.writeValueAsString(requestBody);

        when(employeeService.updateEmployeeStatus(eq("jan@example.com"), eq(EmploymentStatus.ON_LEAVE)))
                .thenReturn(Optional.of(updatedEmployee));
        when(mapper.toDto(updatedEmployee)).thenReturn(updatedDto);

        mockMvc.perform(patch("/api/employees/{email}/status", "jan@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(statusJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ON_LEAVE"));
    }
}
