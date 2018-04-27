package tds.support.tool.configuration.security.utils;

import org.springframework.core.io.UrlResource;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URLConnection;

/**
 * A UrlResource that sets Accept:application/octet-stream when requesting content.
 */
public class BinaryHttpResource extends UrlResource {
    /**
     * Construct a new resource with the given path. The path should *not* have the binary- prefix,
     * it should start with http:// or https://.
     *
     * @param path path
     * @throws MalformedURLException if path is not valid
     */
    BinaryHttpResource(final String path) throws MalformedURLException {
        super(path);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        // copied from super
        final URLConnection con = getURL().openConnection();
        con.setRequestProperty("Accept", "application/octet-stream");
        try {
            return con.getInputStream();
        }
        catch (IOException ex) {
            // Close the HTTP connection (if applicable).
            if (con instanceof HttpURLConnection) {
                ((HttpURLConnection) con).disconnect();
            }
            throw ex;
        }
    }
}
