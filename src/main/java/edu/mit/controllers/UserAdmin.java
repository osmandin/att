package edu.mit.controllers;

import edu.mit.authz.Role;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.mit.entity.*;
import edu.mit.repository.*;
import edu.mit.service.*;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.logging.Level;

import static org.slf4j.LoggerFactory.getLogger;

@Controller
public class UserAdmin {

    private final Logger logger = getLogger(this.getClass());


    @Resource
    private Environment env;

    @Autowired
    ServletContext context;


    @Autowired
    private UsersFormRepository userrepo;

    @Autowired
    private DepartmentsFormRepository departmentrepo;

    @Autowired
    MapFormRepository maprepo;

    @Autowired
    private UsersFormService userservice;

    @Autowired
    DepartmentsFormService departmentservice;

    // ------------------------------------------------------------------------
    @RequestMapping(value = "/ListUsers", method = RequestMethod.GET)
    public String ListUsers(
            ModelMap model,
            HttpSession session,
            HttpServletRequest request
    ) {

        logger.info("ListUsers GET");


        Map<Integer, Map<Integer, Boolean>> adminactivemap = new HashMap<Integer, Map<Integer, Boolean>>();
        // List<UsersForm> adminusersForms = userrepo.findByIsadminTrueOrderByLastnameAscFirstnameAsc();
        List<UsersForm> allUsers = new ArrayList<>();


        // TODO authz -- filter by what users the admin can see

        final String email = (String) request.getAttribute("mail");
        logger.info("User:{}"+ email);
        List<UsersForm> currentUsers = userrepo.findByEmail(email);
        UsersForm currentUser = currentUsers.get(0);

        if (currentUser.getRole().equals(Role.deptadmin.name())) {
            final Set dept = currentUser.getDepartmentsForms();
            // find only users who belong to the same department
            // Note that the user may belong to different departments
            allUsers = userrepo.findByDepartmentsForms(dept);
            logger.info("Corresponding users:" + allUsers);
        } else if (currentUser.getRole().equals(Role.siteadmin.name())){
            allUsers = userrepo.findAllByIdAfter(0); //TODO implement proper findAll
            logger.info("Found users:{}", allUsers.toString());
        } else {
            logger.info("Improper access");
            return ("Permissions");
        }


        for (final UsersForm uf : allUsers) {
            final Set<DepartmentsForm> departments = uf.getDepartmentsForms();

            logger.debug("Found departments for user:{}", departments);


            if (departments != null) {
                Map<Integer, Boolean> dmap = new HashMap<Integer, Boolean>();
                for (DepartmentsForm df : departments) {
                    IdKey ik = new IdKey();
                    ik.userid = uf.getId();
                    ik.departmentid = df.getId();

                    MapForm mf = maprepo.findByKey(ik);
                    //dmap.put(df.getId(), mf.isDepartmentactive());
                }
                adminactivemap.put(uf.getId(), dmap);
            }
        }
        model.addAttribute("adminactivemap", adminactivemap); //TODO ?
        model.addAttribute("adminusersForms", allUsers);

        // TODO remove
       /*
       Map<Integer, Map<Integer, Boolean>> nonadminactivemap = new HashMap<Integer, Map<Integer, Boolean>>();
        List<UsersForm> nonadminusersForms = userrepo.findByIsadminFalseOrderByLastnameAscFirstnameAsc();
        for (UsersForm uf : nonadminusersForms) {
            List<DepartmentsForm> dfs = uf.getDepartmentsForms();
            if (dfs != null) {
                Map<Integer, Boolean> dmap = new HashMap<Integer, Boolean>();
                for (DepartmentsForm df : dfs) {
                    IdKey ik = new IdKey();
                    ik.userid = uf.getId();
                    ik.departmentid = df.getId();

                    MapForm mf = maprepo.findByKey(ik);
                    dmap.put(df.getId(), mf.isDepartmentactive());
                }
                nonadminactivemap.put(uf.getId(), dmap);
            }
        }
        model.addAttribute("nonadminactivemap", nonadminactivemap);
        model.addAttribute("nonadminusersForms", nonadminusersForms);*/

       return "ListUsers";
    }

    // ------------------------------------------------------------------------    
    @RequestMapping(value = "/EditUser", method = RequestMethod.GET)
    public String EditUser(
            ModelMap model,
            @RequestParam(value = "userid", required = false) int userid,
            HttpSession session,
            HttpServletRequest request
    ) {


        model.addAttribute("userid", userid);

        final UsersForm usersForm = userrepo.findById(userid);

        // Authz: if the user is not a siteadmin or a department admin don't let him change the role;

        final String userAttrib = (String) request.getAttribute("mail");

        final UsersForm user = userrepo.findByEmail(userAttrib).get(0);

        // First find all departments user is not associated with
        final Set<DepartmentsForm> otherDepartments = departmentservice.findSkipUserid(userid);

        // Now find the existing bindings:
        final Set<DepartmentsForm> existing = usersForm.getDepartmentsForms();

        otherDepartments.addAll(existing);

        logger.info("Found departments:{}", otherDepartments);

        model.addAttribute("dropdowndepartments", otherDepartments);

        // add roles:

        final Map<Integer, String> roles = Util.getRoles();

        final Map<Integer, String> rolesToDisplay = new HashMap<>();

        // A dept admin shouldn't be able to set a role to siteadmin

        if (user.getRole().equals(Role.deptadmin.name())) {

            final Set<Integer> keys = roles.keySet();

            for (final Integer k : keys) {
                String s = roles.get(k);
                if (!s.equals(Role.siteadmin.name())) {
                    rolesToDisplay.put(k, s);
                }
            }
            model.addAttribute("roles", rolesToDisplay);

        } else if (user.getRole().equals(Role.siteadmin.name())) {
            model.addAttribute("roles", roles);
        } else {
            return "Permissions";
        }

        model.addAttribute("usersForm", usersForm);

        return "EditUser";
    }

