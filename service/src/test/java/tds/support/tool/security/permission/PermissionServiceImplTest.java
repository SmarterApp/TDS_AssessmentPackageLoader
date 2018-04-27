package tds.support.tool.security.permission;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import tds.support.tool.security.permission.client.*;

import java.util.Collection;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PermissionServiceImplTest {
    private PermissionService service;

    @Mock
    private PermissionWebServiceClient mockPermissionWebServiceClient;

    @Before
    public void setup() {
        service = new PermissionServiceImpl(mockPermissionWebServiceClient);
    }

    @Test(expected = NullPointerException.class)
    public void constructorShouldThrowExceptionForNullParameters() {
        new PermissionServiceImpl(null);
    }

    @Test(expected = UnauthorizedUserException.class)
    public void getPermissionsByRole_client_request_failure_should_result_in_exception() throws Exception {
        when(mockPermissionWebServiceClient.getRole("Support Tool")).thenReturn(new Response<>(Response.Status.FAILURE, "message",null));
        service.getPermissionsByRole();
    }

    @Test
    public void getPermissionsByRole_should_return_all_role_to_permission_mappings() throws Exception {
        when(mockPermissionWebServiceClient.getRole("Support Tool")).thenReturn(
                new Response<>(Response.Status.SUCCESS, null, newArrayList(
                        new Role("role1", newArrayList(
                                new Component("Support Tool", newArrayList(
                                        new Permission("permission1")
                                ))
                        ), newArrayList()),
                        new Role("role2", newArrayList(
                                new Component("Support Tool", newArrayList(
                                        new Permission("permission2"),
                                        new Permission("permission3")
                                ))
                        ), newArrayList())
                )));
        final Map<String, Collection<String>> actual = service.getPermissionsByRole();
        final Map<String, Collection<String>> expected = ImmutableMap.of(
                "role1", ImmutableSet.of("permission1"),
                "role2", ImmutableSet.of("permission2", "permission3")
        );
        assertThat("response should contain all roles and permissions", actual, equalTo(expected));
    }
}
