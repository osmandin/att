package edu.mit.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

import edu.mit.entity.*;

public interface UsersFormRepository extends JpaRepository<UsersForm, Integer> {
    UsersForm findById(int id);

    List<UsersForm> findByIsadminFalse();

    List<UsersForm> findByIsadminFalseOrderByLastnameAscFirstnameAsc();

    List<UsersForm> findByIsadminTrueOrderByLastnameAscFirstnameAsc();

    List<UsersForm> findByUsernameAndFirstnameAndLastnameAndEmail(String username, String firstname, String lastname, String email);

    List<UsersForm> findByUsername(String username);

    List<UsersForm> findByEmail(String email);

    List<UsersForm> findAllByIdAfter(int id);

    List<UsersForm> findByDepartmentsForms(Set depts);
}
