package tds.support.tool.configuration.security.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ProtocolResolver;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.net.MalformedURLException;

/**
 * A protocol resolver for binary-http/s
 */
public class BinaryHttpProtocolResolver implements ProtocolResolver {
    private static final Logger logger = LoggerFactory.getLogger(BinaryHttpProtocolResolver.class);

    private static final String BinaryPrefix = "binary-";
    private static final String BinaryHttpPrefix = BinaryPrefix + "http://";
    private static final String BinaryHttpsPrefix = BinaryPrefix + "https://";

    @Override
    public Resource resolve(final String location, final ResourceLoader resourceLoader) {
        if (location.startsWith(BinaryHttpPrefix) || location.startsWith(BinaryHttpsPrefix)) {
            try {
                return new BinaryHttpResource(location.substring(BinaryPrefix.length()));
            } catch (final MalformedURLException e) {
                logger.warn("Error resolving " + location);
            }
        }
        return null;
    }
}
