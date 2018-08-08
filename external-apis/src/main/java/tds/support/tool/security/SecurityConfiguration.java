package tds.support.tool.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import tds.support.tool.security.permission.PermissionService;
import tds.support.tool.security.permission.PermissionServiceImpl;
import tds.support.tool.security.permission.client.PermissionWebServiceClient;

/**
 * Default security configuration uses the real ForgeRockTokenServices and Authority/Permission services.
 * Note that the (basic) PermissionService will be wrapped by the CachingPermissionService.
 */
@Configuration
@ConditionalOnMissingBean(StubSecurityConfiguration.class)
class SecurityConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "security.oauth2")
    public ForgeRockTokenServices forgeRockTokenServices(final SbacTokenConverter tokenConverter) {
        return new ForgeRockTokenServices(tokenConverter);
    }

    @Bean
    public PermissionService basicPermissionService(final PermissionWebServiceClient client) {
        return new PermissionServiceImpl(client);
    }

    @Bean
    public PermissionWebServiceClient permissionWebServiceClient(
            @Value("${support-tool.permissions-url}") final String endpoint) {
        return new PermissionWebServiceClient(new RestTemplate(), endpoint);
    }
}
