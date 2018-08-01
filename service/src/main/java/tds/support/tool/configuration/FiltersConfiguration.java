package tds.support.tool.configuration;

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

public class FiltersConfiguration {

	@Value("${internalClientPort:${server.port}}")
	private Integer internalClientPort;

	@Value("${internalClientPortOnly:true}")
	private Boolean internalClientPortOnly;

	@Bean(name = "internalClientRestrictingFilter")
	public FilterRegistrationBean internalClientRestrictingFilter(FilterRegistrationBean internalClientFilter) {
		Filter filter = new OncePerRequestFilter() {

			@Override
			protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain filterChain) throws ServletException, IOException {

				if (!internalClientPortOnly || request.getLocalPort() == internalClientPort) {
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
		filterRegistrationBean.addUrlPatterns(internalClientFilter.getInitParameters().get("internal-client-path"));
		return filterRegistrationBean;
	}
}
