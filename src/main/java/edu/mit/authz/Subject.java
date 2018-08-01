package edu.mit.authz;

import edu.mit.entity.UsersForm;
import edu.mit.service.UsersFormService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class Subject {

    private final Logger logger = getLogger(this.getClass());

    @Autowired
    private UsersFormService usersFormService;


    // TODO replace with a database:
    Map<String, String> realm =  new HashMap<>();


    //TODO replace static lookup:
    {
        //realm.put("osmandin@mit.edu",  Role.Constants.SITEADMIN);
        realm.put("karismith@mit.edu",  Role.Constants.SITEADMIN);
        realm.put("joecarrano@mit.edu",  Role.Constants.SITEADMIN);
    }

   public Role getRole(final String principal) {


        // final String principal_role = realm.get(principal);

       List<UsersForm> users = usersFormService.findByEmail(principal);

       if (users.isEmpty()) {
           return Role.visitor;
       }

       final String principal_role = users.get(0).getRole();

       logger.debug("Found role:{} for:{}", principal_role, principal);

        if (principal_role == null) {
            return Role.visitor;
        }

        logger.debug("Principal role:{}", principal_role);
        logger.info("Principal role:{}", principal_role);


        return Role.valueOf(principal_role);
    }

    boolean hasRole(final Role role) {
      // TODO
        return false;
    }



}
