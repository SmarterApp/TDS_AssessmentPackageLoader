package tds.support.tool.configuration.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

/**
 * An authentication entry point which returns 401 Unauthorized without
 * a challenge or redirection for SSO authentication.
 */
public class Http401NotAuthorizedNoChallengeEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(final HttpServletRequest request,
                         final HttpServletResponse response,
                         final AuthenticationException authException) {
        response.setStatus(SC_UNAUTHORIZED);
    }
}