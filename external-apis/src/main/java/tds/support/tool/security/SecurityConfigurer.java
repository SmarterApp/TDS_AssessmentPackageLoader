package tds.support.tool.security;

import org.springframework.boot.actuate.endpoint.AbstractEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfiguration;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

import java.util.Set;

/**
 * Security configuration uses oauth2.
 * <p>
 * This isn't really using OAuth2, it is using grant tokens from OpenAM to extract user
 * attributes, then using the tenancy chain to create authorities. From the client point
 * of view, it looks like OAuth2 because they are getting a password grant token and
 * submitting it with the request. However the resource server (this app) isn't relying
 * on OAuth2 for resource permissions.
 * </p>
 *
 * @see ForgeRockTokenServices
 */
@EnableWebSecurity
@EnableResourceServer
public class SecurityConfigurer extends WebSecurityConfigurerAdapter {

    private Set<AbstractEndpoint> actuatorEndpoints;

    public SecurityConfigurer(Set<AbstractEndpoint> actuatorEndpoints) {
        this.actuatorEndpoints = actuatorEndpoints;
    }

    /**
     * This configurer will be wired into {@link ResourceServerConfiguration} to override which
     * resources require authentication. This doesn't quite play nicely with the actuator endpoints
     * so, for example, /health will not vary based on authenticated or anonymous. It is important
     * that management security is not enabled when using this; currently, this assumes it will be
     * disabled by setting management.security.enabled=false in properties. Perhaps we could code
     * something here that explicitly disables ManagementWebSecurityAutoConfiguration behavior.
     *
     * @return a ResourceServerConfigurer
     */
    @Bean
    public ResourceServerConfigurer configurer() {
        return new ResourceServerConfigurer() {
            @Override
            public void configure(final ResourceServerSecurityConfigurer resources) {
                // no-op
            }

            @Override
            public void configure(final HttpSecurity http) throws Exception {
                for (final AbstractEndpoint endpoint : actuatorEndpoints) {
                    http.authorizeRequests()
                            .antMatchers("/" + endpoint.getId() + "/**")
                            .permitAll();
                }

                http.authorizeRequests()
                        .antMatchers("/api/**").authenticated()
                        .anyRequest().denyAll();
            }
        };
    }
}
