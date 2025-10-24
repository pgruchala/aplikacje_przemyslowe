package org.example;

import com.opencsv.exceptions.CsvException;
import org.example.exception.ApiException;
import org.example.exception.InvalidDataException;
import org.example.model.Employee;
import org.example.model.ImportSummary;
import org.example.model.POSITION;
import org.example.service.ApiService;
import org.example.service.EmployeeService;
import org.example.service.ImportService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

import java.io.IOException;
import java.util.List;

@SpringBootApplication
@ImportResource("classpath:employees-beans.xml")
public class EmployeeManagementApplication implements CommandLineRunner {
    private final EmployeeService employeeService;
    private final ImportService importService;
    private final ApiService apiService;
    private final List<Employee> xmlEmployees;
    private final String csvFilePath;

    public EmployeeManagementApplication(EmployeeService employeeService,
                                         ImportService importService,
                                         ApiService apiService,
                                         @Qualifier("xmlEmployees") List<Employee> xmlEmployees,
                                         @Value("${app.import.csv-file}") String csvFilePath) {
        this.employeeService = employeeService;
        this.importService = importService;
        this.apiService = apiService;
        this.xmlEmployees = xmlEmployees;
        this.csvFilePath = csvFilePath;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("--- START APLIKACJI ---");

        System.out.println("\n--- 1. Importowanie pracowników z CSV (" + csvFilePath + ") ---");
        try {
            ImportSummary csvSummary = importService.importFromCSV(csvFilePath);
            System.out.println(csvSummary);
        } catch (Exception e) {
            System.err.println("błąd podczas importu CSV: " + e.getMessage());
        }

        System.out.println("\n--- 2. Dodawanie pracowników z konfiguracji XML ---");
        for (Employee emp : xmlEmployees) {
            System.out.println("Dodawanie: " + emp.getName() + " " + emp.getSurname());
            if (!employeeService.addEmployee(emp)) {
                System.out.println("Błąd: Nie można dodać pracownika (prawdopodobnie duplikat emaila): " + emp.getEmail());
            }
        }
        System.out.println("Pomyślnie dodano " + xmlEmployees.size() + " pracowników z XML.");


        System.out.println("\n--- 3. Pobieranie i dodawanie pracowników z API ---");
        try {
            List<Employee> apiEmployees = apiService.fetchEmployeesFromAPI();
            int successCount = 0;
            for (Employee emp : apiEmployees) {
                if (employeeService.addEmployee(emp)) {
                    successCount++;
                } else {
                    System.out.println("Błąd: Nie można dodać pracownika z API (prawdopodobnie duplikat emaila): " + emp.getEmail());
                }
            }
            System.out.println("Pomyślnie pobrano i dodano " + successCount + " z " + apiEmployees.size() + " pracowników z API.");
        } catch (ApiException e) {
            System.err.println("Błąd podczas pobierania danych z API: " + e.getMessage());
        }

        String targetCompany = "TechCorp";
        System.out.println("\n--- 4. Statystyki dla firmy: " + targetCompany + " ---");
        employeeService.getCompanyStatistics().entrySet().stream()
                .filter(entry -> entry.getKey().equals(targetCompany))
                .forEach(entry -> {
                    System.out.println("Firma: " + entry.getKey());
                    System.out.println(entry.getValue());
                });

        System.out.println("\n--- 5. Walidacja spójności wynagrodzeń ---");
        List<Employee> inconsistentSalaries = employeeService.validateSalaryConsistency();
        if (inconsistentSalaries.isEmpty()) {
            System.out.println("Wszyscy pracownicy zarabiają powyżej minimalnej stawki dla swojego stanowiska.");
        } else {
            System.out.println("Znaleziono pracowników zarabiających poniżej bazowej stawki dla stanowiska:");
            inconsistentSalaries.forEach(emp ->
                    System.out.printf("  - %s %s (Stanowisko: %s, Pensja: %.2f, Baza: %.2f)\n",
                            emp.getName(), emp.getSurname(), emp.getPosition(), emp.getSalary(), emp.getPosition().getBaseSalary())
            );
        }

        System.out.println("\n--- KONIEC APLIKACJI ---");
    }

    public static void main(String[] args) {
        SpringApplication.run(EmployeeManagementApplication.class, args);
    }

}