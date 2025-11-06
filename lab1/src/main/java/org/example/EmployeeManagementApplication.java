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
public class EmployeeManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(EmployeeManagementApplication.class, args);
    }
}