package edu.mit.att.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Data
@Entity
@ToString(exclude = {"departments"})
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String username;
    private String firstname;
    private String lastname;
    private boolean isadmin;
    private String email;
    private String role = "visitor";
    private boolean enabled;

    @JsonIgnore // this is done to avoid circular serialization to json when using the api
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "map",
            joinColumns = @JoinColumn(name = "userid", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "departmentid", referencedColumnName = "id")

    )
    private Set<Department> departments;

    @Transient
    private List<String> selectedDepartments;


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

    public Set<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(Set<Department> departments) {

        this.departments = departments;
    }

    public List<String> getSelectedDepartments() {
        return selectedDepartments;
    }

    public void setSelectedDepartments(List<String> selectedDepartmets) {
        this.selectedDepartments = selectedDepartmets;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        User user = (User) o;

        return id == user.id;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + id;
        return result;
    }

    public User(String username, String firstname, String lastname, boolean isadmin, String email, String role) {
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.isadmin = isadmin;
        this.email = email;
        this.role = role;
    }

    public User() {
    }
}
