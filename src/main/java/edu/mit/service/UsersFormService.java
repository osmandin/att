package edu.mit.service;

import edu.mit.entity.*;

import java.util.List;
import java.util.Map;

public interface UsersFormService {
    public UsersForm create(UsersForm usersForm);

    public UsersForm create(UsersForm usersForm, List<DepartmentsForm> departmentsForms);


    public List<UsersForm> findAllAdmin();

    public List<UsersForm> findAllNonadmin();

    public boolean duplicate(String username, String firstname, String lastname, String email);

    public boolean duplicate(String username);

    public Map<String, Object> get(int id);

    public List<UsersForm> findByName(String name);

    public List<UsersForm> findByEmail(String email);

    public List<UsersForm> findAll();
}
