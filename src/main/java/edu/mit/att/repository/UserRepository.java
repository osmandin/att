package edu.mit.att.repository;

import edu.mit.att.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findById(int id);

    List<User> findByIsadminFalse();

    List<User> findByIsadminFalseOrderByLastnameAscFirstnameAsc();

    List<User> findByIsadminTrueOrderByLastnameAscFirstnameAsc();

    List<User> findByUsernameAndFirstnameAndLastnameAndEmail(String username, String firstname, String lastname, String email);

    List<User> findByUsername(String username);

    List<User> findByEmail(String email);

    List<User> findAllByIdAfter(int id);

    List<User> findByDepartmentsForms(Set depts);
}
