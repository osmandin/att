package edu.mit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

import edu.mit.entity.*;


public interface SsaContactsFormRepository extends JpaRepository<SsaContactsForm, Integer> {
    public SsaContactsForm findById(int id);

    @Query(value = "SELECT c FROM SsaContactsForm c where c.ssasForm.id=?1 order by name asc")
    List<SsaContactsForm> findAllBySsaIdOrderByNameAsc(int ssaid);
}
