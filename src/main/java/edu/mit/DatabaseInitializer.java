package edu.mit;

import edu.mit.entity.DepartmentsForm;
import edu.mit.repository.DepartmentsFormRepository;
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

    @Autowired
    private DepartmentsFormRepository departmentrepo;

    /**
     * Populates the database
     */
    @PostConstruct
    public void populateDatabase() {

        logger.info("INIT");
        logger.debug("(Initialize database here if you want to. . .)");

        //FIXME this is only for testing purposes. remvoe later.

        DepartmentsForm departmentsForm = new DepartmentsForm();
        departmentsForm.setName("test department");


        try {
            departmentrepo.save(departmentsForm);
        } catch (Exception e) {
            logger.error("Error saving test item:{}", e);

        }

    }

}
