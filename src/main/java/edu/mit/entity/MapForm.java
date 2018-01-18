package edu.mit.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@Table(name = "map")
public class MapForm implements Serializable {

    @Column(name = "userid", insertable = false, updatable = false)
    private Integer userid;

    @Column(name = "departmentid", insertable = false, updatable = false)
    private Integer departmentid;

    // private boolean departmentactive = true; //TODO is department active required?

    @EmbeddedId
    IdKey key;
}
