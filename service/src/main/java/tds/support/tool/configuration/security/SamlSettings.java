package tds.support.tool.configuration.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "saml")
public class SamlSettings {

    private String keyStoreFile;
    private String keyStorePassword;
    private String privateKeyEntryAlias;
    private String privateKeyEntryPassword;
    private String idpMetadataUrl;
    private String spEntityId;
    private Integer maxAuthenticationAge;
    private String logoutRedirectUrl = "/";
    private boolean globalLogout = true;
    private LoadBalancedSettings loadBalance = new LoadBalancedSettings();

    /**
     * @return The full file path to the Java KeyStore (JKS) file.
     */
    public String getKeyStoreFile() {
        return keyStoreFile;
    }

    public void setKeyStoreFile(final String keyStoreFile) {
        this.keyStoreFile = keyStoreFile;
    }

    /**
     * @return Password for the JKS File.
     */
    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public void setKeyStorePassword(final String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    /**
     * @return Private key alias in JKS used for SAML signing.
     */
    public String getPrivateKeyEntryAlias() {
        return privateKeyEntryAlias;
    }

    public void setPrivateKeyEntryAlias(final String privateKeyEntryAlias) {
        this.privateKeyEntryAlias = privateKeyEntryAlias;
    }

    /**
     * @return Private key alias password. May be same as JKS file password.
     */
    public String getPrivateKeyEntryPassword() {
        return privateKeyEntryPassword;
    }

    public void setPrivateKeyEntryPassword(final String privateKeyEntryPassword) {
        this.privateKeyEntryPassword = privateKeyEntryPassword;
    }

    /**
     * @return Identity provider metadata URL.
     */
    public String getIdpMetadataUrl() {
        return idpMetadataUrl;
    }

    public void setIdpMetadataUrl(final String idpMetadataUrl) {
        this.idpMetadataUrl = idpMetadataUrl;
    }

    /**
     * @return Service Provider entity id as registered in the IDP circle of trust.
     */
    public String getSpEntityId() {
        return spEntityId;
    }

    public void setSpEntityId(final String spEntityId) {
        this.spEntityId = spEntityId;
    }

    /**
     * @return  Load balancing settings; configuring SAML with a known scheme/host/port/contextPath
     *          when behind a load balancer or reverse proxy.
     */
    public LoadBalancedSettings getLoadBalance() {
        return loadBalance;
    }

    public void setLoadBalance(final LoadBalancedSettings loadBalance) {
        this.loadBalance = loadBalance;
    }

    /**
     * @return base url built from scheme, host and optional port; null if proxy is not enabled
     */
    public String getEntityBaseUrl() {
        return loadBalance.getBaseUrl();
    }

    /**
     * @return maximum time in seconds that system allows users to single sign-on since their initial authentication
     * with the IDP. Setting this to a very high value can avoid getting hard-to-resolve "access denied"
     * errors in some situations.
     */
    public Integer getMaxAuthenticationAge() {
        return maxAuthenticationAge;
    }

    public void setMaxAuthenticationAge(final Integer maxAuthenticationAge) {
        this.maxAuthenticationAge = maxAuthenticationAge;
    }

    /**
     * @return URL to redirect to after a successful logout.
     */
    public String getLogoutRedirectUrl() {
        return logoutRedirectUrl;
    }

    public void setLogoutRedirectUrl(final String logoutRedirectUrl) {
        this.logoutRedirectUrl = logoutRedirectUrl;
    }

    /**
     * @return true if performing SAML single logout, false if local logout only.
     * IDP must support single logout if this flag is true.
     */
    public boolean isGlobalLogout() {
        return globalLogout;
    }

    public void setGlobalLogout(final boolean globalLogout) {
        this.globalLogout = globalLogout;
    }

    /**
     * SAML configuration when server is hosted behind a load balancer or reverse proxy.
     */
    public static class LoadBalancedSettings {
        private boolean enabled = false;
        private String scheme = "https";
        private String hostname;
        private String contextPath = "/";
        private int port;
        private boolean includeServerPort = false;

        /**
         * @return True to use load balancer settings when configuring SAML
         */
        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(final boolean enabled) {
            this.enabled = enabled;
        }

        /**
         * @return The scheme used when communicating with the server
         */
        public String getScheme() {
            return scheme;
        }

        public void setScheme(final String scheme) {
            this.scheme = scheme;
        }

        /**
         * @return The host name used when communicating with the server
         */
        public String getHostname() {
            return hostname;
        }

        public void setHostname(final String hostname) {
            this.hostname = hostname;
        }

        /**
         * @return The context path used when communicating with the server
         */
        public String getContextPath() {
            return contextPath;
        }

        public void setContextPath(final String contextPath) {
            this.contextPath = contextPath;
        }

        /**
         * @return The port used when communicating with the server; only applies when
         *         {@link #isIncludeServerPort()} is true.
         */
        public int getPort() {
            return port;
        }

        public void setPort(final int port) {
            this.port = port;
        }

        /**
         * @return True to include the defined port in the server url
         */
        public boolean isIncludeServerPort() {
            return includeServerPort;
        }

        public void setIncludeServerPort(final boolean includeServerPort) {
            this.includeServerPort = includeServerPort;
        }

        /**
         * @return base url built from scheme, host and optional port; null if proxy is not enabled
         */
        public String getBaseUrl() {
            if (!enabled) return null;

            final StringBuilder sb = new StringBuilder();
            sb.append(scheme).append("://").append(hostname);
            if (includeServerPort) {
                sb.append(":").append(getPort());
            }
            return sb.toString();
        }
    }
}
