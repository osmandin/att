package edu.mit.entity;

import javax.persistence.*;
import java.util.List;

import lombok.*;

@Data
@Entity
@Table(name = "departments")
@EqualsAndHashCode(exclude = {"ssasForm"})
@ToString(exclude = {"usersForms", "ssasForm"})
public class DepartmentsForm {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String name;

    @Transient
    private boolean active; // used for usersForm

    @OneToOne(fetch = FetchType.EAGER, mappedBy = "departmentForm")
    private SsasForm ssasForm;

    public int hashCode(SsasForm sf) {
        return sf.getId();
    }

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "map",
            joinColumns = @JoinColumn(name = "departmentid", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "userid", referencedColumnName = "id")
    )
    private List<UsersForm> usersForms;
}
