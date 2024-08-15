package edu.tec.azuay.faan.config.image;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class UploadConfig {

    @Value("${upload.dir}")
    private String uploadDir;

    @Bean
    public File ensureUploadDirectoryExists() {
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            if (directory.mkdirs()) {
                System.out.printf("Upload directory created: %s%n", directory.getAbsolutePath());
            } else {
                System.err.printf("Failed to create upload directory: %s%n", directory.getAbsolutePath());
            }
        } else {
            System.out.printf("Upload directory already exists: %s%n", directory.getAbsolutePath());
        }
        return directory;
    }
}
