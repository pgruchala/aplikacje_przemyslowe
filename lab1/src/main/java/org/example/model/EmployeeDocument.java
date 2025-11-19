package org.example.model;

import java.time.LocalDate;
import java.util.UUID;

public class EmployeeDocument {
    private String id;
    private String employeeEmail;
    private String fileName;
    private String originalFileName;
    private DocumentType fileType;
    private LocalDate uploadDate;
    private String filePath;

    public EmployeeDocument() {
    }

    public EmployeeDocument(String employeeEmail, String fileName, String originalFileName, DocumentType fileType, String filePath) {
        this.id = UUID.randomUUID().toString();
        this.employeeEmail = employeeEmail;
        this.fileName = fileName;
        this.originalFileName = originalFileName;
        this.fileType = fileType;
        this.uploadDate = LocalDate.now();
        this.filePath = filePath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmployeeEmail() {
        return employeeEmail;
    }

    public void setEmployeeEmail(String employeeEmail) {
        this.employeeEmail = employeeEmail;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public DocumentType getFileType() {
        return fileType;
    }

    public void setFileType(DocumentType fileType) {
        this.fileType = fileType;
    }

    public LocalDate getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDate uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