    // ------------------------------------------------------------------------
    @RequestMapping(value = "/EditUser", method = RequestMethod.POST)
    public ModelAndView EditUser(UsersForm user) {

        logger.info("Editing user:{}", user.toString());

        // Check role:



        // This is done to prevent a IllegalStateException from JPA.
        // Assign a new list TODO: check any side effects

        final UsersForm updatedUser = userrepo.findById(user.getId());

        final Set<DepartmentsForm> updatedDepartments = new HashSet<>();

        for (final String s : user.getSelectedDepartments()) {
            final DepartmentsForm d = departmentrepo.findById(Integer.parseInt(s));
            updatedDepartments.add(d);
        }

        updatedUser.setDepartmentsForms(updatedDepartments);
        updatedUser.setRole(user.getRole());
        updatedUser.setFirstname(user.getFirstname());
        updatedUser.setLastname(user.getLastname());
        updatedUser.setEmail(user.getEmail());


        // Save the object
        userrepo.save(updatedUser);

        final ModelAndView mav = new ModelAndView();
        mav.setViewName("redirect:/ListUsers");
        return mav;
    }

    /**
     * Add a user
     *
     * @param usersForm
     * @return
     */
    @RequestMapping(value = {"/add", "/AddUser", "AddUser"}, method = RequestMethod.GET)
    public ModelAndView AddUser(final Model usersForm) {
        logger.info("AddUser GET");


        // populate any form elements here:
        List<DepartmentsForm> dfsList =  departmentrepo.findAll();
        Set<DepartmentsForm> dfs = new HashSet<>(dfsList);
        //model.addAttribute("departmentsForm", dfs);
        logger.info("Found departments:{}", dfs.toString());

        List<DepartmentAdmin> selectedDepartments = new ArrayList<>();

        final UsersForm item = new UsersForm();
        item.setDepartmentsForms(dfs);

        final ModelAndView model = new ModelAndView("AddUser");
        model.addObject("usersForm", item);
        model.addObject("allDepartments", dfs);
        model.addObject("selectedDepartments", selectedDepartments);
        return model;
    }

    /**
     * Add a user
     *
     * @param item
     * @return
     */
    @RequestMapping(value = {"/add", "AddUser", "/AddUser"}, method = RequestMethod.POST)
    public ModelAndView greetingForm(final UsersForm item) {

        // associated department logic:

        final Set<DepartmentsForm> depts = new HashSet<>();

        // Convert from string to department. this can be replaced by a spring converter in future.

        if (item.getSelectedDepartments() == null || item.getSelectedDepartments().size() == 0) {
            logger.error("No department specified");
        } else {
            logger.info("Selected departments:{}", item.getSelectedDepartments());
            for (final String d : item.getSelectedDepartments()) {
                //df.setActive(true); //TODO revisit is active/inactive logic necessary?
                DepartmentsForm department = departmentrepo.findById(Integer.parseInt(d));
                logger.info("Found department:{}", department);
                depts.add(department);
            }
        }

        // save user:

        try {
            logger.info("Saving item:{}", item);
            item.setDepartmentsForms(depts);
            userservice.create(item);
        } catch (Exception e) {
            logger.error("Error saving item:{}", e);
        }

        final ModelAndView modelAndView = new ModelAndView("redirect:/ListUsers"); //TODO
        return modelAndView;
    }

    // ------------------------------------------------------------------------
    @RequestMapping(value = "/DeleteUser", method = RequestMethod.POST)
    public ModelAndView DeleteUser(
            @RequestParam(value = "userid", required = false) int userid,
            final RedirectAttributes redirectAttributes,
            ModelMap model,
            HttpSession session
    ) {
        logger.info("Post");
        Utils utils = new Utils();
        if (!utils.setupAdminHandler(model, session, env)) {
            return new ModelAndView("/Home");
        }

        UsersForm usersForm = userrepo.findById(userid);

        String username = usersForm.getUsername();

        usersForm.setDepartmentsForms(null);
        userrepo.delete(usersForm);
        userrepo.delete(usersForm);

        ModelAndView mav = new ModelAndView();

        mav.setViewName("redirect:/ListUsers");

        redirectAttributes.addFlashAttribute("userdeleted", true);
        redirectAttributes.addFlashAttribute("username", username);

        logger.info("delete user: username={0}", new Object[]{username});

        return mav;
    }

    // ------------------------------------------------------------------------
    @RequestMapping(value = "/DeleteUserDepartment", method = RequestMethod.GET)
    public String DeleteUserDepartment(
            @RequestParam(value = "id", required = false) int id,
            @RequestParam(value = "userid", required = false) int userid,
            ModelMap model,
            HttpSession session
    ) {
        logger.info("DeleteUserDepartment Get");

        Utils utils = new Utils();
        if (!utils.setupAdminHandler(model, session, env)) {
            return "Home";
        }

        model.addAttribute("userid", userid);

        UsersForm uf = userrepo.findById(userid);
        Set<DepartmentsForm> dfs = uf.getDepartmentsForms();

        logger.info("delete departmentsForms id={0}", new Object[]{id});

        final Set<DepartmentsForm> newdfs = new HashSet<>();
        for (DepartmentsForm df : dfs) {
            if (df.getId() != id) {
                newdfs.add(df);
            }
        }
        uf.setDepartmentsForms(newdfs);
        userrepo.save(uf);

        dfs = departmentservice.findSkipUserid(userid);
        model.addAttribute("dropdowndepartments", dfs);

        UsersForm usersForm = userrepo.findById(userid);
        model.addAttribute("usersForm", usersForm);

        return "EditUser";
    }


}