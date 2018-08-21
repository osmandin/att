package edu.mit.att.repository;

import edu.mit.att.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {
    Department findById(int departmentid);

    List<Department> findByName(String name);

    @Query(value = "SELECT d FROM Department d order by name asc")
    List<Department> findAllOrderByNameAsc();

    @Query(value = "SELECT d FROM Department d JOIN d.users u where d.id=:departmentid and u.id=:userid order by d.name")
    List<Department> findBasedOnIdAndUserid(@Param("departmentid") int departmentid, @Param("userid") int userid);
}
