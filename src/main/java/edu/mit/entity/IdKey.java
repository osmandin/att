package edu.mit.entity;

import javax.persistence.Column;
import java.io.Serializable;

public class IdKey implements Serializable {
    @Column(name = "userid", insertable = false, updatable = false)
    public Integer userid;

    @Column(name = "departmentid", insertable = false, updatable = false)
    public Integer departmentid;

    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (!(other instanceof IdKey))
            return false;
        IdKey castIdKey = (IdKey) other;
        return userid.equals(castIdKey.userid) && departmentid.equals(castIdKey.departmentid);
    }

    public int hashCode() {
        final int prime = 31;
        int hash = 17;
        hash = hash * prime + this.userid.hashCode();
        hash = hash * prime + this.departmentid.hashCode();
        return hash;
    }
}
