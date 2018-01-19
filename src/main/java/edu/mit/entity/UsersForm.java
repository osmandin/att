package edu.mit.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@ToString(exclude = {"departmentsForms"})
@Table(name = "users")
public class UsersForm {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String username;
    private String firstname;
    private String lastname;
    private boolean isadmin;
    private String email;
    private boolean enabled;

    @JsonIgnore // this is done to avoid circular serialization to json when using the api
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "map",
            joinColumns = @JoinColumn(name = "userid", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "departmentid", referencedColumnName = "id")
    )
    private List<DepartmentsForm> departmentsForms;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public boolean isIsadmin() {
        return isadmin;
    }

    public void setIsadmin(boolean isadmin) {
        this.isadmin = isadmin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<DepartmentsForm> getDepartmentsForms() {
        return departmentsForms;
    }

    public void setDepartmentsForms(List<DepartmentsForm> departmentsForms) {
        this.departmentsForms = departmentsForms;
    }
}