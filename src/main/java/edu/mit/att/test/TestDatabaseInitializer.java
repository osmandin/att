package edu.mit.att.test;

import edu.mit.att.service.UsersFormService;
import edu.mit.att.entity.DepartmentsForm;
import edu.mit.att.entity.UsersForm;
import edu.mit.att.entity.UsersFormBuilder;
import edu.mit.att.repository.DepartmentsFormRepository;
import edu.mit.att.service.SsasFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class TestDatabaseInitializer {

    private final Logger logger = getLogger(this.getClass());

    @Autowired
    public TestDatabaseInitializer() {
    }

    @Autowired
    private DepartmentsFormRepository departmentrepo;

    @Autowired
    private UsersFormService usersFormService;

    @Autowired
    private SsasFormService ssasFormService;

    @Value("${testing.mail:johndoe@mail.com}")
    private String email;

    @Value("${testing.username:johndoe}")
    private String username;

    @Value("${testing.firstname:john}")
    private String firstName;

    @Value("${testing.lastname:doe}")
    private String lastName;

    @Value("${testing.role:visitor}")
    private String role;

    @Value("${testing.department:iasc}")
    private String department;

    /**
     * Populates database with test record
     */
    @PostConstruct
    public void populateDatabase() {

        logger.info("INIT");
        logger.debug("INIT");

        //FIXME this is only for testing purposes. remvoe later.

        try {
            final DepartmentsForm dept = new DepartmentsForm();
            dept.setName(department);
            departmentrepo.save(dept);

            final UsersForm user = new UsersFormBuilder().
                    setUsername(username).setEmail(email).setFirstname(firstName).setLastname(lastName).createUsersForm();
            user.setRole(role);
            user.setIsadmin(true); //FIXME CHECK -- WHAT DOES THIS DO?

            final List<DepartmentsForm> departments = departmentrepo.findAll();

            // done to take care of Hibernate exception

            final Set<DepartmentsForm> departmentsSet = new HashSet<>(departments);
            user.setDepartmentsForms(departmentsSet);
            usersFormService.create(user, departments);
            logger.debug("Saved user:{}", usersFormService.findAll());
        } catch (Exception e) {
            logger.error("Error saving test item:{}", e);
        }
    }

}
