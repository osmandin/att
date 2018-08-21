package edu.mit.att.repository;

import edu.mit.att.entity.SsaContactsForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface SsaContactsFormRepository extends JpaRepository<SsaContactsForm, Integer> {
    public SsaContactsForm findById(int id);

    @Query(value = "SELECT c FROM SsaContactsForm c where c.submissionAgreement.id=?1 order by name asc")
    List<SsaContactsForm> findAllBySsaIdOrderByNameAsc(int ssaid);
}
