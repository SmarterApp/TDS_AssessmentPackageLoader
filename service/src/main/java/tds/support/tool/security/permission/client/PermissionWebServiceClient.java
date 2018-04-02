package tds.support.tool.security.permission.client;

import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import tds.support.tool.configuration.SupportToolProperties;

import javax.validation.constraints.NotNull;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

@Service
public class PermissionWebServiceClient {
    private static final Logger logger = LoggerFactory.getLogger(PermissionWebServiceClient.class);

    /**
     * Spring REST Template used to connect to the permission service REST API
     */
    private RestTemplate restTemplate;

    /**
     * Permission service REST API base URL
     */
    private String endpoint;

    public PermissionWebServiceClient(@NotNull final RestTemplate restTemplate,
                                      @NotNull final SupportToolProperties properties) {
        this.restTemplate = checkNotNull(restTemplate);
        this.endpoint = checkNotNull(properties.getPermissionsUrl());
    }

    /**
     * Gets all components and the permissions associated with these components
     *
     * @return response containing all registered components
     */
    public Response<Component> getComponents() {
        return get("/component",
                new ParameterizedTypeReference<Response<Component>>() {},
                ImmutableMap.of());
    }

    /**
     * Gets the component and permissions associated with the given component name
     *
     * @param component Component name as registered in the permission management system
     * @return response containing the matching component or a response with a failure status if no matching component is found
     */
    public Response<Component> getComponent(final String component) {
        return get("/component?component={component}",
                new ParameterizedTypeReference<Response<Component>>() {},
                ImmutableMap.of("component", component));
    }

    /**
     * Gets all roles and the components and permissions associated with the roles
     *
     * @return response containing all registered roles
     */
    public Response<Role> getRoles() {
        return get("/role",
                new ParameterizedTypeReference<Response<Role>>() {},
                ImmutableMap.of());
    }

    /**
     * Gets the role and the components and permissions associated with that role for the given component name
     *
     * @param component Component name as registered in the permission management system
     * @return response containing the matching role or a response with a failure status if no matching component is found
     */
    public Response<Role> getRole(final String component) {
        return get("/role?component={component}",
                new ParameterizedTypeReference<Response<Role>>() {},
                ImmutableMap.of("component", component));
    }

    /**
     * Gets all permissions
     *
     * @return response containing all registered permissions
     */
    public Response<Permission> getPermissions() {
        return get("/permission",
                new ParameterizedTypeReference<Response<Permission>>() {},
                ImmutableMap.of());
    }


    private <T> T get(String path, ParameterizedTypeReference<T> typeReference, Map<String, ?> parameters) {
        final String url = endpoint + path;
        try {
            return restTemplate.exchange(url, HttpMethod.GET, null, typeReference, parameters).getBody();
        } catch (final RestClientException e) {
            logger.warn("Error getting {}{}: {}", url, parameters.isEmpty() ? "" : (" component=" + parameters.get("component")), e.getMessage());
            return null;
        }
    }
}
