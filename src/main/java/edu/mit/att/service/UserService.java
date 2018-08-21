package edu.mit.att.service;

import edu.mit.att.entity.Department;
import edu.mit.att.entity.User;

import java.util.List;
import java.util.Map;

 public interface UserService {
     User create(User user);

     User create(User user, List<Department> departments);


     List<User> findAllAdmin();

     List<User> findAllNonadmin();

     boolean duplicate(String username, String firstname, String lastname, String email);

     boolean duplicate(String username);

     Map<String, Object> get(int id);

     List<User> findByName(String name);

     List<User> findByEmail(String email);

     List<User> findAll();
}
