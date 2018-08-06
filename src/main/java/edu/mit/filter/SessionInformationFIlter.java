package edu.mit.filter;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Enumeration;

/**
 * Debubbing filter, used to set session variables
 */



@Component // necessary for localhost?
@Order(1)
//@WebFilter(urlPatterns = {"/", "/Logout", "/*", "/**", "/Admin"})
@WebFilter
public class SessionInformationFIlter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(SessionInformationFIlter.class);

    // TODO: for testing
    //@Resource
    private Environment env;


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.debug("Init filter");
        logger.info("Init filter");

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        logger.info("filter:" + ((HttpServletRequest) servletRequest).getRequestURL());

        //logger.info("info:{}" , ((HttpServletRequest) servletRequest).getRequestURL());
        //logger.info("secure info:{}" , ((HttpServletRequest) servletRequest).isSecure());

        Enumeration<String> headerNames = ((HttpServletRequest) servletRequest).getHeaderNames();

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        HttpSession session = httpServletRequest.getSession(false);

        if (session == null) {
            //logger.info("Session null. Creating session");
            //session = httpServletRequest.getSession();
        } else {
            //logger.debug("Retrieved mail from session:{}", session.getAttribute("mail"));
            //logger.info("Retrieved mail from session:{}", session.getAttribute("mail"));
            //session.setAttribute("mail", null);
        }


        //logger.info("Header Information:");

        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String s = headerNames.nextElement();
                String v = ((HttpServletRequest) servletRequest).getHeader(s);
                //logger.info("Header: {} Value:{}", s, v);

                /*if (httpServletRequest.getRequestURL().toString().contains("https://")) { // To get around the referrer problem
                    session.setAttribute("safe", "true");
                } else {
                    logger.info("request url:{}", httpServletRequest.getRequestURL().toString());
                }*/
            }
        }


        /*
        final Enumeration<String> sessionNames = session.getAttributeNames();

        while (sessionNames.hasMoreElements()) {
            try {
                String s = sessionNames.nextElement();
                String v = (String) session.getAttribute(s);
                logger.info("Session:{} {}", s, v);
            } catch (Exception e) {
            }
        }
        */

       /* final Enumeration<String> attributeNames = httpServletRequest.getAttributeNames();


        while (attributeNames.hasMoreElements()) {
            try {
                String s = attributeNames.nextElement();
                String v = (String) httpServletRequest.getAttribute(s);
                logger.info("Attribute element:{} {}", s, v);
            } catch (Exception e) {
            }
        }*/

        // Information from TouchStone:

        //logger.info("Touchstone Attrib {}: {}", "displayName", httpServletRequest.getAttribute("displayName"));
        logger.info("Touchstone Attrib {}: {}", "mail", httpServletRequest.getAttribute("mail"));
        //logger.info("Touchstone Attrib {}: {}", "nickname", httpServletRequest.getAttribute("nickname"));


/*        logger.debug("Touchstone Attrib:{}", httpServletRequest.getAttribute("displayName"));
        logger.debug("Touchstone Attrib:{}", httpServletRequest.getAttribute("mail"));
        logger.debug("Touchstone Attrib:{}", httpServletRequest.getAttribute("nickname"));*/

        //session.setAttribute("mail", httpServletRequest.getAttribute("mail"));

        if (env != null && env.getRequiredProperty("testing.status").equals("true")) {
            httpServletRequest.setAttribute("mail", "osmandin@mit.edu");        }

        if (httpServletRequest.getAttribute("mail") == null) { //TODO
           // httpServletRequest.setAttribute("mail", "osmandin@mit.edu");
        }


        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}