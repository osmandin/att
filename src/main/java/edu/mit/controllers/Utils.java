package edu.mit.controllers;

import org.springframework.core.env.Environment;
import org.springframework.ui.ModelMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Utils {

    private final static Logger LOGGER = Logger.getLogger(Utils.class.getCanonicalName());

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
    public boolean setupAdminHandler(ModelMap model, HttpSession session, Environment env) {
        /*if (session.isNew()) {
            return false;
        }

        int sessiontimeout = Integer.parseInt(env.getRequiredProperty("session.timeout"));
        session.setMaxInactiveInterval(sessiontimeout);

        if (session.getAttribute("loggedin") == null || session.getAttribute("loggedin").toString().equals("0") || session.getAttribute("isadmin") == null || session.getAttribute("isadmin").toString().equals("0")) {
            return false;
        }*/

        return true;
    }

    // ------------------------------------------------------------------------
    public boolean isAcceptedAddress(HttpServletRequest request, String addressmatch) {
        String remoteAddr = request.getRemoteAddr();
        if (!remoteAddr.matches(addressmatch)) {
            LOGGER.log(Level.SEVERE, "Error: Not an accepted address: {0}", new Object[]{remoteAddr});
            return false;
        }
        return true;
    }

    // ------------------------------------------------------------------------
    public String getHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            return "unknown";
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
