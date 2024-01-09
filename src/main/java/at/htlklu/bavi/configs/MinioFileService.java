package at.htlklu.bavi.configs;
import io.minio.*;
import io.minio.errors.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class MinioFileService {

    private final MinioClient minioClient;

    public MinioFileService() {
        this.minioClient = MinioClient.builder()
                .endpoint("http://localhost:9000") // Ihr MinIO-Endpunkt hier
                .credentials("root", "rootroot") // Ihre Zugangsdaten hier
                .build();
    }

    public String uploadFile(MultipartFile file) {
        try {
            // Datei im MinIO Bucket speichern
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket("songs") // Ihr Bucket-Name hier
                            .object(file.getOriginalFilename()) // Originaler Dateiname als Objektname verwenden
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());

            // Rückgabe der URL zur hochgeladenen Datei
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket("songs") // Ihr Bucket-Name hier
                            .object(file.getOriginalFilename()) // Originaler Dateiname als Objektname verwenden
                            .build());
        } catch (InvalidResponseException | NoSuchAlgorithmException | ServerException | IOException |
                 XmlParserException | ErrorResponseException | InsufficientDataException | InternalException |
                 InvalidKeyException e) {
            throw new RuntimeException("Fehler beim Hochladen der Datei in Minio", e);
        }
    }
}

