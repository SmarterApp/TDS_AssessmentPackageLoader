package tds.support.tool.services;

public interface ProgmanClientService {
    /**
     * Get the application's configured tenant ID (fetched from progman).
     *
     * @return The application's configured tenant ID.
     */
    public String getTenantId();
}
