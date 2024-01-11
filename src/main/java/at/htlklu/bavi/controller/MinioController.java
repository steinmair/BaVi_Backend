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

    @PostMapping("/upload/{bucketName}")
    public ResponseEntity<String> uploadFile(@PathVariable String bucketName, @RequestParam("file") MultipartFile file) {
        try {
            minioService.uploadFile(bucketName, file.getOriginalFilename(), file.getInputStream(), file.getContentType());
            return ResponseEntity.ok().body("File uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Failed to upload file");
        }
    }

    @GetMapping("/download/{bucketName}/{objectName}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String bucketName, @PathVariable String objectName) {
        try {
            InputStreamResource resource = new InputStreamResource(minioService.downloadFile(bucketName, objectName));
            return ResponseEntity.ok()
                    .contentLength(resource.contentLength())
                    .header("Content-Disposition", "attachment; filename=\"" + objectName + "\"")
                    .body(resource);
        } catch (Exception e) {
            // Handle exceptions and return appropriate response
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/list/{bucketName}")
    public ResponseEntity<List<String>> listFiles(@PathVariable String bucketName) {
        try {
            List<String> objectNames = minioService.listFiles(bucketName);
            return ResponseEntity.ok().body(objectNames);
        } catch (MinioServiceException e) {
            // Log the exception or perform any additional actions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @DeleteMapping("/delete/{bucketName}/{objectName}")
    public ResponseEntity<String> deleteFile(@PathVariable String bucketName, @PathVariable String objectName) {
        try {
            minioService.deleteFile(bucketName, objectName);
            return ResponseEntity.ok().body("File " + objectName + " deleted successfully");
        } catch (Exception e) {
            // Handle exceptions and return appropriate response
            return ResponseEntity.status(500).body("Failed to delete file");
        }
    }

    @PostMapping("/createBucket/{bucketName}")
    public ResponseEntity<String> createBucket(@PathVariable String bucketName) {
        try {
            minioService.createBucket(bucketName);
            return ResponseEntity.ok().body("Bucket created successfully");
        } catch (MinioServiceException e) {
            // Log the exception or perform any additional actions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating bucket: " + e.getMessage());
        }
    }

    @DeleteMapping("/deleteBucket/{bucketName}")
    public ResponseEntity<String> deleteBucket(@PathVariable String bucketName) {
        try {
            minioService.deleteBucket(bucketName);
            return ResponseEntity.ok().body("Bucket deleted successfully");
        } catch (MinioServiceException e) {
            // Log the exception or perform any additional actions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting bucket: " + e.getMessage());
        }
    }
}