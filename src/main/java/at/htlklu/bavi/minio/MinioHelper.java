package at.htlklu.bavi.minio;

public class MinioHelper {

    public static String prepareMinioBucketName(String bucketName) {
        bucketName = bucketName.toLowerCase();
        return bucketName;
    }
}
