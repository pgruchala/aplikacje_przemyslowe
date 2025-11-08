package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.config.EmployeeMapper;
import org.example.controller.StatisticsController;
import org.example.model.CompanyStatistics;
import org.example.model.Employee;
import org.example.model.EmploymentStatus;
import org.example.model.POSITION;
import org.example.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StatisticsController.class)
public class StatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;
    @MockBean
    private EmployeeMapper mapper;

    @Test
    void testGetAverageSalary_Overall() throws Exception {
        when(employeeService.calcAvgSalary()).thenReturn(15000.0);

        mockMvc.perform(get("/api/statistics/salary/average"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageSalary").value(15000.0));
    }

    @Test
    void testGetAverageSalary_ByCompany() throws Exception {
        when(employeeService.calcAvgSalary("TechCorp")).thenReturn(12000.0);

        mockMvc.perform(get("/api/statistics/salary/average").param("company", "TechCorp"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageSalary").value(12000.0));
    }

    @Test
    void testGetCompanyStatistics_Success() throws Exception {
        CompanyStatistics stats = new CompanyStatistics(2, 10000.0, "Jan Kowalski");
        when(employeeService.getCompanyStatistics()).thenReturn(Map.of("TechCorp", stats));

        Employee highestPaid = new Employee("Jan", "Kowalski", "jk@mail.com", "TechCorp", POSITION.MANAGER, 12000);
        when(employeeService.findEmployeeWithHighestSalary("TechCorp")).thenReturn(Optional.of(highestPaid));

        mockMvc.perform(get("/api/statistics/company/{companyName}", "TechCorp"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyName").value("TechCorp"))
                .andExpect(jsonPath("$.employeeCount").value(2))
                .andExpect(jsonPath("$.averageSalary").value(10000.0))
                .andExpect(jsonPath("$.highestSalary").value(12000.0))
                .andExpect(jsonPath("$.topEarnerName").value("Jan Kowalski"));
    }

    @Test
    void testGetCompanyStatistics_NotFound() throws Exception {
        when(employeeService.getCompanyStatistics()).thenReturn(Collections.emptyMap());

        mockMvc.perform(get("/api/statistics/company/{companyName}", "FakeCorp"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Nie znaleziono statystyk dla firmy: FakeCorp"));
    }

    @Test
    void testGetPositionCounts() throws Exception {
        Map<POSITION, Long> positionMap = Map.of(
                POSITION.PROGRAMISTA, 5L,
                POSITION.MANAGER, 2L
        );
        when(employeeService.countByPosition()).thenReturn(positionMap);

        mockMvc.perform(get("/api/statistics/positions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Programista").value(5))
                .andExpect(jsonPath("$.Manager").value(2));
    }

    @Test
    void testGetStatusCounts() throws Exception {
        Map<EmploymentStatus, Long> statusMap = Map.of(
                EmploymentStatus.ACTIVE, 3L,
                EmploymentStatus.ON_LEAVE, 1L
        );
        when(employeeService.countByStatus()).thenReturn(statusMap);

        mockMvc.perform(get("/api/statistics/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ACTIVE").value(3))
                .andExpect(jsonPath("$.ON_LEAVE").value(1));
    }
}