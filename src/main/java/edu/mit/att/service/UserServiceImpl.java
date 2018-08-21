package edu.mit.att.service;


import edu.mit.att.entity.DepartmentsForm;
import edu.mit.att.entity.User;
import edu.mit.att.repository.DepartmentsFormRepository;
import edu.mit.att.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.logging.Logger;

@Service
public class UserServiceImpl implements UserService {

    private final static Logger LOGGER = Logger.getLogger(UserServiceImpl.class.getCanonicalName());

    @Resource
    private UserRepository userRepository;

    @Resource
    private DepartmentsFormRepository departmentsFormRepository;

    @Transactional
    public User create(User user) {
        return userRepository.save(user);
    }


    @Transactional
    public User create(User user, List<DepartmentsForm> departmentsForm) {
        // FIXME - Check Is this legit?
        user.setDepartmentsForms(new HashSet<>(departmentsFormRepository.findAll()));
        return userRepository.save(user);
    }


    @Transactional
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional
    public List<User> findAllAdmin() {
        List<User> adminForms = userRepository.findByIsadminTrueOrderByLastnameAscFirstnameAsc();
        return adminForms;
    }

    @Transactional
    public List<User> findAllNonadmin() {
        List<User> nonadminForms = userRepository.findByIsadminFalseOrderByLastnameAscFirstnameAsc();
        return nonadminForms;
    }

    @Transactional
    public boolean duplicate(String username, String firstname, String lastname, String email) {
        List<User> ufs = userRepository.findByUsernameAndFirstnameAndLastnameAndEmail(username, firstname, lastname, email);
        return ufs.size() > 0;
    }

    @Transactional
    public boolean duplicate(String username) {
        List<User> ufs = userRepository.findByUsername(username);
        return ufs.size() > 0;
    }


    @Transactional
    public Map<String, Object> get(int id) {
        Map<String, Object> info = new HashMap<String, Object>();
        User uf = userRepository.findById(id);
        if (uf == null) {
            return null;
        }
        info.put("id", Integer.toString(uf.getId()));
        info.put("username", uf.getUsername());
        info.put("last_name", uf.getLastname());
        info.put("first_name", uf.getFirstname());
        info.put("email", uf.getEmail());
        info.put("userenabled", uf.isEnabled() ? "1" : "0");
        info.put("is_admin", uf.isIsadmin() ? "1" : "0");
        return info;
    }

    @Transactional
    public List<User> findByName(String name) {
        List<User> newus = new ArrayList<User>();
        if (name == null) {
            return newus;
        }
        // assume name is "firstname lastname"
        String[] parts = name.split(" +");
        List<User> us = userRepository.findAll();
        if (parts.length != 2) {
            return newus;
        }
        for (User u : us) {
            if (parts[0].equals(u.getFirstname()) && parts[0].equals(u.getFirstname())) {
                newus.add(u);
            }
        }
        return newus;
    }

    @Transactional
    public List<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

}

