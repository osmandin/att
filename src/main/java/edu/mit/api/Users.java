package edu.mit.api;


import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import edu.mit.entity.UsersForm;
import edu.mit.repository.UsersFormRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Users {

    @Autowired
    private UsersFormRepository userrepo;

    @RequestMapping("/users")
    public UsersForm getusers(@RequestParam(value = "username", defaultValue = "test") String name) {
        final List<UsersForm> userList = userrepo.findByUsername(name);

        if (userList.isEmpty()) {
            return new UsersForm();
        }
        return userList.get(0);
    }

    @RequestMapping("/users/all")
    public List<UsersForm> getusersAll() {
        final List<UsersForm> userList = userrepo.findAllByIdAfter(0);
        return userList; // TOOD paginate
    }
}
