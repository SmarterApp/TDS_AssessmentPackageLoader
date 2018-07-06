package tds.support.tool.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Configuration for the test package loader.
 * <p>
 *     If ART, TIS and/or THSS are not configured in the environment the Support Tool is intended to support, those
 *     properties should be left blank/empty.  An environment without ART can still run in "GUEST" mode (that is,
 *     users can take exams without a proctored session).  An environment without TIS or THSS can take exams through to
 *     completion, but cannot have those exams scored.
 *
 *     Conversely, the Support Tool requires an instance of TDS to exist; otherwise there is no system to load test
 *     packages into.
 * </p>
 */
@Component
@ConfigurationProperties(prefix = "support-tool")
public class SupportToolProperties {
    private String artRestUrl;
    private String assessmentUrl;
    private String examUrl;
    private String contentUrl;
    private String thssApiUrl;
    private String tisApiUrl;
    private String progmanUrl;
    private String progmanTenant;
    private String progmanTenantLevel;
    private String permissionsUrl;
    private String SsoUrl;
    private String SsoUsername;
    private String SsoPassword;
    private String SsoClientSecret;
    private String SsoClientId;

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

    public String getExamUrl() {
        return examUrl;
    }

    public void setExamUrl(final String examUrl) {
        if (examUrl == null) throw new IllegalArgumentException("examUrl cannot be null");
        this.examUrl = removeTrailingSlash(examUrl);
    }

    /**
     * @return URL that points to the deployed Content microservice.
     */
    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(final String contentUrl) {
        if (contentUrl == null) throw new IllegalArgumentException("contentUrl cannot be null");
        this.contentUrl = removeTrailingSlash(contentUrl);
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
     * @return URL that points to the deployed instance of Progman REST component,
     *   e.g.: http://progman-web-deployment.sbtds.org:8080/rest/
     */
    public Optional<String> getProgmanUrl() {
        return Optional.ofNullable(progmanUrl);
    }

    public void setProgmanUrl(final String progmanUrl) {
        this.progmanUrl = removeTrailingSlash(progmanUrl);
    }

    /**
     * @return Progman tenant, e.g.: CA
     */
    public Optional<String> getProgmanTenant() {
        return Optional.ofNullable(progmanTenant);
    }

    public void setProgmanTenant(final String progmanTenant) {
        this.progmanTenant = progmanTenant;
    }

    /**
     * @return Progman tenant level, e.g.: STATE
     */
    public Optional<String> getProgmanTenantLevel() {
        return Optional.ofNullable(progmanTenantLevel);
    }

    public void setProgmanTenantLevel(final String progmanTenantLevel) {
        this.progmanTenantLevel = progmanTenantLevel;
    }

    /**
     * @return SSO endpoint, e.g.: https://sso-deployment.sbtds.org/auth/oauth2/access_token?realm=/sbac
     */
    public Optional<String> getSsoUrl() {
        return Optional.ofNullable(SsoUrl);
    }

    public void setSsoUrl(final String ssoUrl) {
        this.SsoUrl = removeTrailingSlash(ssoUrl);
    }

    /**
     * @return SSO username, e.g.: user@example.com
     */
    public Optional<String> getSsoUsername() {
        return Optional.ofNullable(SsoUsername);
    }

    public void setSsoUsername(final String ssoUsername) {
        this.SsoUsername = ssoUsername;
    }

    /**
     * @return SSO password, e.g.: password
     */
    public Optional<String> getSsoPassword() {
        return Optional.ofNullable(SsoPassword);
    }

    public void setSsoPassword(final String ssoPassword) {
        this.SsoPassword = ssoPassword;
    }

    /**
     * @return SSO client secret, e.g.: mySecret12345
     */
    public Optional<String> getSsoClientSecret() {
        return Optional.ofNullable(SsoClientSecret);
    }

    public void setSsoClientSecret(final String ssoClientSecret) {
        this.SsoClientSecret = ssoClientSecret;
    }

    /**
     * @return SSO client ID, e.g.: pm
     */
    public Optional<String> getSsoClientId() {
        return Optional.ofNullable(SsoClientId);
    }

    public void setSsoClientId(final String ssoClientId) {
        this.SsoClientId = ssoClientId;
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

    public String getPermissionsUrl() {
        return permissionsUrl;
    }

    public void setPermissionsUrl(final String permissionsUrl) {
        this.permissionsUrl = permissionsUrl;
    }

}
