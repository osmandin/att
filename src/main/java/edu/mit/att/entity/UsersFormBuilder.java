package edu.mit.att.entity;

public class UsersFormBuilder {
    private String username;
    private String firstname;
    private String lastname;
    private boolean isadmin;
    private String email;
    private String role;

    public UsersFormBuilder setUsername(String username) {
        this.username = username;
        return this;
    }

    public UsersFormBuilder setFirstname(String firstname) {
        this.firstname = firstname;
        return this;
    }

    public UsersFormBuilder setLastname(String lastname) {
        this.lastname = lastname;
        return this;
    }

    public UsersFormBuilder setIsadmin(boolean isadmin) {
        this.isadmin = isadmin;
        return this;
    }

    public UsersFormBuilder setEmail(String email) {
        this.email = email;
        return this;
    }

    public UsersFormBuilder setRole(String role) {
        this.role = role;
        return this;
    }

    public UsersForm createUsersForm() {
        return new UsersForm(username, firstname, lastname, isadmin, email, role);
    }
}