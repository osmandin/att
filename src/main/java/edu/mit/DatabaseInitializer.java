package edu.mit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class DatabaseInitializer {

    private final Logger logger = getLogger(this.getClass());

    @Autowired
    public DatabaseInitializer() {
    }

    /**
     * Populates the database
     */
    @PostConstruct
    public void populateDatabase() {

        logger.debug("Initialize database here if you want to . . .");

    }

}
