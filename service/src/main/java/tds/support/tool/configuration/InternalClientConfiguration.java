package tds.support.tool.configuration;

import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.DispatcherType;

@Configuration
public class InternalClientConfiguration  {

	@Bean(name = "internalClientFilter")
	public FilterRegistrationBean internalClientFilter() {
		final FilterRegistrationBean internalClient = new FilterRegistrationBean();
		internalClient.setFilter(new NoOpFilter());
		internalClient.setOrder(1);
		internalClient.setAsyncSupported(true);
		internalClient.setName("internalClient");
		internalClient.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ASYNC);
		internalClient.addUrlPatterns("/*");
		internalClient.addInitParameter("internal-client-path", "/internal/*");
		return internalClient;
	}

	// Note: if you have auto-proxy issues, you can add the following dependency
	// in your pom.xml:
	// <dependency>
	// <groupId>org.aspectj</groupId>
	// <artifactId>aspectjweaver</artifactId>
	// </dependency>
	@Bean
	public DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator() {
		return new DefaultAdvisorAutoProxyCreator();
	}
}