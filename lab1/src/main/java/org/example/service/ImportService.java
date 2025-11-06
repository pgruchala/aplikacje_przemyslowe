package org.example.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.example.exception.DuplicateEmailException;
import org.example.exception.InvalidDataException;
import org.example.model.Employee;
import org.example.model.ImportSummary;
import org.example.model.POSITION;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

@Service
public class ImportService {
    private final EmployeeService e_service;

    public ImportService(EmployeeService e_service) {
        this.e_service = e_service;
    }

    public ImportSummary importFromCSV(String filePath) throws IOException, CsvException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))){
            return importFromReader(reader);
        }
    }
    public ImportSummary importFromReader(Reader sourceReader) throws IOException {
        ImportSummary summary = new ImportSummary();
        int lineNumber = 0;

        try (BufferedReader reader = new BufferedReader(sourceReader)) {
            String line;
            if (reader.readLine() != null) {
                lineNumber++;
            }
            while ((line = reader.readLine()) != null){
                lineNumber++;
                String originalLine = line.trim();
                if (originalLine.isEmpty()){
                    continue;
                }
                String[] parts = originalLine.split(";");
                String errorDescription = null;
                if (parts.length != 6) {
                    errorDescription = "Nieprawidłowa liczba kolumn (oczekiwano 6)";
                } else {
                    try {
                        String name = parts[0].trim();
                        String surname = parts[1].trim();
                        String email = parts[2].trim();
                        String company = parts[3].trim();
                        String positionStr = parts[4].trim().toUpperCase(); // Konwersja na wielkie litery dla enum
                        String salaryStr = parts[5].trim();

                        POSITION position;
                        try {
                            position = POSITION.valueOf(positionStr);
                        } catch (IllegalArgumentException e) {
                            throw new IllegalArgumentException("Nieprawidłowe stanowisko: " + positionStr);
                        }

                        double salary = Double.parseDouble(salaryStr);
                        if (salary <= 0) {
                            throw new InvalidDataException("Wynagrodzenie musi być dodatnie: " + salary);
                        }

                        Employee employee = new Employee(name, surname, email, company, position);
                        employee.setSalary(salary);

                        try {
                            this.e_service.addEmployee(employee);
                            summary.success();
                        } catch (DuplicateEmailException e) {
                            errorDescription = "Nie można dodać pracownika - duplikat emaila: " + email;
                        } catch (IllegalArgumentException e) {
                            errorDescription = "Błąd walidacji danych (serwis): " + e.getMessage();
                        }

                    } catch (InvalidDataException e) {
                        errorDescription = "Błąd walidacji danych: " + e.getMessage();
                    } catch (IllegalArgumentException e) {
                        errorDescription = "Błąd parsowania danych: " + e.getMessage();
                    } catch (Exception e) {
                        errorDescription = "Nieoczekiwany błąd: " + e.getMessage();
                    }
                }

                if (errorDescription != null) {
                    String errorMsg = String.format("Linia %d: %s (Dane: %s)", lineNumber, errorDescription, originalLine);
                    summary.error(errorMsg);
                }

            }
        }


        return summary;
    }
}
