package tds.support.tool.configuration;


import javax.servlet.*;
import java.io.IOException;

/**
 * Created by Greg Charles on 8/1/18.
 */
public class NoOpFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
