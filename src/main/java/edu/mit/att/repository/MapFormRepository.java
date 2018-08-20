package edu.mit.att.repository;

import edu.mit.att.entity.IdKey;
import edu.mit.att.entity.MapForm;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MapFormRepository extends JpaRepository<MapForm, IdKey> {
    public MapForm findByKey(IdKey idk);
}
