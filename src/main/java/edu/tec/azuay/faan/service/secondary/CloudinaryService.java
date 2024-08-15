package edu.tec.azuay.faan.service.secondary;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    /**
     * Uploads an image to Cloudinary.
     *
     * @param multipartFile the file to upload
     * @return a map containing the upload result
     * @throws IOException if an I/O error occurs
     */
    public Map upload(MultipartFile multipartFile) throws IOException {
        File file = convert(multipartFile);
        Map result = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());

        if (!Files.deleteIfExists(file.toPath())) {
            throw new IOException("Failed to delete temporary file: " + file.getAbsolutePath());
        }

        return result;
    }

    /**
     * Deletes an image from Cloudinary.
     *
     * @param id the ID of the image to delete
     * @return a map containing the deletion result
     * @throws IOException if an I/O error occurs
     */
    public Map delete(String id) throws IOException {
        return cloudinary.uploader().destroy(id, ObjectUtils.emptyMap());
    }

    /**
     * Converts a MultipartFile to a File.
     *
     * @param multipartFile the file to convert
     * @return the converted file
     * @throws IOException if an I/O error occurs
     */
    private File convert(MultipartFile multipartFile) throws IOException {
        File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        try (FileOutputStream fo = new FileOutputStream(file)) {
            fo.write(multipartFile.getBytes());
        }
        return file;
    }

    /**
     * Gets the optimized image URL.
     *
     * @param url the URL of the image to optimize
     * @return the optimized image URL
     * @see <a href="https://cloudinary.com/documentation/image_transformation_reference">Cloudinary Image Transformations</a>
     */
    public String getOptimizedImageUrl(String url) {
        return cloudinary.url().transformation(new Transformation()
                .width(300).crop("scale").chain()
                .quality("auto").chain()
                .fetchFormat("auto")).generate(url.substring(url.lastIndexOf("/") + 1));
    }
}
