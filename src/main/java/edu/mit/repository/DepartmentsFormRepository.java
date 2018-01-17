package edu.mit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import edu.mit.entity.*;


import java.util.List;

public interface DepartmentsFormRepository extends JpaRepository<DepartmentsForm, Integer> {
    DepartmentsForm findById(int departmentid);

    List<DepartmentsForm> findByName(String name);

    @Query(value = "SELECT d FROM DepartmentsForm d order by name asc")
    List<DepartmentsForm> findAllOrderByNameAsc();

    @Query(value = "SELECT d FROM DepartmentsForm d JOIN d.usersForms u where d.id=:departmentid and u.id=:userid order by d.name")
    List<DepartmentsForm> findBasedOnIdAndUserid(@Param("departmentid") int departmentid, @Param("userid") int userid);
}
