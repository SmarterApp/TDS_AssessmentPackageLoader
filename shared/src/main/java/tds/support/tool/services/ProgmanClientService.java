package tds.support.tool.services;

import org.springframework.security.access.prepost.PreAuthorize;

public interface ProgmanClientService {
    /**
     * Get the application's configured tenant ID (fetched from progman).
     *
     * @return The application's configured tenant ID.
     */
    String getTenantId();
}
