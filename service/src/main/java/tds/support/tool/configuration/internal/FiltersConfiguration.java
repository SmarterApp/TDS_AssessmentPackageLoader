package tds.support.tool.configuration.internal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Restricts calls to internal endpoints to be on the internal client port.
 *
 * Adapted from https://tech.asimio.net/2016/12/15/Configuring-Tomcat-to-Listen-on-Multiple-ports-using-Spring-Boot.html
 */
public class FiltersConfiguration {

	@Value("${internalClientPort:${server.port}}")
	private Integer internalClientPort;

	@Bean(name = "internalClientRestrictingFilter")
	public FilterRegistrationBean internalClientRestrictingFilter() {
		Filter filter = new OncePerRequestFilter() {

			@Override
			protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain filterChain) throws ServletException, IOException {

				if (request.getLocalPort() == internalClientPort) {
					filterChain.doFilter(request, response);
				} else {
					response.sendError(404);
				}
			}
		};
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
		filterRegistrationBean.setFilter(filter);
		filterRegistrationBean.setOrder(-100);
		filterRegistrationBean.setName("internalClientPortRestriction");
		filterRegistrationBean.addUrlPatterns("/internal/*");
		return filterRegistrationBean;
	}
}
