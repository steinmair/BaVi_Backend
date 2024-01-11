package at.htlklu.bavi.minio;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class MinioService {

    @Autowired
    private MinioClient minioClient;

    public List<String> listFiles(String bucketName) {
        List<String> objectNames = new ArrayList<>();
        try {
            Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder().bucket(bucketName).build());
            for (Result<Item> result : results) {
                Item item = result.get();
                objectNames.add(item.objectName());
            }
        } catch (Exception e) {
            throw new MinioServiceException("Error listing files in bucket: " + bucketName, e);
        }
        return objectNames;
    }


    /*public void uploadFile(String bucketName, String objectName, InputStream inputStream, String contentType) {
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(inputStream, inputStream.available(), -1)
                    .contentType(contentType)
                    .build());
        } catch (Exception e) {
            // Handle exceptions
        }
    }*/

    public void uploadFile(String bucketName, String objectName, InputStream inputStream, String contentType) {
        try {
            // Check if the file already exists, if so, delete it first
            if (minioClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(objectName).build()) != null) {
                deleteFile(bucketName, objectName);
            }

            // Upload the new file
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(inputStream, inputStream.available(), -1)
                    .contentType(contentType)
                    .build());
        } catch (Exception e) {
            // Handle exceptions
            throw new MinioServiceException("Error uploading file to bucket: " + bucketName, e);
        }
    }




    public InputStream downloadFile(String bucketName, String objectName) {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());
        } catch (Exception e) {
            throw new FileNotFoundException("Error downloading file from bucket: " + bucketName + ", object: " + objectName, e);
            // Handle exceptions

        }
    }


    public void deleteFile(String bucketName, String objectName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());
        } catch (Exception e) {
            // Handle exceptions
            throw new MinioServiceException("Error deleting file from bucket: " + bucketName + ", object: " + objectName, e);
        }
    }
    public void createBucket(String bucketName) {
        try {
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (Exception e) {
            throw new MinioServiceException("Bucket already Exist: " + bucketName);
            // Handle exceptions
        }
    }
    public void deleteBucket(String bucketName) {
        try {
            if (minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                // List all objects in the bucket
                List<String> objectNames = listFiles(bucketName);

                // Delete each object in the bucket
                for (String objectName : objectNames) {
                    deleteFile(bucketName, objectName);
                }

                // After all objects are deleted, remove the bucket
                minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
            } else {
                throw new MinioServiceException("Bucket does not exist: " + bucketName);
            }
        } catch (Exception e) {
            // Log the exception
            e.printStackTrace();
            // Rethrow the exception or handle it as needed
            throw new MinioServiceException("Error deleting bucket: " + bucketName, e);
        }
    }
}
