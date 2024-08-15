package edu.tec.azuay.faan.service.secondary;

import edu.tec.azuay.faan.persistence.entity.Image;
import edu.tec.azuay.faan.persistence.repository.ImageRepository;
import edu.tec.azuay.faan.persistence.utils.Converter;
import edu.tec.azuay.faan.service.interfaces.IUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.UUID;

import static edu.tec.azuay.faan.persistence.utils.ConstantNames.FOLDER_MAPPING;

@Service
@RequiredArgsConstructor
public class UploadServiceImpl implements IUploadService {

    private Path rootFolder;

    private final ImageRepository imageRepository;

    @Override
    public String saveFile(MultipartFile file, String folder) throws IOException {
        String originalFileName = Objects.requireNonNull(file.getOriginalFilename()).replaceAll("\\s+", "");
        String uniqueFilename = UUID.randomUUID() + "_" + Converter.normalizeFileName(originalFileName);
        byte[] bytes = file.getBytes();

        String fileHash = calculateHash(bytes);

        Image existingImage = imageRepository.findByImageHash(fileHash);

        if (existingImage != null) {
            return existingImage.getImagePath();
        }

        String folderPath = FOLDER_MAPPING.get(folder);

        Path path = Paths.get(folderPath + File.separator + uniqueFilename);
        Files.write(path, bytes);

        Image photo = new Image(uniqueFilename);
        photo.setImageHash(fileHash);
        photo.setImagePath(uniqueFilename);
        photo.setImageUrl(getUrlFile(uniqueFilename, folder));

        Image savedImage = imageRepository.insert(photo);

        if (ObjectUtils.isEmpty(savedImage)) {
            return "";
        }

        return uniqueFilename;
    }

    @Override
    public Resource getFile(String filename, String folder) throws IOException {
        this.rootFolder = Paths.get(FOLDER_MAPPING.get(folder));
        Path path = rootFolder.resolve(filename);
        Resource resource = new UrlResource(path.toUri());

        if (resource.exists() && resource.isReadable()) {
            return resource;
        } else {
            throw new FileNotFoundException("Resource not found: " + filename);
        }
    }

    @Override
    public String getUrlFile(String filename, String folder) {
        String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

        return fileUrl + String.format("/upload/get-file/%s/%s", filename, folder);
    }

    @Override
    public void deleteFile(String filename) throws IOException {
        Path path = rootFolder.resolve(filename);

        if (Files.exists(path)) {
            Files.delete(path);
        } else {
            throw new FileNotFoundException("Resource not found: " + filename);
        }
    }

    private String calculateHash(byte[] fileBytes) {
        return getHash(fileBytes);
    }

    static String getHash(byte[] fileBytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(fileBytes);
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error calculating hash", e);
        }
    }
}
