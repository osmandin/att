package edu.mit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

import edu.mit.entity.*;


public interface SsasFormRepository extends JpaRepository<SsasForm, Integer> {
    SsasForm findById(int id);

    List<SsasForm> findByDeletedFalseOrderByCreationdateAsc();

    List<SsasForm> findByDepartmenthead(String departmenthead);

    @Query(value = "SELECT s FROM UsersForm u JOIN u.departmentsForms, SsasForm s JOIN s.departmentForm where u.username=?1")
    List<SsasForm> findAllSsasForUsername(String username);

    @Query(value = "SELECT distinct s FROM UsersForm u JOIN u.departmentsForms d, SsasForm s JOIN s.departmentForm where u.username=?1 and s.approved=true and s.enabled=true order by d.name")
    List<SsasForm> findAllActiveApprovedEnabledDepartmentsForUsername(String username);

    @Query(value = "SELECT distinct s FROM SsasForm s JOIN s.departmentForm d where s.enabled=true order by d.name")
        //@Query(value = "SELECT distinct s FROM UsersForm u JOIN u.departmentsForms, SsasForm s JOIN s.departmentForm d where s.enabled=true order by d.name")
    List<SsasForm> findAllEnabledDepartments();

    @Query(value = "SELECT distinct s FROM SsasForm s JOIN s.departmentForm d where d.id=?1 order by s.effectivedate")
    List<SsasForm> findAllForDepartmentId(int departmentid);

}
