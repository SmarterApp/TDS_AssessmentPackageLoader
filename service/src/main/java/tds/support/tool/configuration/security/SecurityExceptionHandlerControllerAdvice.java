package tds.support.tool.configuration.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;

/**
 * Security REST API exception handling
 */
@ControllerAdvice
public class SecurityExceptionHandlerControllerAdvice extends ResponseEntityExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(SecurityExceptionHandlerControllerAdvice.class);

    /**
     * Maps {@link AccessDeniedException} to HTTP 404 instead of 403.
     *
     * @param exception original exception
     * @param request   request resulting in exception
     * @return NOT_FOUND (404) response
     */
    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<Object> handleAccessDeniedException(final RuntimeException exception, final WebRequest request, final HttpServletRequest httpRequest) {
        logger.info("Access denied (): {}", httpRequest.getRequestURI());
        return createOpaqueError(exception, request, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Creates an opaque exception response with no granular exception details
     *
     * @param exception original exception
     * @param request   request resulting in exception
     * @param status    the status of the exception to send
     * @return a response with the given status
     */
    private ResponseEntity<Object> createOpaqueError(final RuntimeException exception, final WebRequest request, final HttpStatus status) {
        return handleExceptionInternal(exception, status.getReasonPhrase(), new HttpHeaders(), status, request);
    }
}
