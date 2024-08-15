package edu.tec.azuay.faan.controller.file;

import edu.tec.azuay.faan.service.interfaces.IUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/upload")
public class UploadController {

    private final IUploadService uploadService;

    @PreAuthorize("denyAll()")
    @PostMapping
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam MultipartFile file, @RequestParam String folder) {
        Map<String, Object> response = new HashMap<>();
        try {
            String fileName = uploadService.saveFile(file, folder);
            response.put("data", fileName);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("message", "Error uploading file: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("get-file/{filename:.+}/{folder}")
    public ResponseEntity<Resource> getUploadedFile(@PathVariable String filename, @PathVariable String folder) {
        try {
            Resource resource = uploadService.getFile(filename, folder);
            String contentType = Files.probeContentType(Paths.get(resource.getURI()));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));

            return ResponseEntity.ok().headers(headers).body(resource);
        } catch (FileNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable String fileName) {
        try {
            uploadService.deleteFile(fileName);

            return new ResponseEntity<>("Successful", HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("Err: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
