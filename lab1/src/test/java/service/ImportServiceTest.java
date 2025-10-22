package service;

import org.example.model.Employee;
import org.example.model.ImportSummary;
import org.example.service.EmployeeService;
import org.example.service.ImportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ImportServiceTest {
    @Mock
    private EmployeeService mockEmployeeService;
    @InjectMocks
    private ImportService importService;

    private String csvHeader = "Name;Surname;Email;Company;Position;Salary\n";

    @Test
    void testImportFromReader_success() throws IOException {
        String csvData = csvHeader +
                "Jan;Kowalski;jk@mail.com;TestCorp;PROGRAMISTA;9000\n" +
                "Anna;Nowak;an@mail.com;TestCorp;MANAGER;13000\n";
        StringReader reader = new StringReader(csvData);

        when(mockEmployeeService.addEmployee(any(Employee.class))).thenReturn(true);

        ImportSummary summary = importService.importFromReader(reader);

        assertEquals(2, summary.getImported(), "Should import 2 employees");
        assertTrue(summary.getErrorList().isEmpty(), "Error list should be empty");

        verify(mockEmployeeService, times(2)).addEmployee(any(Employee.class));
    }
    @Test
    void testImportFromReader_handlesVariousErrors() throws IOException {
        String csvData = csvHeader +
                "Jan;Kowalski;jk@mail.com;TestCorp;PROGRAMISTA;9000\n" + // OK
                "Anna;Nowak;an@mail.com;TestCorp;DYREKTOR;15000\n" +      // Błędne stanowisko
                "Piotr;Zet;pz@mail.com;TestCorp;STAZYSTA;-100\n" +        // Ujemne wynagrodzenie
                "Maria;Lis;ml@mail.com;TestCorp;PREZES;25000;ZbednaKolumna\n" + // Zła liczba kolumn
                "Adam;Kot;ak@mail.com;TestCorp;WICEPREZES;18000\n";       // OK
        StringReader reader = new StringReader(csvData);

        when(mockEmployeeService.addEmployee(any(Employee.class))).thenReturn(true);

        ImportSummary summary = importService.importFromReader(reader);

        assertEquals(2, summary.getImported(), "Should import 2 valid employees");
        assertEquals(3, summary.getErrorList().size(), "Should report 3 errors");

        assertTrue(summary.getErrorList().get(0).contains("Nieprawidłowe stanowisko: DYREKTOR"), "Missing position error");
        assertTrue(summary.getErrorList().get(1).contains("Wynagrodzenie musi być dodatnie"), "Missing negative salary error");
        assertTrue(summary.getErrorList().get(2).contains("Nieprawidłowa liczba kolumn"), "Missing column count error");

        verify(mockEmployeeService, times(2)).addEmployee(any(Employee.class));
    }

    @Test
    void testImportFromReader_handlesDuplicateEmail() throws IOException {
        String csvData = csvHeader +
                "Jan;Kowalski;jk@mail.com;TestCorp;PROGRAMISTA;9000\n" +
                "Anna;Nowak;jk@mail.com;TestCorp;MANAGER;13000\n";
        StringReader reader = new StringReader(csvData);

        when(mockEmployeeService.addEmployee(any(Employee.class)))
                .thenReturn(true)
                .thenReturn(false);

        ImportSummary summary = importService.importFromReader(reader);

        assertEquals(1, summary.getImported(), "Should import 1 employee");
        assertEquals(1, summary.getErrorList().size(), "Should have 1 error");
        assertTrue(summary.getErrorList().get(0).contains("prawdopodobnie duplikat emaila"), "Missing duplicate email error");
        verify(mockEmployeeService, times(2)).addEmployee(any(Employee.class));
    }

    @Test
    void testImportFromCSV_success(@TempDir Path tempDir) throws Exception {
        String csvData = csvHeader + "Jan;Kowalski;jk@mail.com;TestCorp;PROGRAMISTA;9000\n";
        Path testFile = tempDir.resolve("test.csv");
        Files.write(testFile, csvData.getBytes());

        when(mockEmployeeService.addEmployee(any(Employee.class))).thenReturn(true);
        ImportSummary summary = importService.importFromCSV(testFile.toString());

        assertEquals(1, summary.getImported());
        assertTrue(summary.getErrorList().isEmpty());
        verify(mockEmployeeService, times(1)).addEmployee(any(Employee.class));
    }
}
