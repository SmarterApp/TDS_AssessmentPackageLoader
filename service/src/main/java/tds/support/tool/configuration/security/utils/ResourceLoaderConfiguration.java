package tds.support.tool.configuration.security.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for enabling the binary http protocol resolver. To use do something like:<pre>
 * {@literal @}Import({ResourceLoaderConfiguration.class})
 *  public class Application { ... }
 * </pre>
 */
@Configuration
public class ResourceLoaderConfiguration {

    @Bean
    public ResourceLoaderPostProcessor resourceLoaderPostProcessor() {
        return new ResourceLoaderPostProcessor();
    }
}
