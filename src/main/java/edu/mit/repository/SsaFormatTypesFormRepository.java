package edu.mit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import edu.mit.entity.*;


public interface SsaFormatTypesFormRepository extends JpaRepository<SsaFormatTypesForm, Integer> {
    public SsaFormatTypesForm findById(int id);

    @Query(value = "SELECT f FROM SsaFormatTypesForm f order by formattype asc")
    List<SsaFormatTypesForm> findAllOrderByFormattypeAsc();

    @Query(value = "SELECT f FROM SsaFormatTypesForm f where f.ssasForm.id=?1 order by formattype asc")
    List<SsaFormatTypesForm> findAllBySsaIdOrderByFormattypeAsc(int ssaid);
}
