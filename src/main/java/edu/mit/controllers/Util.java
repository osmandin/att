package edu.mit.controllers;

import java.util.HashMap;
import java.util.Map;

public class Util {

    // FIXME: clean up

    public static Map<Integer, String> getRoles() {
        final Map<Integer, String> formats = new HashMap<>();

        final String[] formatsStr = new String[] {"siteadmin", "visitor", "deptadmin", "donor"};
        //
        int i = 0;

        for (String f : formatsStr) {
            formats.put(i, f);
            i++;
        }

        return formats;
    }

}
