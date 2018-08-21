package edu.mit.att.api;


import java.util.List;

import edu.mit.att.entity.User;
import edu.mit.att.entity.UserBuilder;
import edu.mit.att.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Users {

    @Autowired
    private UserRepository userrepo;

    @RequestMapping("/users")
    public User getusers(@RequestParam(value = "username", defaultValue = "test") String name) {
        final List<User> userList = userrepo.findByUsername(name);

        if (userList.isEmpty()) {
            return new UserBuilder().createUsersForm();
        }
        return userList.get(0);
    }

    @RequestMapping("/users/all")
    public List<User> getusersAll() {
        final List<User> userList = userrepo.findAllByIdAfter(0);
        return userList; // TOOD paginate
    }
}
