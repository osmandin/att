package edu.mit.att.repository;

import edu.mit.att.entity.SsaFormatTypesForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface SsaFormatTypesFormRepository extends JpaRepository<SsaFormatTypesForm, Integer> {
    public SsaFormatTypesForm findById(int id);

    @Query(value = "SELECT f FROM SsaFormatTypesForm f order by formattype asc")
    List<SsaFormatTypesForm> findAllOrderByFormattypeAsc();

    @Query(value = "SELECT f FROM SsaFormatTypesForm f where f.submissionAgreement.id=?1 order by formattype asc")
    List<SsaFormatTypesForm> findAllBySsaIdOrderByFormattypeAsc(int ssaid);
}
