package edu.mit.authz;

public enum Role {

    siteadmin(Constants.SITEADMIN), deptadmin (Constants.DEPTADMIN), visitor (Constants.VISITOR), donor (Constants.DONOR);

    final String role;

    Role(final String role) {
        this.role = role;
    }

    // for finer grained permissions
    boolean isPermitted(final Role role) {

        if (role == Role.siteadmin) {
            return true;
        }

        return false;
    }

    // TODO decide if this should be an external class
    public static class Constants {
        static final String SITEADMIN = "siteadmin";
        static final String DEPTADMIN = "deptadmin";
        static final String VISITOR = "visitor";
        static final String DONOR = "donor";
    }
}
