package edu.mit.authz;

public enum Role {

    siteadmin(Constants.SITEADMIN), deptadmin(Constants.DEPTADMIN), visitor(Constants.VISITOR), donor(Constants.DONOR);

    final String role;

    Role(final String role) {
        this.role = role;
    }

    private static class Constants {
        static final String SITEADMIN = "siteadmin";
        static final String DEPTADMIN = "deptadmin";
        static final String VISITOR = "visitor";
        static final String DONOR = "donor";
    }
}
