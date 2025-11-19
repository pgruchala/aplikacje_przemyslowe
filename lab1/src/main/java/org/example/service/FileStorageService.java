package org.example.service;

import org.example.exception.FileNotFoundException;
import org.example.exception.FileStorageException;
import org.example.exception.InvalidFileException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;
@Service
public class FileStorageService {

    private final Path uploadPath;
    private final Path reportsPath;
    private final Path documentsRootPath

    public FileStorageService(
            @Value("${app.upload.directory}") String uploadDir,
            @Value("${app.reports.directory}") String reportsDir) {

        this.uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.reportsPath = Paths.get(reportsDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.uploadPath);
            Files.createDirectories(this.reportsPath);
        } catch (IOException ex) {
            throw new FileStorageException("Nie można utworzyć katalogów.", ex);
        }
    }

    /**
     * Zapisuje plik na dysku, używając logiki z poradnika.
     * Ta funkcja jest teraz "pełna".
     */
    public String storeFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new InvalidFileException("Nie można zapisać pustego pliku");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("Nazwa pliku jest wymagana");
        }

        String extension = getFileExtension(originalFilename);


        String baseName = originalFilename;
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > -1) {
            baseName = originalFilename.substring(0, dotIndex);
        }

        String filename = generateUniqueFilename(baseName, extension);

        Path targetLocation = this.uploadPath.resolve(filename);

        try {
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return filename;
        } catch (IOException ex) {
            throw new FileStorageException("Błąd podczas zapisu pliku " + originalFilename, ex);
        }
    }

    public Path loadFile(String filename) {
        Path filePath = this.uploadPath.resolve(filename).normalize();
        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("Nie znaleziono pliku: " + filename);
        }
        return filePath;
    }

    public void deleteFile(String filename) {
        try {
            Path filePath = uploadPath.resolve(filename).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new FileStorageException("Nie można usunąć pliku: " + filename, ex);
        }
    }


    private String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        if (lastDot == -1) {
            return "";
        }
        return filename.substring(lastDot + 1);
    }

    private String generateUniqueFilename(String baseName, String extension) {
        String cleanTitle = baseName.replaceAll("[^a-zA-Z0-9]", "_");
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);

        String finalExtension = extension.isEmpty() ? "" : "." + extension;

        return cleanTitle + "_" + uniqueId + finalExtension;
    }

    public Path getReportsPath() {
        return reportsPath;
    }
}
