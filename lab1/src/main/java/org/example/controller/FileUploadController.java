package org.example.controller;

import org.example.exception.EmployeeNotFoundException;
import org.example.exception.FileNotFoundException;
import org.example.model.CompanyStatistics;
import org.example.model.Employee;
import org.example.model.ImportSummary;
import org.example.service.EmployeeService;
import org.example.service.FileStorageService;
import org.example.service.ImportService;
import org.example.service.ReportGeneratorService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {
    private final ImportService importService;
    private final FileStorageService fileStorageService;
    private final ReportGeneratorService reportGeneratorService;
    private final EmployeeService employeeService;

    public FileUploadController(ImportService importService,
                                FileStorageService fileStorageService,
                                ReportGeneratorService reportGeneratorService,
                                EmployeeService employeeService) {
        this.importService = importService;
        this.fileStorageService = fileStorageService;
        this.reportGeneratorService = reportGeneratorService;
        this.employeeService = employeeService;
    }

    @PostMapping("/import/csv")
    public ResponseEntity<ImportSummary> importCsv(@RequestParam("file") MultipartFile file) {
        String savedFilename = null;
        try {
            savedFilename = fileStorageService.storeFile(file);

            Path fullPathToFile = fileStorageService.loadFile(savedFilename);

            ImportSummary summary = importService.importFromCSV(fullPathToFile.toString());

            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas importu pliku: " + e.getMessage(), e);
        } finally {
            if (savedFilename != null) {
                fileStorageService.deleteFile(savedFilename);
            }
        }

    }
    @GetMapping("/export/csv")
    public ResponseEntity<Resource> exportEmployeesCsv(
            @RequestParam(required = false) String company) {

        List<Employee> employees;
        String downloadFilename;
        if (company != null && !company.isEmpty()) {
            employees = employeeService.searchByCompany(company);
            downloadFilename = "employees_" + company + ".csv";
        } else {
            employees = employeeService.displayEmployees();
            downloadFilename = "employees.csv";
        }

        Path csvPath = reportGeneratorService.generateEmployeesCsv(employees);
        Resource resource;

        try {
            resource = new UrlResource(csvPath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new FileNotFoundException("Nie można odczytać wygenerowanego pliku: " + csvPath.getFileName());
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + downloadFilename + "\"")
                    .body(resource);
        } catch (MalformedURLException e) {
            throw new FileNotFoundException("Nie można odczytać wygenerowanego pliku (Malformed URL)", e);
        } finally {
            if (csvPath != null) {
                try {
                    Files.deleteIfExists(csvPath);
                } catch (IOException e) {
                    System.err.println("Nie można usunąć pliku tymczasowego: " + csvPath);
                }
            }
        }
    }

    @GetMapping("/reports/statistics/{companyName}")
    public ResponseEntity<Resource> exportStatisticsPdf(@PathVariable String companyName) {

        CompanyStatistics stats = employeeService.getCompanyStatistics().get(companyName);
        if (stats == null) {
            throw new EmployeeNotFoundException("Nie znaleziono statystyk dla firmy: " + companyName);
        }

        // 1. Generuj plik (dostajemy pełną ścieżkę do 'reports/...')
        Path pdfPath = reportGeneratorService.generateCompanyStatisticsPdf(companyName, stats);
        Resource resource;

        try {
            // 2. Załaduj zasób (BEZ HELPERA)
            resource = new UrlResource(pdfPath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new FileNotFoundException("Nie można odczytać wygenerowanego pliku: " + pdfPath.getFileName());
            }

            // 3. Zwróć odpowiedź
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + pdfPath.getFileName().toString() + "\"")
                    .body(resource);
        } catch (MalformedURLException e) {
            throw new FileNotFoundException("Nie można odczytać wygenerowanego pliku (Malformed URL)", e);
        } finally {
            // 4. Posprzątaj (BEZ HELPERA, używamy Files.deleteIfExists)
            if (pdfPath != null) {
                try {
                    Files.deleteIfExists(pdfPath);
                } catch (IOException e) {
                    System.err.println("Nie można usunąć pliku tymczasowego: " + pdfPath);
                }
            }
        }
    }
}
