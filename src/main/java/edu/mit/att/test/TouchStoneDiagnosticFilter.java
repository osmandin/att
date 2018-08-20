package edu.mit.att.test;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Filter to set http servlet variable when in development.
 *
 * Could also be used for displaying variables set up by Touchstone
 *
 * The filter is used when Touchstone is not in effect (e.g., for local development)
 */

@Component
@Order(1)
//@WebFilter(urlPatterns = {"/", "/Logout", "/*", "/**", "/Admin"})
@WebFilter
public class TouchStoneDiagnosticFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(TouchStoneDiagnosticFilter.class);

    // TODO: for testing only. Remove the annotation when using in production
    @Resource
    private Environment env;

    @Value("${testing.mail:osmandin@mit.edu}")
    private String email;


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.debug("Init filter");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        logger.info("Filter:" + ((HttpServletRequest) servletRequest).getRequestURL());


        final HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        // Information from TouchStone:
        // other Attributes are displayName, mail, nickname
        logger.info("Touchstone Attribute {} :{}", "mail", httpServletRequest.getAttribute("mail"));


        if (env != null && env.getRequiredProperty("testing.status").equals("true")) {
            logger.debug("Set mail value:{}", email);
            httpServletRequest.setAttribute("mail", email);
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}