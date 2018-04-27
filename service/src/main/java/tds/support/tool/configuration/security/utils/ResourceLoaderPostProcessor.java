package tds.support.tool.configuration.security.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

/**
 * Processor for injecting our binary resource protocol resolver.
 * <p>
 * Although Spring provides a hook for {@link org.springframework.core.io.ProtocolResolver}'s they
 * don't make it easy to get one in place before the application context refresh() method is called.
 * By implementing {@link BeanFactoryPostProcessor} this component gets instantiated earlier during
 * that process so it has a chance to inject the resolver before the resource loader is used.
 * </p>
 */
public class ResourceLoaderPostProcessor implements ResourceLoaderAware, BeanFactoryPostProcessor {

    @Override
    public void setResourceLoader(final ResourceLoader resourceLoader) {
        if (resourceLoader instanceof DefaultResourceLoader) {
            ((DefaultResourceLoader)resourceLoader).addProtocolResolver(new BinaryHttpProtocolResolver());
        }
    }

    @Override
    public void postProcessBeanFactory(final ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // see class javadocs
    }
}
