package org.example.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.example.model.ImportSummary;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class ImportService {
    EmployeeService e_service;
    public ImportService(EmployeeService e_service) {
        this.e_service = e_service;
    }
    public static ImportSummary importFromCSV(String filePath) throws IOException, CsvException {
        ImportSummary summary = new ImportSummary();
        try (CSVReader reader = new CSVReader(new FileReader(filePath))){
            List<String[]> allRows = reader.readAll();
            for (int i = 1; i < allRows.size(); i++){
                String[] row = allRows.get(i);

            }
        }


        return summary;
    }
}
