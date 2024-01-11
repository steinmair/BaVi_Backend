package at.htlklu.bavi.minio;

public class FileNotFoundException extends MinioServiceException {

    public FileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileNotFoundException(String message) {
        super(message);
    }
}
