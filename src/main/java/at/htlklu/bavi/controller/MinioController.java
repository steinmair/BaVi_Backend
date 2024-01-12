package at.htlklu.bavi.controller;

import at.htlklu.bavi.minio.MinioService;
import at.htlklu.bavi.minio.MinioServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("")
public class MinioController {

    @Autowired
    private MinioService minioService;

    @PostMapping("{title}/upload")
    public ResponseEntity<String> uploadFile(@PathVariable String title, @RequestParam("file") MultipartFile file) {
        try {
            minioService.uploadFile(title, file.getOriginalFilename(), file.getInputStream(), file.getContentType());
            return ResponseEntity.ok().body("File uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Failed to upload file");
        }
    }

    @GetMapping("{title}/{file}/download")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String title, @PathVariable String file) {
        try {
            InputStreamResource resource = new InputStreamResource(minioService.downloadFile(title, file));
            return ResponseEntity.ok()
                    .contentLength(resource.contentLength())
                    .header("Content-Disposition", "attachment; filename=\"" + file + "\"")
                    .body(resource);
        } catch (Exception e) {
            // Handle exceptions and return appropriate response
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("{title}/list")
    public ResponseEntity<List<String>> listFiles(@PathVariable String title) {
        try {
            List<String> objectNames = minioService.listFiles(title);
            return ResponseEntity.ok().body(objectNames);
        } catch (MinioServiceException e) {
            // Log the exception or perform any additional actions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @DeleteMapping("{title}/{file}/delete")
    public ResponseEntity<String> deleteFile(@PathVariable String title, @PathVariable String file) {
        try {
            minioService.deleteFile(title, file);
            return ResponseEntity.ok().body("File " + file + " deleted successfully");
        } catch (Exception e) {
            // Handle exceptions and return appropriate response
            return ResponseEntity.status(500).body("Failed to delete file");
        }
    }

    @PostMapping("{title}/createBucket")
    public ResponseEntity<String> createBucket(@PathVariable String title) {
        try {
            minioService.createBucket(title);
            return ResponseEntity.ok().body("Bucket created successfully");
        } catch (MinioServiceException e) {
            // Log the exception or perform any additional actions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating bucket: " + e.getMessage());
        }
    }

    @DeleteMapping("{title}/deleteBucket")
    public ResponseEntity<String> deleteBucket(@PathVariable String title) {
        try {
            minioService.deleteBucket(title);
            return ResponseEntity.ok().body("Bucket deleted successfully");
        } catch (MinioServiceException e) {
            // Log the exception or perform any additional actions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting bucket: " + e.getMessage());
        }
    }
}