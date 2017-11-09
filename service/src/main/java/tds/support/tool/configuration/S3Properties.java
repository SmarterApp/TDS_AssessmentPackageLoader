package tds.support.tool.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "data.s3")
public class S3Properties {
    private String accessKey;
    private String secretKey;
    private String bucketName;
    private String testPackagePrefix;

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(final String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(final String secretKey) {
        this.secretKey = secretKey;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(final String bucketName) {
        this.bucketName = bucketName;
    }

    public String getTestPackagePrefix() {
        return testPackagePrefix;
    }

    public void setTestPackagePrefix(final String testPackagePrefix) {
        this.testPackagePrefix = testPackagePrefix;
    }
}