package edu.mit.controllers;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PropertiesConfigurationUtil {

    private static final Logger logger = LoggerFactory.getLogger(PropertiesConfigurationUtil.class);

    /**
     * For getting login details
     */
    private static Configuration login;


    static {
        try {
            login = new PropertiesConfiguration("connection.props");
        } catch (Exception e) {
            logger.error("Error setting property file:", e);
            logger.error("Exiting due to configuration error.");
            System.exit(1);
        }
    }

    public static Credentials getCredentials() {
        final Credentials credentials = new Credentials();
        credentials.setPassword(login.getProperty("login_password").toString());
        credentials.setUrl(login.getProperty("login_url").toString());
        credentials.setUsername_app(login.getProperty("app_username").toString());
        credentials.setPassword_app(login.getProperty("app_password").toString());
        return credentials;
    }
}