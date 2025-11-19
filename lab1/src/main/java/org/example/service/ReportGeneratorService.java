package org.example.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import org.example.exception.FileStorageException;
import org.example.model.CompanyStatistics;
import org.example.model.Employee;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@Service
public class ReportGeneratorService {
    private final Path reportsPath;

    public ReportGeneratorService(FileStorageService fileStorageService) {
        this.reportsPath = fileStorageService.getReportsPath();
    }

    public Path generateEmployeesCsv(List<Employee> employees) {
        String filename = "employees_" + UUID.randomUUID().toString() + ".csv";
        Path filePath = this.reportsPath.resolve(filename);

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath.toFile()))) {

            writer.println("Name,Surname,Email,Company,Position,Salary,Status");

            for (Employee e : employees) {
                writer.printf("%s,%s,%s,%s,%s,%.2f,%s\n",
                        e.getName(),
                        e.getSurname(),
                        e.getEmail(),
                        e.getCompany(),
                        e.getPosition().getName(),
                        e.getSalary(),
                        e.getStatus().name()
                );
            }

        } catch (IOException e) {
            throw new FileStorageException("Nie można wygenerować raportu CSV: " + e.getMessage(), e);
        }

        return filePath;
    }
    public Path generateCompanyStatisticsPdf(String companyName, CompanyStatistics stats) {
        String filename = "stats_" + companyName.replaceAll("[^a-zA-Z0-9]", "_") + "_" + UUID.randomUUID().toString() + ".pdf";
        Path filePath = this.reportsPath.resolve(filename);

        try (PdfWriter writer = new PdfWriter(filePath.toString());
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            document.add(new Paragraph("Raport Statystyk dla Firmy")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(20));

            document.add(new Paragraph(companyName)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(16));

            document.add(new Paragraph("\n")); // Odstęp

            document.add(new Paragraph("Liczba pracowników: " + stats.getEmployeeCount())
                    .setFontSize(12));

            document.add(new Paragraph(String.format("Średnie wynagrodzenie: %.2f PLN", stats.getAvgSalary()))
                    .setFontSize(12));

            document.add(new Paragraph("Najlepiej zarabiający pracownik: " + stats.getHighestPaid())
                    .setFontSize(12));

        } catch (IOException e) {
            throw new FileStorageException("Nie można wygenerować raportu PDF: " + e.getMessage(), e);
        }

        return filePath;
    }

}
