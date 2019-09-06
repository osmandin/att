package edu.mit.att.test;

import edu.mit.att.entity.Department;
import edu.mit.att.entity.User;
import edu.mit.att.repository.DepartmentRepository;
import edu.mit.att.service.UserService;
import edu.mit.att.entity.UserBuilder;
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
    private DepartmentRepository departmentrepo;

    @Autowired
    private UserService userService;

    @Value("${testing.mail:testuser@mail.com}")
    private String email;

    @Value("${testing.username:testuser}")
    private String username;

    @Value("${testing.firstname:test}")
    private String firstName;

    @Value("${testing.lastname:test}")
    private String lastName;

    @Value("${testing.role:visitor}")
    private String role;

    @Value("${testing.department:none}")
    private String department;

    /**
     * Populates database with an initial admin record
     */
    @PostConstruct
    public void populateDatabase() {

        logger.debug("INIT");

        try {

            if (departmentrepo.findAll().isEmpty()) {
                final Department dept = new Department();
                dept.setName(department);
                departmentrepo.save(dept);
            }


            final User user = new UserBuilder().
                    setUsername(username).setEmail(email).setFirstname(firstName).setLastname(lastName).createUsersForm();
            user.setRole(role);
            user.setIsadmin(true); //TODO CHECK -- WHAT DOES THIS DO?

            final List<Department> departments = departmentrepo.findAll();

            // done to take care of Hibernate exception

            final Set<Department> departmentsSet = new HashSet<>(departments);
            user.setDepartments(departmentsSet);

            if (userService.findAll().isEmpty()) {
                userService.create(user, departments);
                logger.debug("Saved user:{}", userService.findAll());
            }

        } catch (Exception e) {
            logger.error("Error saving item:{}", e);
        }
    }

}
