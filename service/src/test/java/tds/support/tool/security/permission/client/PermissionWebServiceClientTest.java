package tds.support.tool.security.permission.client;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import tds.support.tool.configuration.SupportToolProperties;

import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class PermissionWebServiceClientTest {
    private PermissionWebServiceClient client;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private SupportToolProperties properties;

    @Before
    public void setup() {
        when(properties.getPermissionsUrl()).thenReturn("http://localhost:8998");
        client = new PermissionWebServiceClient(restTemplate, properties);
    }

    @Test
    public void shouldGetRole() {
        final Role role = new Role("ROLE", null, null);
        when(restTemplate.exchange(startsWith(properties.getPermissionsUrl() + "/role"), eq(HttpMethod.GET), eq(null), any(ParameterizedTypeReference.class), any(Map.class)))
                .thenReturn(new ResponseEntity<>(new Response<>("status", "message", newArrayList(role)), HttpStatus.OK));
        assertThat(client.getRole("Reporting").getValue().get(0)).isEqualTo(role);
    }

    @Test
    public void shouldCatchRestClientException() {
        when(restTemplate.exchange(startsWith(properties.getPermissionsUrl() + "/role"), eq(HttpMethod.GET), eq(null), any(ParameterizedTypeReference.class), any(Map.class)))
                .thenThrow(RestClientException.class);
        assertThat(client.getRole("Reporting")).isNull();
    }

}
