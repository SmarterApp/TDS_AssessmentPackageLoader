package tds.support.tool.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tds.support.tool.security.permission.PermissionService;

/**
 * Stub security configuration uses the StubTokenServices and StubPermissionService.
 * Note that the (basic) PermissionService will be wrapped by the CachingPermissionService.
 */
@Configuration
@ConditionalOnProperty(name = "security.oauth2.token-info-url", havingValue = "local")
class StubSecurityConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(StubSecurityConfiguration.class);

    @Bean
    public StubTokenServices stubTokenServices(final SbacTokenConverter tokenConverter) {
        logger.warn("Configuring stub token services intended for testing and local development only!");
        return new StubTokenServices(tokenConverter);
    }

    @Bean
    public PermissionService basicPermissionService() {
        return new StubPermissionService();
    }
}
