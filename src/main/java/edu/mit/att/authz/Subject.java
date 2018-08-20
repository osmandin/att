package edu.mit.att.authz;

import edu.mit.att.service.UsersFormService;
import edu.mit.att.entity.UsersForm;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class Subject {

    private final Logger logger = getLogger(this.getClass());

    @Autowired
    private UsersFormService usersFormService;


   public Role getRole(final String principal) {

       final List<UsersForm> users = usersFormService.findByEmail(principal);

       if (users.isEmpty()) {
           return Role.visitor;
       }

       final String principal_role = users.get(0).getRole();

       logger.debug("Found role:{} for:{}", principal_role, principal);

        if (principal_role == null || principal_role.isEmpty()) {
            return Role.visitor;
        }

        return Role.valueOf(principal_role);
    }

}
