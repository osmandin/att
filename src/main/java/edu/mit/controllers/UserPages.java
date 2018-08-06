package edu.mit.controllers;

import edu.mit.EmailUtil;
import edu.mit.authz.Role;
import edu.mit.authz.Subject;
import org.apache.commons.io.FileUtils;
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
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;


import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.Null;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

@Controller
public class UserPages {
    private final static Logger LOGGER = Logger.getLogger(UserPages.class.getCanonicalName());

    @Resource
    private Environment env;

    @Autowired
    private Subject subject;

    @Autowired
    ServletContext context;

    @Autowired
    private UsersFormRepository userrepo;

    @Autowired
    private DepartmentsFormRepository departmentsFormRepository;

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
            HttpServletRequest httpServletRequest,
            HttpSession session
    ) {
        LOGGER.log(Level.INFO, "SubmitRecords");

        model.addAttribute("page", "SubmitRecords");

/*        if (session.isNew()) {
            String acceptedaddressmatch = env.getRequiredProperty("acceptedaddressmatch");
            Utils utils = new Utils();
            if (!utils.isAcceptedAddress(request, acceptedaddressmatch)) {
                LOGGER.log(Level.SEVERE, "Not an accepted address");
                model.addAttribute("displaysubmitlink", 0);
                //return "Home"; //FIXME OSMAN REMVOED
            }
        }*/

        if (session.getAttribute("loggedin") == null || session.getAttribute("loggedin").toString().equals("0")) {
            LoginData logindata = new LoginData();
            model.addAttribute("loginData", logindata);
            LOGGER.log(Level.INFO, "user related data null");
            // return "Auth";
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

        final String principal = (String) httpServletRequest.getAttribute("mail");

        if (principal == null) {
            LOGGER.severe("Error getting current user");
            throw new RuntimeException(); //TODO
        }

        LOGGER.info("Mail attribute:" + principal);

        boolean isadmin = false;

        final Role role = subject.getRole(principal);

        if ((role.equals(Role.siteadmin))) {
            LOGGER.info("Is site admin");
            isadmin = true;
        }


        // TODO find all for admin:

/*        try {
            if (isadmin) {
                List<SsasForm> ssas = ssarepo.findAll();
                LOGGER.info("ssas for admin user:" + ssas.toString());
                if (ssas != null && !ssas.isEmpty() && ssas.size() != 0) {
                    model.addAttribute("departments", 1);
                    model.addAttribute("ssaForms", ssas);
                }
                return "SubmitRecords";
            }
        } catch (Exception e) {
            LOGGER.severe("Error:" + e);
            throw new RuntimeException(e);
        }*/

        // Not an admin:

        final List<UsersForm> users = userrepo.findByEmail(principal);
        final UsersForm user = users.get(0); //TODO Get current user

        LOGGER.info("Retrieved user:" + user.toString());

        final List<SsasForm> userSubmissionAgreements = new ArrayList<>();

        // FIXME: BUG - extra departments are created when a new SSA is created

        final Set<DepartmentsForm> departments = user.getDepartmentsForms();

        // workaround:

        // List<DepartmentsForm> realDepts = departmentsFormRepository.findAll();
/*
        final List<DepartmentsForm> departmentsList = new ArrayList<>();

        for (DepartmentsForm d : departments) {
            if (!departmentsList.contains(d)) {
                departmentsList.add(d);
            }
        }*/


        LOGGER.info("User departments:" + departments.toString());

        final List<SsasForm> ssas = ssarepo.findAll();

        LOGGER.info("SSAS:" + ssas.toString());

        // Buggy:

        for (final DepartmentsForm df : departments) {

            LOGGER.info("Considering department (in the loop):" + df.toString());

            final int departmentId = df.getId();

            for (final SsasForm ssa : ssas) {
                final DepartmentsForm sd = ssa.getDepartmentForm();
                if (sd.getId() == departmentId) {
                    userSubmissionAgreements.add(ssa);
                }
            }
        }

        LOGGER.info("Retrieved SSAs for user:" + userSubmissionAgreements.toString());


        if (userSubmissionAgreements.size() > 0) {
            model.addAttribute("departments", 1);
            model.addAttribute("ssaForms", userSubmissionAgreements);
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
        LOGGER.log(Level.INFO, "Checking space...");
        final File f = new File(env.getRequiredProperty("dropoff.dir"));
        final Long bytes = f.getUsableSpace();
        final String strbytes = Long.toString(bytes);
        LOGGER.log(Level.INFO, "Available bytes: {0}", new Object[]{strbytes});
        return strbytes;
    }

    // ------------------------------------------------------------------------
    @RequestMapping(value = "/UploadComplete", method = RequestMethod.GET)
    public String UploadComplete(
            ModelMap model,
            HttpSession session
    ) {
        //LOGGER.log(Level.INFO, "UploadComplete GET");

        String ssaid = (String) session.getAttribute("ssaid");
        //LOGGER.log(Level.INFO, "SSAID: {0}", ssaid);

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
            MultipartFile  file,
            HttpSession session,
            MultipartHttpServletRequest mrequest) {

        LOGGER.log(Level.INFO, "UploadComplete POST");

        LOGGER.log(Level.INFO, "File uploaded:" + file.getOriginalFilename());

        LOGGER.log(Level.INFO, "FileDataString:", myfiledatastring); // TODO remove

        LOGGER.log(Level.INFO, "Number of files uploaded: "+ files.length);
        LOGGER.log(Level.INFO, "fileinfodata param: " + myfiledatastring);

        if (file == null || file.getOriginalFilename().isEmpty()) {
            return "FileError";
        }


        if (myfiledatastring == null || myfiledatastring.isEmpty()) {
            // Not doing this results in files getting submitted again and again.

            LOGGER.info("EMPTY");


            LOGGER.info("Hidden value null");
            String v = mrequest.getParameter("filedata");
            LOGGER.info("Multipart value:" + v);

            //return "UploadComplete";
        } else {
            LOGGER.info("NOT EMPTY. NOT EMPTY. NOT EMPTY");
        }


        final int ssaid = (Integer) session.getAttribute("ssaid");
        model.addAttribute("ssaid", ssaid);

        SsasForm ssasForm = ssarepo.findById(ssaid);
        LOGGER.log(Level.INFO, "Associated Department form:" + ssasForm.getDepartmentForm());
        final String DEPARTMENT_ID = ssasForm.getDepartmentForm().getName();


        final String description = (String) session.getAttribute("generalRecordsDescription");
        final String startYear = (String) session.getAttribute("startyear");
        String endYear = (String) session.getAttribute("endyear");
        if (endYear == null || endYear.equals("") || endYear.matches("^\\s*$")) {
            endYear = startYear;
        }


        final String name = (String) session.getAttribute("name");
        String username = (String) session.getAttribute("username");
        String useremail = (String) session.getAttribute("email");


        // Create RsasForm:

        RsasForm rsa = new RsasForm();
        rsa.setStartyear(startYear);
        rsa.setEndyear(endYear);
        rsa.setDescription(description);
        rsa.setApproved(false);
        rsa.setCreatedby(name); // name here... not username... change?
        final String sqlDate = String.format("%1$tY-%1$tm-%1$td", Calendar.getInstance());
        rsa.setTransferdate(sqlDate);

        final SsasForm ssa = ssarepo.findById(ssaid);
        if (ssa == null || ssaid == 0) {
            LOGGER.log(Level.SEVERE, "ssa is null or ssaid is zero: ssaid={0}", new Object[]{ssaid});
        } else {
            rsa.setSsasForm(ssa);
        }

        rsa = rsarepo.save(rsa);

        // Add info for file size (for web side):

        final Format format = new Format();

        final List<FileData> fileData = new ArrayList<>(); //format.parseFileInfo(myfiledatastring);
        //final List<FileData> fileData = format.parseFileInfo(file.getOriginalFilename());


        FileData fileData1 = new FileData();
        fileData1.setName(file.getOriginalFilename());
        fileData1.setSize(file.getSize());
        fileData1.setNicesize(FileUtils.byteCountToDisplaySize(file.getSize()));
        fileData1.setLastmoddatetime("122");
        fileData.add(fileData1);

        model.addAttribute("filedata", fileData);
        model.addAttribute("totalfilesize", FileUtils.byteCountToDisplaySize(file.getSize()));

        // Details for each file ?

        List<RsaFileDataForm> fileDataForms = new ArrayList<RsaFileDataForm>();

        for (final FileData fileDetails : fileData) {
            RsaFileDataForm fd = new RsaFileDataForm();
            fd.setName(fileDetails.getName());
            fd.setSize(fileDetails.getSize());
            fd.setNicesize(format.displayBytes(fileDetails.getSize()));
            fd.setLastmoddatetime(fileDetails.getLastmoddatetime());
            fd.setRsasForm(rsa);

            fd = filedatarepo.save(fd);
            fileDataForms.add(fd);
        }

        rsa.setRsaFileDataForms(fileDataForms);
        rsa.setExtent(format.getTotalfilesize());
        rsa.setExtentstr(format.getTotalfilesizestr());


        rsa = rsarepo.save(rsa); // Saved!

        LOGGER.log(Level.INFO, "Saved form:", rsa.getId());


        model.addAttribute("rsaid", rsa.getId());

        // Create drop off directory:

        final String DROP_OFF_DIR = getDrop_off_dir(DEPARTMENT_ID, rsa);
        LOGGER.log(Level.INFO, "Drop off directory:" +  DROP_OFF_DIR);

        final File dir = new File(DROP_OFF_DIR);
        boolean successful = dir.mkdirs();
        if (!successful) {
            LOGGER.log(Level.SEVERE, "dir={0} NOT created", new Object[]{DROP_OFF_DIR});

        }

        rsa.setPath(DROP_OFF_DIR);

        final List<String> fileList = new ArrayList<>(); // this will be emailed to the user
        final MyFileUtils fileutils = new MyFileUtils();


        if (files != null) {
            fileDataForms = new ArrayList<>();

            // Copy files:

            final List<FileData> uploadFileInfo = fileutils.uploadFiles(files, DROP_OFF_DIR, fileData);

            // Create a bag:

            // fileutils.bagit(uploadfileinfo, DROP_OFF_DIR);


            // Create metadata

            final Map<String, String> metadata = new HashMap<>();
            metadata.put("SSA Id:", String.valueOf(ssa.getId()));
            metadata.put("Department Id:", String.valueOf(ssa.getDepartmentForm().getId()));
            metadata.put("Department Name:", ssa.getDepartmentForm().getName());
            metadata.put("RSA Id:", String.valueOf(rsa.getId()));
            metadata.put("User Email:", (String) request.getAttribute("mail"));
            metadata.put("Transfer Date:", Instant.now().toString());
            metadata.put("Inventory Documents:", String.valueOf(uploadFileInfo.size()));

            final Map<String, String> checksums = new HashMap<>();

            for (final FileData fileinfo : uploadFileInfo) {

                LOGGER.log(Level.INFO, "Processing file:" + fileinfo.getName());
                fileList.add(fileinfo.getName()); //TODO we need more than file name!


                final String md5 = getMD5(fileinfo.getPath());
                checksums.put(fileinfo.getPath(), md5);


                if (fileinfo.getName().equals("")) { // ignore cases where filename is empty... happens when file tag is created in page but not populated
                    LOGGER.log(Level.INFO, "for rsa={0} filename is blank as happens when file tag used but not populated", new Object[]{rsa.getId()});
                    continue;
                }

                final List<RsaFileDataForm> fds = filedatarepo.findBasedOnIdAndFilename(rsa.getId(), fileinfo.getName());

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
                fd.setNicesize(format.displayBytes(fileinfo.getSize()));
                fd.setStatus(fileinfo.getStatus());
                fd = filedatarepo.save(fd);
                fileDataForms.add(fd);
            }

            // write the checksums to file:

            try {
                LOGGER.info("Checksums:" + checksums);
                FileUtils.writeStringToFile(new File(DROP_OFF_DIR + "/" + "att-manifest-md5.txt"), formattedChecksum(checksums) );
            } catch (IOException e) {
                LOGGER.info("Error writing checksum file" + e);
            }

            // write other metadata to file:


            try {
                LOGGER.info("Metadata:" + metadata);
                FileUtils.writeStringToFile(new File(DROP_OFF_DIR + "/" + "att-metadata.txt"), formattedMetadata(metadata) );
            } catch (IOException e) {
                LOGGER.info("Error writing checksum file" + e);
            }

            rsa.setRsaFileDataForms(fileDataForms);
            rsa = rsarepo.saveAndFlush(rsa);
            LOGGER.log(Level.INFO, "Saved RSA:" + rsa.getId());

        } else if (files == null){
            LOGGER.log(Level.SEVERE, "files null");
        }

        // Send mail
        // notifyUser(fileList);

        return "UploadComplete";
    }

    // convert hashmap to

    private String formattedChecksum(Map<String, String> checksums) {
        final StringBuffer sb = new StringBuffer();
        Set<String> keys = checksums.keySet();

        for (String k : keys) {
            String v = checksums.get(k);
            sb.append(v);
            sb.append(" ");
            sb.append(k);
            sb.append("\n");
        }

        return sb.toString();
    }


    private String formattedMetadata(Map<String, String> checksums) {
        final StringBuffer sb = new StringBuffer();
        Set<String> keys = checksums.keySet();

        for (String k : keys) {
            String v = checksums.get(k);
            sb.append(k);
            sb.append(" ");
            sb.append(v);
            sb.append("\n");
        }

        return sb.toString();
    }

    public String getMD5(final String path) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(path);
            return md5Hex(fis);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();

            } catch (NullPointerException | IOException e) {
            }
        }
        return "";
    }

    /**
     * Returns where the file must be stored
     * @param DEPARTMENT_ID
     * @param rsa
     * @return
     */
    private String getDrop_off_dir(String DEPARTMENT_ID, RsasForm rsa) {
        return env.getRequiredProperty("dropoff.dir") + "/" +
                DEPARTMENT_ID + "/" + Integer.toString(rsa.getId());
    }

    // TODO Policy - what happens if the file is copied but the mail is never sent?
    private void notifyUser(List<String> fileList) {
        try {
            emailUtil.notify(adminEmail, emailSubject, emailPrefix + fileList.toString());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error sending mail:{}", e);
        }
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
