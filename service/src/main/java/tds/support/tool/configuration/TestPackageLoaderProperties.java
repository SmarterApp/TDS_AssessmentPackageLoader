package tds.support.tool.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Configuration for the test package loader.
 */
@Component
@ConfigurationProperties(prefix = "test-package-loader")
public class TestPackageLoaderProperties {
    private String artRestUrl;
    private String assessmentUrl;
    private String thssApiUrl;
    private String tisApiUrl;

    /**
     * @return URL that points to the deployed instance of Administration and Registration Tools (ART) REST component.
     */
    public Optional<String> getArtRestUrl() {
        return Optional.ofNullable(artRestUrl);
    }

    public void setArtRestUrl(final String artRestUrl) {
        this.artRestUrl = removeTrailingSlash(artRestUrl);
    }

    /**
     * @return URL that points to the deployed Assessment microservice.
     */
    public String getAssessmentUrl() {
        return assessmentUrl;
    }

    public void setAssessmentUrl(final String assessmentUrl) {
        if (assessmentUrl == null) throw new IllegalArgumentException("asssessmentUrl cannot be null");
        this.assessmentUrl = removeTrailingSlash(assessmentUrl);
    }

    /**
     * @return URL that points to the deployed instance of the Teacher Hand-Scoring System (THSS) API endpoint(s).
     */
    public Optional<String> getThssApiUrl() {
        return Optional.ofNullable(thssApiUrl);
    }

    public void setThssApiUrl(final String thssApiUrl) {
        this.thssApiUrl = removeTrailingSlash(thssApiUrl);
    }

    /**
     * @return URL that points to the deployed instance of the Test Integration System (TIS) API endpoint(s).
     */
    public Optional<String> getTisApiUrl() {
        return Optional.ofNullable(tisApiUrl);
    }

    public void setTisApiUrl(final String tisApiUrl) {
        this.tisApiUrl = removeTrailingSlash(tisApiUrl);
    }

    /**
     * Remove the trailing slash from a URL (if one exists)
     *
     * @param url The URL to change
     * @return The URL with the trailing slash removed
     */
    private String removeTrailingSlash(final String url) {
        if (url != null && url.endsWith("/")) {
            return url.substring(0, url.length() - 1);
        } else {
            return url;
        }
    }
}
