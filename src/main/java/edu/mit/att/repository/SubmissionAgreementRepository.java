package edu.mit.att.repository;

import edu.mit.att.entity.SubmissionAgreement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface SubmissionAgreementRepository extends JpaRepository<SubmissionAgreement, Integer> {
    SubmissionAgreement findById(int id);

    List<SubmissionAgreement> findByDeletedFalseOrderByCreationdateAsc();

    List<SubmissionAgreement> findByDepartmenthead(String departmenthead);

    @Query(value = "SELECT s FROM User u JOIN u.departments, SubmissionAgreement s JOIN s.department where u.username=?1")
    List<SubmissionAgreement> findAllSsasForUsername(String username);

    /*@Query(value = "SELECT distinct s FROM User u JOIN u.departments d, SubmissionAgreement s JOIN s.departmentForm where u.username=?1 and s.approved=true and s.enabled=true order by d.name")
    List<SubmissionAgreement> findAllActiveApprovedEnabledDepartmentsForUsername(String username);*/

    /*@Query(value = "SELECT distinct s FROM SubmissionAgreement s JOIN s.department d where s.enabled=true order by d.name")
        //@Query(value = "SELECT distinct s FROM User u JOIN u.departmentsForms, SubmissionAgreement s JOIN s.department d where s.enabled=true order by d.name")
    List<SubmissionAgreement> findAllEnabledDepartments();*/

    @Query(value = "SELECT distinct s FROM SubmissionAgreement s JOIN s.department d where d.id=?1 order by s.effectivedate")
    List<SubmissionAgreement> findAllForDepartmentId(int departmentid);

}
