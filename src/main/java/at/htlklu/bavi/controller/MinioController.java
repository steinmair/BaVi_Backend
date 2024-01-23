package at.htlklu.bavi.controller;

import at.htlklu.bavi.minio.MinioBucketExistsException;
import at.htlklu.bavi.minio.MinioService;
import at.htlklu.bavi.minio.MinioServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
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


    @PostMapping("{archivNumber}/upload")
    public ResponseEntity<String> uploadFile(@PathVariable String archivNumber, @RequestParam("file") MultipartFile file) {
        try {
            minioService.uploadFile(archivNumber, file.getOriginalFilename(), file.getInputStream(), file.getContentType());
            return ResponseEntity.ok().body("File uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Failed to upload file");
        }
    }

    @GetMapping("{archivNumber}/{file}/download")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable String archivNumber, @PathVariable String file) {
        try {
            ByteArrayResource resource = minioService.downloadFile(archivNumber, file);
            return ResponseEntity.ok()
                    .contentLength(resource.contentLength())
                    .header("Content-Disposition", "attachment; filename=\"" + file + "\"")
                    .body(resource);
        } catch (Exception e) {
            // Handle exceptions and return appropriate response
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("{archivNumber}/list")
    public ResponseEntity<List<String>> listFiles(@PathVariable String archivNumber) {
        try {
            List<String> objectNames = minioService.listFiles(archivNumber);
            return ResponseEntity.ok().body(objectNames);
        } catch (MinioServiceException e) {
            // Log the exception or perform any additional actions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @DeleteMapping("{archivNumber}/{file}/delete")
    public ResponseEntity<String> deleteFile(@PathVariable String archivNumber, @PathVariable String file) {
        try {
            minioService.deleteFile(archivNumber, file);
            return ResponseEntity.ok().body("File " + file + " deleted successfully");
        } catch (Exception e) {
            // Handle exceptions and return appropriate response
            return ResponseEntity.status(500).body("Failed to delete file");
        }
    }

    @PostMapping("{archivNumber}/createBucket")
    public ResponseEntity<String> createBucket(@PathVariable String archivNumber) {
        try {
            minioService.createBucket(archivNumber);
            return ResponseEntity.ok().body("Bucket created successfully");
        } catch (MinioBucketExistsException e) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body("Bucket already exists");
        } catch (MinioServiceException e) {
            // Log the exception or perform any additional actions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating bucket: " + e.getMessage());
        }
    }

    @DeleteMapping("{archivNumber}/deleteBucket")
    public ResponseEntity<String> deleteBucket(@PathVariable String archivNumber) {
        try {
            minioService.deleteBucket(archivNumber);
            return ResponseEntity.ok().body("Bucket deleted successfully");
        }catch (MinioBucketExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Bucket does not Exist");
        }
        catch (MinioServiceException e) {
            // Log the exception or perform any additional actions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting bucket: " + e.getMessage());
        }
    }
}