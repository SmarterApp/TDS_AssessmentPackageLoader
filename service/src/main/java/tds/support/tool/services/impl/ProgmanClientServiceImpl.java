package tds.support.tool.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperFactoryBean;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;

import java.util.ArrayList;
import java.util.Map;

import tds.support.tool.configuration.SupportToolProperties;
import tds.support.tool.services.ProgmanClientService;

@Service
public class ProgmanClientServiceImpl implements ProgmanClientService {

    private static final Logger logger = LoggerFactory.getLogger(ProgmanClientServiceImpl.class);

    private String progmanURL;
    private String progmanTenant;
    private String progmanTenantLevel;
    private String ssoURL;
    private String ssoUsername;
    private String ssoPassword;
    private String ssoClientId;
    private String ssoClientSecret;

    @Autowired
    public ProgmanClientServiceImpl(SupportToolProperties supportToolProperties) {
        this.progmanURL = supportToolProperties.getProgmanUrl().orElseThrow(() -> new RuntimeException(
            "Progman Url property is not configured"));
        this.progmanTenant = supportToolProperties.getProgmanTenant().orElseThrow(() -> new RuntimeException(
            "Progman Tenant property is not configured"));
        this.progmanTenantLevel = supportToolProperties.getProgmanTenantLevel().orElseThrow(() -> new RuntimeException(
            "Progman Tenant Level property is not configured"));
        this.ssoURL = supportToolProperties.getSsoUrl().orElseThrow(() -> new RuntimeException(
            "SSO Url property is not configured"));
        this.ssoUsername = supportToolProperties.getSsoUsername().orElseThrow(() -> new RuntimeException(
            "SSO Username property is not configured"));
        this.ssoPassword = supportToolProperties.getSsoPassword().orElseThrow(() -> new RuntimeException(
            "SSL Password property is not configured"));
        this.ssoClientSecret = supportToolProperties.getSsoClientSecret().orElseThrow(() -> new RuntimeException(
            "SSL Client Secret property is not configured"));
        this.ssoClientId = supportToolProperties.getSsoClientId().orElseThrow(() -> new RuntimeException(
            "SSL Client ID property is not configured"));
    }

    /**
     * Fetch tenantId from progman. Searches tenants using algorithm found at
     * https://github.com/SmarterApp/TDS_Administrative/blob/master/tsb/load_reg_package.pl#L187
     *
     * @return tenant ID fetched from progman.
     */
    public String getTenantId() {
        try {
            logger.debug("Calling Program Management to get tenant ID.");
            ArrayList<Map> results = (ArrayList) progmanRestTemplate().getForObject(progmanURL +
                "/tenant?name=" + progmanTenant + "&type=" + progmanTenantLevel, Map.class).get("searchResults");
            logger.debug(results.size() + " tenants found. Searching for correct tenant.");
            // find the correct tenant and lookup the ID (usually only get 1, per query in URL)
            for (Map<String, String> result : results) {
                logger.debug("Found tenant name " + result.get("name") + " type " + result.get("type"));
                if (result.get("name").equals(progmanTenant) && result.get("type").equals(progmanTenantLevel)) {
                    logger.info("Fetched tenant id from progman: " + result.get("id"));
                    return result.get("id");
                }
            }
            throw new RuntimeException(
                "Tenant not found in progman for tenant " + progmanTenant + " level " + progmanTenantLevel);
        } catch (Exception e) {
            logger.error("Failure looking up tenant ID in Progman");
            throw new RuntimeException(e);
        }
    }

    /**
     * From org.opentestsystem.shared.progman.client.config.ProgramManagementIntegratedClientConfig
     */
    private RestOperations progmanRestTemplate() {
        final OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(progmanResource());
        for (final HttpMessageConverter<?> messageConverter : restTemplate.getMessageConverters()) {
            if (messageConverter instanceof MappingJackson2HttpMessageConverter) {
                final MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = (MappingJackson2HttpMessageConverter) messageConverter;
                mappingJackson2HttpMessageConverter.setObjectMapper(progmanObjectMapper());
                break;
            }
        }
        return restTemplate;
    }

    /**
     * From org.opentestsystem.shared.progman.client.config.ProgramManagementIntegratedClientConfig
     */
    private OAuth2ProtectedResourceDetails progmanResource() {
        final ResourceOwnerPasswordResourceDetails resourceDetails = new ResourceOwnerPasswordResourceDetails();
        resourceDetails.setUsername(ssoUsername);
        resourceDetails.setPassword(ssoPassword);
        resourceDetails.setClientId(ssoClientId);
        resourceDetails.setClientSecret(ssoClientSecret);
        resourceDetails.setAccessTokenUri(ssoURL);
        resourceDetails.setClientAuthenticationScheme(AuthenticationScheme.form);
        return resourceDetails;
    }

    /**
     * From org.opentestsystem.shared.progman.client.config.ProgramManagementIntegratedClientConfig
     */
    private ObjectMapper progmanObjectMapper() {
        final Jackson2ObjectMapperFactoryBean omfb = new Jackson2ObjectMapperFactoryBean();
        omfb.setIndentOutput(true);
        omfb.setSimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        omfb.afterPropertiesSet();

        final ObjectMapper mapper = omfb.getObject();
        mapper.registerModule(new JodaModule());

        return mapper;
    }
}
