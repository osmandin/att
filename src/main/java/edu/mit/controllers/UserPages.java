package edu.mit.controllers;

import edu.mit.EmailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import edu.mit.repository.*;
import edu.mit.service.*;
import edu.mit.entity.*;


import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
public class UserPages {
    private final static Logger LOGGER = Logger.getLogger(UserPages.class.getCanonicalName());

    private boolean isadmin = false;
    private String email = "";

    @Resource
    private Environment env;


    @Autowired
    ServletContext context;

    @Autowired
    private UsersFormRepository userrepo;

    @Autowired
    private UsersFormService userFormService;

    @Autowired
    DepartmentsFormService departmentservice;

    @Autowired
    private SsasFormRepository ssarepo;

    @Autowired
    private RsasFormRepository rsarepo;

    @Autowired
    private RsaFileDataFormRepository filedatarepo;

    @Autowired
    private EmailUtil emailUtil;

    @Value("${email.admin}")
    private String adminEmail;

    @Value("${email.subject}")
    private String emailSubject;

    @Value("${email.message.prefix}")
    private String emailPrefix;



    // ------------------------------------------------------------------------
    @RequestMapping("/SubmitRecords")
    public String SubmitRecords(
            ModelMap model,
            HttpServletRequest request,
            HttpSession session
    ) {
        LOGGER.log(Level.INFO, "SubmitRecords");

        model.addAttribute("page", "SubmitRecords");

        if (session.isNew()) {
            String acceptedaddressmatch = env.getRequiredProperty("acceptedaddressmatch");
            Utils utils = new Utils();
            if (!utils.isAcceptedAddress(request, acceptedaddressmatch)) {
                LOGGER.log(Level.SEVERE, "Not an accepted address");
                model.addAttribute("displaysubmitlink", 0);
                //return "Home"; //FIXME OSMAN REMVOED
            }
        }

        if (session.getAttribute("loggedin") == null || session.getAttribute("loggedin").toString().equals("0")) {
            LoginData logindata = new LoginData();
            model.addAttribute("loginData", logindata);
            LOGGER.log(Level.INFO, "user related data null");
            // return "Auth";
        }

        String username = null;
        try {
            username = session.getAttribute("username").toString(); //TODO
            if (username == null || username.equals("") || username.length() == 0) {
                LOGGER.log(Level.SEVERE, "null or blank username");
                return "Home";
            }
        } catch (Exception e) {
            username = "testuser";
            e.printStackTrace();
        }

        // FIXME: Removed session extension logic:
        /*
        try {
            int sessiontimeout = Integer.parseInt(env.getRequiredProperty("session.timeout"));
            session.setMaxInactiveInterval(sessiontimeout);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }*/

        model.addAttribute("departments", 0);


        String isadmin_str = (String) session.getAttribute("isadmin");

        boolean isadmin = false;

        if (isadmin_str != null && isadmin_str.equals("1")) {
            isadmin = true;
        }

        try {
            if (isadmin) {
                List<SsasForm> ssas = ssarepo.findAllEnabledDepartments();
                if (ssas != null && !ssas.isEmpty() && ssas.size() != 0) {
                    model.addAttribute("departments", 1);
                    model.addAttribute("ssaForms", ssas);
                }
                return "SubmitRecords";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<UsersForm> users = userrepo.findAll();

        LOGGER.info("Users:" + users);

        if (users.size() != 1) {
            LOGGER.info("User size not 1");
            users = userFormService.findAllAdmin();
            LOGGER.info("Users:" + users);
            //return "SubmitRecords";
        }

        UsersForm user = users.get(0); //TODO

        LOGGER.info("Retrieved user:" + user.toString());

        List<SsasForm> usersssas = new ArrayList<>();

        List<DepartmentsForm> departments = user.getDepartmentsForms();
        for (DepartmentsForm df : departments) {
            int deptid = df.getId();
            List<SsasForm> ssas = ssarepo.findAll();
            for (SsasForm ssa : ssas) {
                DepartmentsForm ssadf = ssa.getDepartmentForm();
                if (ssadf.getId() == deptid) {
                    usersssas.add(ssa);
                }
            }
        }

        LOGGER.info("Retrieved SSAs:" + usersssas.toString());


        if (usersssas != null && usersssas.size() > 0) {
            model.addAttribute("departments", 1);
            model.addAttribute("ssaForms", usersssas);
        }

        return "SubmitRecords";
    }

    // ------------------------------------------------------------------------
    @RequestMapping(value = "/RecordsSubmissionForm", method = RequestMethod.POST)
    public String RecordsSubmissionForm(
            ModelMap model,
            @RequestParam(value = "ssaid", required = false) int ssaid,
            HttpSession session
    ) {
        LOGGER.log(Level.INFO, "RecordsSubmissionForm Post");

        /*   Utils utils = new Utils();
        if (!utils.setupAuthdHandler(model, session, env)) {
            return "Home";
        }*/

        try {
            if (ssaid <= 0 && session.getAttribute("ssaid") != null && !session.getAttribute("ssaid").equals("")) {
                ssaid = Integer.parseInt(session.getAttribute("ssaid").toString());
            }

            session.setAttribute("ssaid", ssaid);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if (ssaid != 0) {
            LOGGER.log(Level.INFO, "ssaid={0}", new Object[]{ssaid});
            SsasForm ssaform = ssarepo.findById(ssaid);

            model.addAttribute("ssa", ssaform);
        } else {
            LOGGER.log(Level.INFO, "ssaid is zero!!!");
            SsasForm ssasForm = new SsasForm();
            model.addAttribute("ssasForm", ssasForm);
        }

        return "RecordsSubmissionForm";
    }

    // ------------------------------------------------------------------------
    @RequestMapping("/UploadFiles")
    public String UploadFiles(
            ModelMap model,
            @RequestParam(value = "generalRecordsDescription", required = false) String generalRecordsDescription,
            @RequestParam(value = "startyear", required = false) String startyear,
            @RequestParam(value = "endyear", required = false) String endyear,
            HttpSession session
    ) {
        LOGGER.log(Level.INFO, "UploadFiles");

        /*Utils utils = new Utils();
        if (!utils.setupAuthdHandler(model, session, env)) {
            return "Home";
        }
*/
        Format format = new Format();
        String avail = format.showavailbytes(env.getRequiredProperty("dropoff.dir"));
        model.addAttribute("avail_bytes", avail);

        session.setAttribute("generalRecordsDescription", generalRecordsDescription);
        session.setAttribute("startyear", startyear);
        session.setAttribute("endyear", endyear);

        model.addAttribute("totalmax", env.getRequiredProperty("js.totalmax"));
        model.addAttribute("peruploadmax", env.getRequiredProperty("js.peruploadmax"));

        return "UploadFiles";
    }

    // ------------------------------------------------------------------------
    @ResponseBody
    @RequestMapping("/CheckSpace")
    public String CheckSpace(
            ModelMap model,
            HttpSession session
    ) {
        LOGGER.log(Level.INFO, "CheckSpace");

   /*     Utils utils = new Utils();
        if (!utils.setupAuthdHandler(model, session, env)) {
            return "";
        }
*/
        File f = new File(env.getRequiredProperty("dropoff.dir"));
        Long bytes = f.getUsableSpace();
        String strbytes = Long.toString(bytes);
        LOGGER.log(Level.INFO, "available bytes: {0}", new Object[]{strbytes});

        return strbytes;
        //return "338610"; // 338614 Screen Shot 2016-11-03 at 10.30.25 AM.png
    }

    // ------------------------------------------------------------------------
    @RequestMapping(value = "/UploadComplete", method = RequestMethod.GET)
    public String UploadComplete(
            ModelMap model,
            HttpSession session
    ) {
        LOGGER.log(Level.INFO, "UploadComplete GET");

/*        Utils utils = new Utils();
        if (!utils.setupAuthdHandler(model, session, env)) {
            return "Home";
        }*/

        String ssaid = (String) session.getAttribute("ssaid");
        model.addAttribute("ssaid", ssaid);

        return "UploadComplete";
    }

    // ------------------------------------------------------------------------
    @RequestMapping(value = "/UploadComplete", method = RequestMethod.POST)
    public String UploadComplete(
            @RequestParam("file") MultipartFile[] files,
            ModelMap model,
            HttpServletRequest request,
            @RequestParam(value = "filedata", required = false) String myfiledatastring,
            HttpSession session
    ) {
        LOGGER.log(Level.INFO, "UploadComplete POST");

     /*   Utils utils = new Utils();
        if (!utils.setupAuthdHandler(model, session, env)) {
            return "Home";
        }*/

        int ssaid = (Integer) session.getAttribute("ssaid");
        model.addAttribute("ssaid", ssaid);


        String description = (String) session.getAttribute("generalRecordsDescription");
        String startyear = (String) session.getAttribute("startyear");
        String endyear = (String) session.getAttribute("endyear");
        String username = (String) session.getAttribute("username");
        String name = (String) session.getAttribute("name");
        String useremail = (String) session.getAttribute("email");

        if (username == null || username.equals("")) {
            LOGGER.log(Level.SEVERE, "username is blank. setting to remporary user");
            username = "testuser";
            model.addAttribute("nooffices", 1);
            // return "Home";
        }

        if (endyear == null || endyear.equals("") || endyear.matches("^\\s*$")) {
            endyear = startyear;
        }

        String sqldate = String.format("%1$tY-%1$tm-%1$td", Calendar.getInstance());

        RsasForm rsa = new RsasForm();
        rsa.setStartyear(startyear);
        rsa.setEndyear(endyear);
        rsa.setDescription(description);
        rsa.setApproved(false);
        rsa.setCreatedby(name); // name here... not username... change?
        rsa.setTransferdate(sqldate);

        SsasForm ssa = ssarepo.findById(ssaid);
        if (ssa == null || ssaid == 0) {
            LOGGER.log(Level.SEVERE, "ssa is null or ssaid is zero: ssaid={0}", new Object[]{ssaid});
        } else {
            rsa.setSsasForm(ssa);
        }

        rsa = rsarepo.save(rsa);

        model.addAttribute("rsaid", rsa.getId());

        String dropoffdirfull = env.getRequiredProperty("dropoff.dir") + "/" + Integer.toString(rsa.getId());
        File dir = new File(dropoffdirfull);
        boolean successful = dir.mkdir();
        if (!successful) {
            LOGGER.log(Level.SEVERE, "dir={0} NOT created", new Object[]{dropoffdirfull});
        }

        Format format = new Format();
        List<FileData> fileinfodata = format.parseFileInfo(myfiledatastring);

        model.addAttribute("filedata", fileinfodata);
        model.addAttribute("totalfilesize", format.getTotalfilesizestr());

        List<RsaFileDataForm> rfds = new ArrayList<RsaFileDataForm>();
        for (FileData filedetails : fileinfodata) {
            RsaFileDataForm fd = new RsaFileDataForm();
            fd.setName(filedetails.getName());
            fd.setSize(filedetails.getSize());
            fd.setNicesize(format.displayBytes(filedetails.getSize()));
            fd.setLastmoddatetime(filedetails.getLastmoddatetime());
            fd.setRsasForm(rsa);

            fd = filedatarepo.save(fd);
            rfds.add(fd);
        }
        rsa.setRsaFileDataForms(rfds);

        rsa.setExtent(format.getTotalfilesize());
        rsa.setExtentstr(format.getTotalfilesizestr());

        rsa = rsarepo.save(rsa);

        final List<String> uploadedFileNames = new ArrayList<>(); // this will be emailed to the user


        if (files == null) {
            LOGGER.log(Level.SEVERE, "files null");
        } else {
            rfds = new ArrayList<RsaFileDataForm>();
            MyFileUtils fileutils = new MyFileUtils();
            List<FileData> uploadfileinfo = fileutils.uploadFiles(files, dropoffdirfull, fileinfodata);


            for (FileData fileinfo : uploadfileinfo) {

                LOGGER.log(Level.INFO, "Considering file:" + fileinfo.getName());
                uploadedFileNames.add(fileinfo.getName()); //TODO we need more than file name!

                if (fileinfo.getName().equals("")) { // ignore cases where filename is empty... happenes when file tag is created in page but not populated
                    LOGGER.log(Level.INFO, "for rsa={0} filename is blank as happens when file tag used but not populated", new Object[]{rsa.getId()});
                } else {
                    List<RsaFileDataForm> fds = filedatarepo.findBasedOnIdAndFilename(rsa.getId(), fileinfo.getName());
                    if (fds.size() != 1) {
                        LOGGER.log(Level.SEVERE, "Error: incorrect number of matches ({0}) for rsa={1} filename={2} myfiledatastring={3}", new Object[]{fds.size(), rsa.getId(), fileinfo.getName(), myfiledatastring});
                        continue;
                    }

                    if (!fileinfo.getSetmoddatetimestatus().equals("success")) {
                        LOGGER.log(Level.SEVERE, "Error: setmoddatetimestatus={0} for rsa={1} filename={2}", new Object[]{fileinfo.getSetmoddatetimestatus(), rsa.getId(), fileinfo.getName()});
                    }
                    // update with extra info
                    RsaFileDataForm fd = fds.get(0);
                    fd.setStatus(fileinfo.getStatus());
                    fd.setSize(fileinfo.getSize());
                    fd.setNicesize(format.displayBytes((long) fileinfo.getSize()));
                    fd.setStatus(fileinfo.getStatus());
                    fd = filedatarepo.save(fd);
                    rfds.add(fd);
                }
            }

            rsa.setRsaFileDataForms(rfds);
            rsa = rsarepo.save(rsa);
        }

        String isadmin_str = (String) session.getAttribute("isadmin");
        boolean isadmin = false;
        if (isadmin_str != null && isadmin_str.equals("1")) {
            isadmin = true;
        }

        List<SsasForm> ssas1 = null;
        // osm: In later versions of MySql the distinct fails
        // for now modified mysql profile to fix this.
        // SET sql_mode=(SELECT REPLACE(@@sql_mode, 'ONLY_FULL_GROUP_BY', ''));
        // and restart MYSql


       /* if (isadmin) {
            ssas = ssarepo.findAllEnabledDepartments();
        } else {
            ssas = ssarepo.findAllActiveApprovedEnabledDepartmentsForUsername(username);
        }

        if (ssas == null || ssas.isEmpty()) {
            LOGGER.log(Level.SEVERE, "ssa null or empty for username={0} isadmin={1}", new Object[]{username, isadmin});
            model.addAttribute("departments", 0);
            return "Home";
        }

        model.addAttribute("departments", 1);
        model.addAttribute("ssaForms", ssas);
        */

        emailUtil.notify(adminEmail, emailSubject, emailPrefix + uploadedFileNames.toString());

        return "UploadComplete";
    }

    // ------------------------------------------------------------------------
    @RequestMapping(value = "/VerifySave", method = RequestMethod.POST)
    public void VerifySave(
            ModelMap model,
            SubmitData submitData,
            @RequestParam(value = "g-recaptcha-response", required = false) String response,
            HttpServletRequest request,
            HttpServletResponse httpresponse,
            HttpSession session
    ) {
        LOGGER.log(Level.INFO, "VerifySave Post");

        Utils utils = new Utils();

        if (session.getAttribute("captchacorrect") != null && !session.getAttribute("captchacorrect").toString().equals("1")) {
            LOGGER.log(Level.INFO, "VerifySave Post: Captcha entry incorrect");
            utils.redirectToRoot(context, httpresponse);
            return;
        }

        String acceptedaddressmatch = env.getRequiredProperty("acceptedaddressmatch");
        if (!utils.isAcceptedAddress(request, acceptedaddressmatch)) {
            utils.redirectToRoot(context, httpresponse);
            LOGGER.log(Level.SEVERE, "Not an accepted address");
            return;
        }

        model.addAttribute("captchacorrect", "0");
        session.setAttribute("captchacorrect", "0");
        session.setAttribute("captchaincorrect", "0");

        String staffemail = env.getRequiredProperty("org.email");
        model.addAttribute("staff_email", staffemail);

        String publickey = env.getRequiredProperty("publiccaptchakey");
        model.addAttribute("publickey", publickey);

        model.addAttribute("submitrecap", "1");

        if (response == null) {
            LOGGER.log(Level.INFO, "VerifySave Post: Captcha entry incorrect");
            session.setAttribute("captchaincorrect", "1");
            utils.redirectToRoot(context, httpresponse);
            return;
        }

        String privatekey = env.getRequiredProperty("privatecaptchakey");

        // boolean correct = VerifyRecaptcha.verify(response, privatekey);

        boolean correct = true;

        if (correct) {
            model.addAttribute("cpatchacorrect", "1");
            session.setAttribute("captchacorrect", "1");

            String date = String.format("%1$tB %1$td, %1$tY", Calendar.getInstance());
            submitData.setDate(date);

            OrgInfo orginfo = new OrgInfo();
            orginfo.setEmail(env.getRequiredProperty("org.email"));
            orginfo.setPhone(env.getRequiredProperty("org.phone"));
            orginfo.setName(env.getRequiredProperty("org.name"));
            orginfo.setNamefull(env.getRequiredProperty("org.namefull"));

            SubmitAppInfo submitappinfo = new SubmitAppInfo();
            submitappinfo.setName(env.getRequiredProperty("submit.name"));
            submitappinfo.setFrom(env.getRequiredProperty("submit.from"));
            submitappinfo.setRoot(context.getContextPath());

            Map<String, Object> datamodel = new HashMap<String, Object>();
            datamodel.put("info", submitData);
            datamodel.put("org", orginfo);
            datamodel.put("submit", submitappinfo);
            datamodel.put("session", session);

           /* String htmldata = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "velocity/Request.vm", "UTF8", datamodel);
            CreatePdf cp = new CreatePdf();
            cp.downloadpdf(httpresponse, htmldata);*/

            return;
        }

        LOGGER.log(Level.INFO, "VerifySave Post: Captcha entry incorrect");

        session.setAttribute("captchaincorrect", "1");

        model.addAttribute("submitData", submitData);

        utils.redirectToRoot(context, httpresponse);
        return;
    }
}
