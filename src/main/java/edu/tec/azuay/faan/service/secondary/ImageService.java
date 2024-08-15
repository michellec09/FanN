package edu.tec.azuay.faan.service.secondary;

import edu.tec.azuay.faan.exceptions.ObjectNotFoundException;
import edu.tec.azuay.faan.persistence.dto.secondary.ImageResponse;
import edu.tec.azuay.faan.persistence.entity.Image;
import edu.tec.azuay.faan.persistence.repository.ImageRepository;
import edu.tec.azuay.faan.persistence.utils.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static edu.tec.azuay.faan.service.secondary.UploadServiceImpl.getHash;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;

    private final CloudinaryService cloudinaryService;

    public Image getOne(String id) {
        return imageRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Image not found"));
    }

    public ImageResponse uploadImage(MultipartFile file) throws IOException {
        String originalFileName = Objects.requireNonNull(file.getOriginalFilename()).replaceAll("\\\\s+", "");
        String uniqueFileName = UUID.randomUUID() + "_" + LocalDateTime.now() + "_" + Converter.normalizeFileName(originalFileName);
        byte[] fileBytes = file.getBytes();

        String fileHash = calculateHash(fileBytes);

        Image existingImage = imageRepository.findByImageHash(fileHash);

        if (existingImage != null) {
            return ImageResponse.builder().message("Image already exists").imagePath(existingImage.getImagePath()).imageUrl(existingImage.getImageUrl()).build();
        }

        Map result = cloudinaryService.upload(file);

        Image photo = new Image(uniqueFileName);
        photo.setImageHash(fileHash);
        photo.setImageUrl((String) result.get("url"));
        photo.setImagePath((String) result.get("public_id"));
        photo.setImageId((String) result.get("asset_id"));

        Image savedImage = imageRepository.insert(photo);

        if (ObjectUtils.isEmpty(savedImage)) {
            return ImageResponse.builder().message("Image not saved").build();
        }

        return ImageResponse.builder().message("Image saved successfully").imagePath(savedImage.getImagePath()).imageUrl(savedImage.getImageUrl()).build();
    }

    private String calculateHash(byte[] fileBytes) {
        return getHash(fileBytes);
    }

    public Image findByPath(String path) {
        return imageRepository.findByImagePath(path);
    }
}
