package edu.mit.att;

import org.springframework.core.env.Environment;
import org.springframework.ui.ModelMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Utils {

    private final static Logger LOGGER = Logger.getLogger(Utils.class.getCanonicalName());

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

    // ------------------------------------------------------------------------
    public boolean setupAuthdHandler(ModelMap model, HttpSession session, Environment env) {

        long banner = (System.currentTimeMillis() / 1000l) % 6 + 1;
        model.addAttribute("banner", banner);

        if (session != null) {
            int sessiontimeout = Integer.parseInt(env.getRequiredProperty("session.timeout"));
            session.setMaxInactiveInterval(sessiontimeout);
            return true;
        } else {
            return false;
        }
    }

    // ------------------------------------------------------------------------
    public void redirectToRoot(
            ServletContext context,
            HttpServletResponse response
    ) {
        try {
            response.sendRedirect(context.getContextPath());
        } catch (java.io.IOException ex) {
            LOGGER.log(Level.SEVERE, "Redirect Error: ", ex);
        }
    }
}
