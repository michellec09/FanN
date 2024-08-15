package edu.tec.azuay.faan.service.interfaces;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IUploadService {

    String saveFile(MultipartFile file, String folder) throws IOException;

    Resource getFile(String filename, String folder) throws IOException;

    void deleteFile(String filename) throws IOException;

    String getUrlFile(String filename, String folder);
}
