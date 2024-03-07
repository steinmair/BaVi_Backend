package at.htlklu.bavi.controller;

import at.htlklu.bavi.minio.MinioBucketExistsException;
import at.htlklu.bavi.minio.MinioService;
import at.htlklu.bavi.minio.MinioServiceException;
import at.htlklu.bavi.utils.LogUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("")
public class MinioController {

    private static final Logger logger = LogManager.getLogger(MinioController.class);
    private static final String CLASS_NAME = "MinioController";

    @Autowired
    private MinioService minioService;


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("{archivNumber}/upload")
    @Operation(summary = "Upload File", description = "Upload a file to the specified song")
    @ApiResponse(responseCode = "200", description = "File uploaded successfully")
    @ApiResponse(responseCode = "400", description = "Failed to upload file")
    public ResponseEntity<String> uploadFile(@PathVariable String archivNumber, @RequestParam("file") MultipartFile file) {
        logger.info(LogUtils.info(CLASS_NAME, "uploadFile", String.format("(%s, %s)", archivNumber, file.getOriginalFilename())));

        try {
            minioService.uploadFile(archivNumber, file.getOriginalFilename(), file.getInputStream(), file.getContentType());
            logger.info("File uploaded successfully: {}", file.getOriginalFilename());
            return ResponseEntity.ok().body("File uploaded successfully");
        } catch (IOException e) {
            logger.error("Failed to upload file: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Failed to upload file");
        }
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("{archivNumber}/{file}/download")
    @Operation(summary = "Download File", description = "Download a file from a specified song")
    @ApiResponse(responseCode = "200", description = "File downloaded successfully")
    @ApiResponse(responseCode = "400", description = "Failed to download file")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable String archivNumber, @PathVariable String file) {
        logger.info(LogUtils.info(CLASS_NAME, "downloadFile", String.format("(%s, %s)", archivNumber, file)));

        try {
            ByteArrayResource resource = minioService.downloadFile(archivNumber, file);
            logger.info("File downloaded successfully: {}", file);
            return ResponseEntity.ok()
                    .contentLength(resource.contentLength())
                    .header("Content-Disposition", "attachment; filename=\"" + file + "\"")
                    .body(resource);
        } catch (Exception e) {
            logger.error("Failed to download file {}: {}", file, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("{archivNumber}/list")
    @Operation(summary = "List song files", description = "List a file from the specified song")
    @ApiResponse(responseCode = "200", description = "Files listed successfully")
    @ApiResponse(responseCode = "400", description = "Failed to list files")
    public ResponseEntity<List<String>> listFiles(@PathVariable String archivNumber) {
        logger.info(LogUtils.info(CLASS_NAME, "listFiles", String.format("(%s)", archivNumber)));

        try {
            List<String> objectNames = minioService.listFiles(archivNumber);
            logger.info("List of files retrieved successfully for archive: {}", archivNumber);
            return ResponseEntity.ok().body(objectNames);
        } catch (MinioServiceException e) {
            logger.error("Failed to list files for archive {}: {}", archivNumber, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("{archivNumber}/{file}/delete")
    @Operation(summary = "Delete File", description = "Upload a file to the specified archive number")
    @ApiResponse(responseCode = "200", description = "Files listed successfully")
    @ApiResponse(responseCode = "400", description = "Failed to list files")
    public ResponseEntity<String> deleteFile(@PathVariable String archivNumber, @PathVariable String file) {
        logger.info(LogUtils.info(CLASS_NAME, "deleteFile", String.format("(%s, %s)", archivNumber, file)));

        try {
            minioService.deleteFile(archivNumber, file);
            logger.info("File {} deleted successfully from archive {}", file, archivNumber);
            return ResponseEntity.ok().body("File " + file + " deleted successfully");
        } catch (Exception e) {
            logger.error("Failed to delete file {} from archive {}: {}", file, archivNumber, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete file");
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("{archivNumber}/createBucket")
    @Operation(summary = "Create Bucket", description = "Create a Bucket")
    @ApiResponse(responseCode = "200", description = "Bucket created successfully")
    @ApiResponse(responseCode = "400", description = "Failed to create a Bucket")
    public ResponseEntity<String> createBucket(@PathVariable String archivNumber) {
        logger.info(LogUtils.info(CLASS_NAME, "createBucket", String.format("(%s)", archivNumber)));

        try {
            minioService.createBucket(archivNumber);
            logger.info("Bucket created successfully: {}", archivNumber);
            return ResponseEntity.ok().body("Bucket created successfully");
        } catch (MinioBucketExistsException e) {
            logger.warn("Bucket {} already exists", archivNumber);
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body("Bucket already exists");
        } catch (MinioServiceException e) {
            logger.error("Error creating bucket {}: {}", archivNumber, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating bucket: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("{archivNumber}/deleteBucket")
    @Operation(summary = "Delete Bucket", description = "Delete Bucket")
    @ApiResponse(responseCode = "200", description = "Deleted successfully")
    @ApiResponse(responseCode = "400", description = "Failed to Delete Bucket")
    public ResponseEntity<String> deleteBucket(@PathVariable String archivNumber) {
        logger.info(LogUtils.info(CLASS_NAME, "deleteBucket", String.format("(%s)", archivNumber)));

        try {
            minioService.deleteBucket(archivNumber);
            logger.info("Bucket deleted successfully: {}", archivNumber);
            return ResponseEntity.ok().body("Bucket deleted successfully");
        } catch (MinioBucketExistsException e) {
            logger.warn("Bucket {} does not exist", archivNumber);
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Bucket does not exist");
        } catch (MinioServiceException e) {
            logger.error("Error deleting bucket {}: {}", archivNumber, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting bucket: " + e.getMessage());
        }
    }
}