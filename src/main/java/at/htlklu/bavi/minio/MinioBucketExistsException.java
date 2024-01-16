package at.htlklu.bavi.minio;

public class MinioBucketExistsException extends MinioServiceException {
    public MinioBucketExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public MinioBucketExistsException(String message) {
        super(message);
    }
}
