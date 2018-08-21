package edu.mit.att.repository;

import edu.mit.att.entity.RsaFileDataForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RsaFileDataFormRepository extends JpaRepository<RsaFileDataForm, Integer> {
    public RsaFileDataForm findById(int id);

    @Query(value = "SELECT f FROM RsaFileDataForm f JOIN f.transferRequest r where r.id=:rsaid and f.name=:filename order by f.name")
    List<RsaFileDataForm> findBasedOnIdAndFilename(@Param("rsaid") int rsaid, @Param("filename") String filename);
}
