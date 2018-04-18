package tds.support.tool.security.permission.client;

import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    private RestTemplate oAuthRestTemplate;

    /**
     * Permission service REST API base URL
     */
    private String endpoint;

    @Autowired
    public PermissionWebServiceClient(@NotNull final RestTemplate oAuthRestTemplate,
                                      @NotNull final SupportToolProperties properties) {
        this.oAuthRestTemplate = checkNotNull(oAuthRestTemplate);
        this.endpoint = checkNotNull(properties.getPermissionsUrl());
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

    private <T> T get(String path, ParameterizedTypeReference<T> typeReference, Map<String, ?> parameters) {
        final String url = endpoint + path;
        try {
            return oAuthRestTemplate.exchange(url, HttpMethod.GET, null, typeReference, parameters).getBody();
        } catch (final RestClientException e) {
            logger.warn("Error getting {}{}: {}", url, parameters.isEmpty() ? "" : (" component=" + parameters.get("component")), e.getMessage());
            return null;
        }
    }
}
