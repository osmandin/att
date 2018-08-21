package edu.mit.att.entity;

public class UserBuilder {
    private String username;
    private String firstname;
    private String lastname;
    private boolean isadmin;
    private String email;
    private String role;

    public UserBuilder setUsername(String username) {
        this.username = username;
        return this;
    }

    public UserBuilder setFirstname(String firstname) {
        this.firstname = firstname;
        return this;
    }

    public UserBuilder setLastname(String lastname) {
        this.lastname = lastname;
        return this;
    }

    public UserBuilder setIsadmin(boolean isadmin) {
        this.isadmin = isadmin;
        return this;
    }

    public UserBuilder setEmail(String email) {
        this.email = email;
        return this;
    }

    public UserBuilder setRole(String role) {
        this.role = role;
        return this;
    }

    public User createUsersForm() {
        return new User(username, firstname, lastname, isadmin, email, role);
    }
}