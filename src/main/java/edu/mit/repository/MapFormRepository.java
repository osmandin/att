package edu.mit.repository;

import org.springframework.data.jpa.repository.JpaRepository;


import edu.mit.entity.*;


public interface MapFormRepository extends JpaRepository<MapForm, IdKey> {
    public MapForm findByKey(IdKey idk);
}
