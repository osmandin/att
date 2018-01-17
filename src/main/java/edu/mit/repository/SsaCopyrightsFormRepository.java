package edu.mit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

import edu.mit.entity.*;


public interface SsaCopyrightsFormRepository extends JpaRepository<SsaCopyrightsForm, Integer> {
    public SsaCopyrightsForm findById(int id);

    @Query(value = "SELECT c FROM SsaCopyrightsForm c order by copyright asc")
    List<SsaCopyrightsForm> findAllOrderByCopyrightAsc();

    @Query(value = "SELECT c FROM SsaCopyrightsForm c where c.ssasForm.id=?1 and c.ssasForm.id != 0 order by copyright asc")
    List<SsaCopyrightsForm> findAllBySsaIdOrderByCopyrightAsc(int ssaid);
}
