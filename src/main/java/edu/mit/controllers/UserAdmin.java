package edu.mit.controllers;

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
/*
import submit.email.Email;
import submit.entity.*;
import submit.impl.Utils;
import submit.repository.DepartmentsFormRepository;
import submit.repository.MapFormRepository;
import submit.repository.UsersFormRepository;
import submit.service.DepartmentsFormService;
import submit.service.UsersFormService;
*/

import edu.mit.entity.*;
import edu.mit.repository.*;
import edu.mit.service.*;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
public class UserAdmin {
    private final static Logger LOGGER = Logger.getLogger(UserAdmin.class.getCanonicalName());

    @SuppressWarnings("unused")
    private static final String rcsinfo = "$Id: UserAdmin.java,v 1.17 2016-12-27 22:26:10-04 ericholp Exp $";

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
            HttpSession session
    ) {
        LOGGER.log(Level.INFO, "ListUsers Get");

        Utils utils = new Utils();
        if (!utils.setupAdminHandler(model, session, env)) {
            return "Home";
        }

        Map<Integer, Map<Integer, Boolean>> adminactivemap = new HashMap<Integer, Map<Integer, Boolean>>();
        List<UsersForm> adminusersForms = userrepo.findByIsadminTrueOrderByLastnameAscFirstnameAsc();
        for (UsersForm uf : adminusersForms) {
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
                adminactivemap.put(uf.getId(), dmap);
            }
        }
        model.addAttribute("adminactivemap", adminactivemap);
        model.addAttribute("adminusersForms", adminusersForms);

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
        model.addAttribute("nonadminusersForms", nonadminusersForms);

        return "ListUsers";
    }

    // ------------------------------------------------------------------------    
    @RequestMapping(value = "/EditUser", method = RequestMethod.GET)
    public String EditUser(
            ModelMap model,
            @RequestParam(value = "userid", required = false) int userid,
            HttpSession session
    ) {
        LOGGER.log(Level.INFO, "EditUser Get");

        Utils utils = new Utils();
        if (!utils.setupAdminHandler(model, session, env)) {
            return "Home";
        }

        model.addAttribute("userid", userid);

        List<DepartmentsForm> dfs = departmentservice.findSkipUserid(userid);
        model.addAttribute("dropdowndepartments", dfs);

        UsersForm usersForm = userrepo.findById(userid);

        List<DepartmentsForm> ds = usersForm.getDepartmentsForms();
        if (ds != null) {
            for (DepartmentsForm dept : ds) {
                IdKey ik = new IdKey();
                ik.userid = usersForm.getId();
                ik.departmentid = dept.getId();

                MapForm mf = maprepo.findByKey(ik);
                if (mf.isDepartmentactive()) {
                    dept.setActive(true);
                }
            }
        }

        model.addAttribute("usersForm", usersForm);

        return "EditUser";
    }

    // ------------------------------------------------------------------------
    @RequestMapping(value = "/EditUser", method = RequestMethod.POST)
    public ModelAndView EditUser(
            UsersForm usersForm,
            BindingResult result,
            final RedirectAttributes redirectAttributes,
            @RequestParam(value = "delete", required = false) String delete,
            @RequestParam(value = "department_id", required = false) DepartmentsForm selectedDepartmentsForms,
            ModelMap model,
            HttpSession session
    ) {
        LOGGER.log(Level.INFO, "EditUser Post");

        Utils utils = new Utils();
        if (!utils.setupAdminHandler(model, session, env)) {
            return new ModelAndView("/Home");
        }

        if (result.hasErrors()) {
            LOGGER.log(Level.INFO, "EditUser Post: has errors");
            return new ModelAndView("/EditUser");
        }

        if (delete != null) {
            String username = usersForm.getUsername();
            // otherwise departments will be removed from map table for other users
            usersForm.setDepartmentsForms(null);
            userrepo.delete(usersForm);
            userrepo.delete(usersForm); // first delete only deletes depts
            redirectAttributes.addFlashAttribute("userdeleted", true);
            redirectAttributes.addFlashAttribute("username", username);
        } else {

            List<MapForm> mfstop = maprepo.findAll(); // why needed? (see below)

            int userid = usersForm.getId();

            if (selectedDepartmentsForms == null) {
                LOGGER.log(Level.INFO, "selected department null");
            } else {
                List<DepartmentsForm> ds = usersForm.getDepartmentsForms();
                if (ds == null) {
                    List<DepartmentsForm> newdfs = new ArrayList<DepartmentsForm>();
                    newdfs.add(selectedDepartmentsForms);
                    usersForm.setDepartmentsForms(newdfs);
                    userrepo.save(usersForm);
                } else {
                    ds.add(selectedDepartmentsForms);
                    usersForm.setDepartmentsForms(ds);
                    userrepo.save(usersForm);
                }
            }

            String thisusername = usersForm.getUsername();

            boolean found = false;
            List<UsersForm> UsersForms = userrepo.findByUsername(thisusername);
            for (UsersForm uf : UsersForms) {
                if (uf.getId() != userid && thisusername.equals(uf.getUsername())) {
                    found = true;
                    break;
                }
            }
            if (found) {
                ModelAndView mav = new ModelAndView();

                LOGGER.log(Level.INFO, "EditUser Post: Duplicate usernames are not allowed!!");

                model.addAttribute("error", "Duplicate usernames are not allowed.");
                mav.setViewName("/EditUser");

                return mav;
            }

            List<DepartmentsForm> dfs = usersForm.getDepartmentsForms();

            userrepo.save(usersForm);

            maprepo.save(mfstop);  // needed because all departments not present in template as hidden variables... could do it but this is easier

            if (dfs == null) {
                LOGGER.log(Level.INFO, "DepartmentsForm null");
            } else {
                for (DepartmentsForm df : dfs) {

                    IdKey ik = new IdKey();
                    ik.userid = usersForm.getId();
                    ik.departmentid = df.getId();

                    MapForm mf = maprepo.findByKey(ik);

                    if (mf == null) {
                        LOGGER.log(Level.INFO, "mapform null");
                    } else {
                        mf.setDepartmentactive(df.isActive());
                        maprepo.save(mf);
                    }
                }
            }

            redirectAttributes.addFlashAttribute("usermodified", true);
            redirectAttributes.addFlashAttribute("username", usersForm.getUsername());
        }


        ModelAndView mav = new ModelAndView();

        if (delete != null) {
            List<UsersForm> adminusersForms = userrepo.findByIsadminTrueOrderByLastnameAscFirstnameAsc();
            model.addAttribute("adminusersForms", adminusersForms);

            List<UsersForm> usersForms = userservice.findAllNonadmin();
            model.addAttribute("nonadminusersForms", usersForms);

            mav.setViewName("redirect:/ListUsers");
        } else {
            List<DepartmentsForm> dfs = departmentservice.findSkipUserid(usersForm.getId());
            model.addAttribute("dropdowndepartments", dfs);

            usersForm = userrepo.findById(usersForm.getId());

            List<DepartmentsForm> ds = usersForm.getDepartmentsForms();
            if (ds != null) {
                for (DepartmentsForm dept : ds) {
                    IdKey ik = new IdKey();
                    ik.userid = usersForm.getId();
                    ik.departmentid = dept.getId();

                    MapForm mf = maprepo.findByKey(ik);
                    if (mf.isDepartmentactive()) {
                        dept.setActive(true);
                    }
                }
            }

            model.addAttribute("usersForm", usersForm);

            mav.setViewName("/EditUser");
        }

        return mav;
    }

    // ------------------------------------------------------------------------
    @RequestMapping(value={"/add1"}, method = RequestMethod.GET)
    public ModelAndView AddUser(
           final Model usersForm
    ) {
        LOGGER.log(Level.INFO, "AddUser Get");

        /* handle session
        Utils utils = new Utils();
        if (!utils.setupAdminHandler(model, session, env)) {
            return "Home";
        } */

        List<DepartmentsForm> dfs = departmentrepo.findAll();
        //model.addAttribute("departmentsForm", dfs);

        //usersForm.setDepartmentsForms(dfs);
        LOGGER.log(Level.INFO, "Returning to page");

        ModelAndView modelAndView = new ModelAndView("/add");
        UsersForm usersForm1 = new UsersForm();
        modelAndView.addObject("usersForm", usersForm1);
        return modelAndView;
    }

    @RequestMapping(value={"/add1"}, method = RequestMethod.POST)
    public ModelAndView greetingForm(final UsersForm item) {
        //final List<Item> itemList = new ArrayList<>();
        //itemList.add(item);
        //itemService.save(itemList);

        LOGGER.log(Level.INFO, "AddUser POST");

        final ModelAndView modelAndView = new ModelAndView("redirect:/results");
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
        LOGGER.log(Level.INFO, "DeleteUser Post");

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

        LOGGER.log(Level.INFO, "delete user: username={0}", new Object[]{username});

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
        LOGGER.log(Level.INFO, "DeleteUserDepartment Get");

        Utils utils = new Utils();
        if (!utils.setupAdminHandler(model, session, env)) {
            return "Home";
        }

        model.addAttribute("userid", userid);

        UsersForm uf = userrepo.findById(userid);
        List<DepartmentsForm> dfs = uf.getDepartmentsForms();

        LOGGER.log(Level.INFO, "delete departmentsForms id={0}", new Object[]{id});

        List<DepartmentsForm> newdfs = new ArrayList<DepartmentsForm>();
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
