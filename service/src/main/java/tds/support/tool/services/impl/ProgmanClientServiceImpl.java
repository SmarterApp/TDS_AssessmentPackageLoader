package tds.support.tool.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;

import tds.support.tool.configuration.SupportToolProperties;
import tds.support.tool.services.ProgmanClientService;

@Service
public class ProgmanClientServiceImpl implements ProgmanClientService {

    private static final Logger logger = LoggerFactory.getLogger(ProgmanClientServiceImpl.class);
    private final OAuth2RestTemplate restTemplate;
    private String progmanURL;
    private String progmanTenant;
    private String progmanTenantLevel;

    @Autowired
    public ProgmanClientServiceImpl(SupportToolProperties supportToolProperties, OAuth2RestTemplate oAuth2RestTemplate) {
        this.restTemplate = oAuth2RestTemplate;
        this.progmanURL = supportToolProperties.getProgmanUrl().orElseThrow(() -> new RuntimeException(
            "Progman Url property is not configured"));
        this.progmanTenant = supportToolProperties.getProgmanTenant().orElseThrow(() -> new RuntimeException(
            "Progman Tenant property is not configured"));
        this.progmanTenantLevel = supportToolProperties.getProgmanTenantLevel().orElseThrow(() -> new RuntimeException(
            "Progman Tenant Level property is not configured"));
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
            ArrayList<Map> results = (ArrayList) restTemplate.getForObject(progmanURL +
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
}
