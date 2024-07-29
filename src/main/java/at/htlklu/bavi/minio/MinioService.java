package at.htlklu.bavi.minio;

import at.htlklu.bavi.controller.SongController;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

//@CrossOrigin("http://localhost:4200")
@CrossOrigin(origins = "*", maxAge = 3600)
@Service
public class MinioService {

    @Autowired
    private MinioClient minioClient;

    private static final Logger logger = LogManager.getLogger(MinioService.class);
    private static final String CLASS_NAME = "MinioService";

    public List<String> listFiles(String bucketName) {
        bucketName = MinioHelper.prepareMinioBucketName(bucketName);

        List<String> objectNames = new ArrayList<>();
        try {
            Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder().bucket(bucketName).build());
            for (Result<Item> result : results) {
                Item item = result.get();
                objectNames.add(item.objectName());
            }
            logger.debug("Listed files in bucket: {}", bucketName);
        } catch (Exception e) {
            throw new MinioServiceException("Error listing files in bucket: " + bucketName, e);
        }
        return objectNames;
    }


    public void uploadFile(String bucketName, String objectName, InputStream inputStream, String contentType) {
        bucketName = MinioHelper.prepareMinioBucketName(bucketName);

        // Check if the file already exists, if so, delete it first
        try {
            if (minioClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(objectName).build()) != null) {
                logger.warn("File {} already exists in bucket {}, deleting it first", objectName, bucketName);
                deleteFile(bucketName, objectName);
            }
        } catch (ErrorResponseException ere) {
            // Object does not exist, no need to delete
        } catch (Exception e) {
            throw new MinioServiceException("Error uploading " + objectName + " to bucket: " + bucketName,e);
        }

        try {
            // Upload the new file
            logger.info("Uploading file {} to bucket {}", objectName, bucketName);
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(inputStream, inputStream.available(), -1)
                    .contentType(contentType)
                    .build());
            logger.debug("File {} uploaded successfully to bucket {}", objectName, bucketName);
        } catch (Exception e) {
            throw new MinioServiceException("Error uploading file to bucket: " + bucketName + ", object: " + objectName, e);
        }
    }


    public ByteArrayResource downloadFile(String bucketName, String objectName) {
        bucketName = MinioHelper.prepareMinioBucketName(bucketName);

        try {
            InputStream inputStream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());

            byte[] content = IOUtils.toByteArray(inputStream);

            logger.debug("Downloaded file from bucket: {}, object: {}", bucketName, objectName);

            return new ByteArrayResource(content);
        } catch (Exception e) {
            throw new RuntimeException("Error downloading file from bucket: " + bucketName + ", object: " + objectName, e);
        }
    }


    public void deleteFile(String bucketName, String objectName) {
        bucketName = MinioHelper.prepareMinioBucketName(bucketName);
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());

            logger.debug("Deleted file from bucket: {}, object: {}", bucketName, objectName);
        } catch (Exception e) {
            throw new MinioServiceException("Error deleting file from bucket: " + bucketName + ", object: " + objectName, e);
        }
    }

    public void copyFile(String sourceBucketName, String sourceFileName, String destinationBucketName, String destinationFileName)
            throws IOException, InvalidKeyException, NoSuchAlgorithmException, MinioException {

        try {
            // Check if the source and destination buckets are different
            if (sourceBucketName.equals(destinationBucketName)) {
                // If source and destination buckets are the same, log and return without copying
                logger.warn("Source and destination buckets are the same, skipping copy operation");
                return;
            }

            // Perform the copy operation
            minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .source(CopySource.builder()
                                    .bucket(sourceBucketName)
                                    .object(sourceFileName)
                                    .build())
                            .bucket(destinationBucketName)
                            .object(destinationFileName)
                            .build());

            // Log successful copy operation
            logger.debug("Copied file from {} in bucket {} to {} in bucket {}", sourceFileName, sourceBucketName, destinationFileName, destinationBucketName);
        } catch (MinioException e) {
            // Log and re-throw any Minio-specific exceptions
            logger.error("Error copying file from {} in bucket {} to {} in bucket {}", sourceFileName, sourceBucketName, destinationFileName, destinationBucketName, e);
            throw e;
        } catch (Exception e) {
            // Log and re-throw any other exceptions
            logger.error("Error copying file from {} in bucket {} to {} in bucket {}", sourceFileName, sourceBucketName, destinationFileName, destinationBucketName, e);

        }
    }

    public void createBucket(String bucketName) {
        bucketName = MinioHelper.prepareMinioBucketName(bucketName);
        try {
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                logger.debug("Created bucket: {}", bucketName);
            } else {
                logger.warn("Bucket already exists: {}", bucketName);
            }
        } catch (Exception e) {

            throw new MinioServiceException("Error creating bucket: " + bucketName, e);
        }
    }

    public void deleteBucket(String bucketName) {
        bucketName = MinioHelper.prepareMinioBucketName(bucketName);
        try {
            if (minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                // List all objects in the bucket
                List<String> objectNames = listFiles(bucketName);

                // Delete each object in the bucket
                for (String objectName : objectNames) {
                    deleteFile(bucketName, objectName);
                    logger.debug("Deleted object {} from bucket {}", objectName, bucketName);
                }

                // After all objects are deleted, remove the bucket
                minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
                logger.info("Deleted bucket: {}", bucketName);
            } else {

                throw new MinioServiceException("Bucket does not exist: " + bucketName);
            }
        } catch (Exception e) {

            throw new MinioServiceException("Error deleting bucket: " + bucketName, e);
        }
    }
}
