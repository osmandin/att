package edu.mit.att.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.mit.att.EmailUtil;
import edu.mit.att.entity.*;
import edu.mit.att.repository.*;
import edu.mit.att.service.DepartmentService;
import edu.mit.att.service.UserService;
import edu.mit.att.authz.Role;
import edu.mit.att.authz.Subject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.web.multipart.MultipartHttpServletRequest;


import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.*;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class UserPages {
    private final static Logger logger = Logger.getLogger(UserPages.class.getCanonicalName());

    @Resource
    private Environment env;

    @Autowired
    private Subject subject;

    @Autowired
    ServletContext context;

    @Autowired
    private UserRepository userrepo;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private UserService userFormService;

    @Autowired
    DepartmentService departmentservice;

    @Autowired
    private SubmissionAgreementRepository ssarepo;

    @Autowired
    private TransferRequestRepository rsarepo;

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
        logger.log(Level.INFO, "SubmitRecords");

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
            logger.log(Level.INFO, "user related data null");
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

        String principal = (String) httpServletRequest.getAttribute("mail");

        if (principal == null || principal.isEmpty()) {
            principal = httpServletRequest.getHeader("mail");
        }

        if (principal == null) {
            logger.severe("Error getting current user");
            throw new RuntimeException(); //TODO
        }

        // LOGGER.info("Mail attribute:" + principal);

        boolean isadmin = false;

        final Role role = subject.getRole(principal);

        if ((role.equals(Role.siteadmin))) {
            logger.info("Is site admin");
            isadmin = true;
        }


        // TODO find all for admin:

/*        try {
            if (isadmin) {
                List<SubmissionAgreement> ssas = ssarepo.findAll();
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

        final List<User> users = userrepo.findByEmail(principal);
        final User user = users.get(0); //TODO Get current user

        // LOGGER.info("Retrieved user:" + user.toString());

        final List<SubmissionAgreement> userSubmissionAgreements = new ArrayList<>();

        // FIXME: BUG - extra departments are created when a new SSA is created

        final Set<Department> departments = user.getDepartments();

        // workaround:

        // List<Department> realDepts = departmentRepository.findAll();
/*
        final List<Department> departmentsList = new ArrayList<>();

        for (Department d : departments) {
            if (!departmentsList.contains(d)) {
                departmentsList.add(d);
            }
        }*/


        logger.info("User departments:" + departments.toString());

        final List<SubmissionAgreement> ssas = ssarepo.findAll();

        logger.info("SSAS:" + ssas.toString());

        // Buggy:

        for (final Department df : departments) {

            logger.info("Considering department (in the loop):" + df.toString());

            final int departmentId = df.getId();

            for (final SubmissionAgreement ssa : ssas) {

                if (ssa.getDepartment() == null) {
                    continue;
                }

                final Department sd = ssa.getDepartment();
                if (sd.getId() == departmentId) {
                    userSubmissionAgreements.add(ssa);
                }
            }
        }

        // LOGGER.info("Retrieved SSAs for user:" + userSubmissionAgreements.toString());


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
        logger.log(Level.INFO, "RecordsSubmissionForm Post");

        try {
            if (ssaid <= 0 && session.getAttribute("ssaid") != null && !session.getAttribute("ssaid").equals("")) {
                ssaid = Integer.parseInt(session.getAttribute("ssaid").toString());
            }

            session.setAttribute("ssaid", ssaid);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if (ssaid != 0) {
            logger.log(Level.INFO, "ssaid={0}", new Object[]{ssaid});
            SubmissionAgreement ssaform = ssarepo.findById(ssaid);

            model.addAttribute("ssa", ssaform);
        } else {
            logger.log(Level.INFO, "ssaid is zero!!!");
            SubmissionAgreement submissionAgreement = new SubmissionAgreement();
            model.addAttribute("ssasForm", submissionAgreement);
        }

        model.addAttribute("transferRequest", new TransferRequest());

        return "RecordsSubmissionForm";
    }

    // ------------------------------------------------------------------------
    @RequestMapping(value = "/UploadFiles", method = RequestMethod.POST)
    public String UploadFiles(
            ModelMap model,
            @Valid TransferRequest transferRequest,
            BindingResult bindingResult,
            HttpSession session
    ) {

        logger.log(Level.INFO, "UploadFiles POST");

        if (bindingResult.hasErrors()) {
            logger.log(Level.INFO, "Error validating fields for RecordsSubmissionForm Post");
            return "RecordsSubmissionForm";
        }

        Format format = new Format();
        String avail = format.showavailbytes(env.getRequiredProperty("dropoff.dir"));
        model.addAttribute("avail_bytes", avail);

        session.setAttribute("generalRecordsDescription", transferRequest.getDescription());
        session.setAttribute("startyear", transferRequest.getStartyear());
        session.setAttribute("endyear", transferRequest.getEndyear());
        session.setAttribute("department", transferRequest.getDepartment());
        session.setAttribute("theses", transferRequest.getTheses());
        session.setAttribute("degrees", transferRequest.getDegrees());

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
        logger.log(Level.INFO, "Checking space...");
        final File f = new File(env.getRequiredProperty("dropoff.dir"));
        final Long bytes = f.getUsableSpace();
        final String strbytes = Long.toString(bytes);
        logger.log(Level.INFO, "Available bytes: {0}", new Object[]{strbytes});
        return strbytes;
    }

    // ------------------------------------------------------------------------
    @RequestMapping(value = "/UploadComplete", method = RequestMethod.GET)
    public String UploadComplete(
            ModelMap model,
            HttpSession session
    ) {
        logger.log(Level.INFO, "UploadComplete GET");

        if (session.getAttribute("ssaid") == null) {
            logger.log(Level.INFO, "User session variable ssaid null. Returning.");
            return "error";
        }

        final String ssaid = (String) session.getAttribute("ssaid");
        logger.log(Level.INFO, "Upload request for SSAID: {0}", ssaid);

        final String degrees = (String) session.getAttribute("degrees");
        final String theses = (String) session.getAttribute("theses");
        final String department = (String) session.getAttribute("department");

        model.addAttribute("ssaid", ssaid);
        model.addAttribute("degrees", degrees);
        model.addAttribute("theses", theses);
        model.addAttribute("department", department);

        return "UploadComplete";
    }

    // ------------------------------------------------------------------------
    @RequestMapping(value = "/UploadComplete", method = RequestMethod.POST)
    public String UploadComplete(
            ModelMap model,
            HttpServletRequest request,
            HttpSession session) {

        logger.log(Level.INFO, "=====================");
        logger.log(Level.INFO, "UploadComplete POST");

        if (session.getAttribute("ssaid") == null) {
            logger.info("Session null for ssaid");
            return "error";
        }

        final int ssaid = (Integer) session.getAttribute("ssaid");
        model.addAttribute("ssaid", ssaid);

        SubmissionAgreement submissionAgreement = ssarepo.findById(ssaid);
        logger.log(Level.INFO, "Associated Department:" + submissionAgreement.getDepartment().getName());
        final String DEPARTMENT_ID = submissionAgreement.getDepartment().getName();
        final String description = (String) session.getAttribute("generalRecordsDescription");
        final String startYear = (String) session.getAttribute("startyear");
        String endYear = (String) session.getAttribute("endyear");

        if (endYear == null || endYear.equals("") || endYear.matches("^\\s*$")) {
            endYear = startYear;
        }

        final String name = (String) session.getAttribute("name");
        final String degrees = (String) session.getAttribute("degrees");
        final String theses = (String) session.getAttribute("theses");
        final String department = (String) session.getAttribute("department");
        String userName = (String) session.getAttribute("username");
        String userEmail = (String) session.getAttribute("email");

        // Create TransferRequest:

        TransferRequest rsa = new TransferRequest();
        rsa.setStartyear(startYear);
        rsa.setEndyear(endYear);
        rsa.setDescription(description);
        rsa.setDegrees(degrees);
        rsa.setTheses(theses);
        rsa.setDepartment(department);
        rsa.setApproved(false);
        rsa.setCreatedby(name); // name here... not username... change?
        final String sqlDate = String.format("%1$tY-%1$tm-%1$td", Calendar.getInstance());
        rsa.setTransferdate(sqlDate);

        final SubmissionAgreement ssa = ssarepo.findById(ssaid);
        if (ssa == null || ssaid == 0) {
            logger.log(Level.SEVERE, "ssa is null or ssaid is zero: ssaid={0}", new Object[]{ssaid});
        } else {
            rsa.setSubmissionAgreement(ssa);
        }

        // Save RSA:
        rsa = rsarepo.save(rsa);

        // Create drop off directory:

        final String DROP_OFF_DIRECTORY = getDrop_off_dir(DEPARTMENT_ID, rsa);
        logger.log(Level.INFO, "Drop off directory:" + DROP_OFF_DIRECTORY);

        final File dir = new File(DROP_OFF_DIRECTORY);

        boolean successful = dir.mkdirs();

        if (!successful) {
            logger.log(Level.SEVERE, "dir={0} NOT created", new Object[]{DROP_OFF_DIRECTORY});
        }

        // process input request:

        ServletFileUpload upload;

        String fileName = "";

        long fileSize = 0;

        File depositedFile = null;

        try {
            // Parse the request with Streaming API
            upload = new ServletFileUpload();
            FileItemIterator iterStream = upload.getItemIterator(request);

            logger.info("Processing request parameters");

            while (iterStream.hasNext()) {
                FileItemStream item = iterStream.next();
                String fieldName = item.getFieldName();
                //logger.info("Field name" + fieldName);
                InputStream stream = item.openStream();
                if (!item.isFormField()) {
                    // logger.info("Field name (not form field):" + item.getName());
                    fileName = item.getName();
                    //Process the InputStream
                    try (InputStream uploadedStream = item.openStream()) {
                        //logger.info("Copying file to share:" + dir.getPath());
                        depositedFile = new File(dir + "/" + item.getName());
                        logger.info("Copying input stream to disk. . .");
                        FileUtils.copyInputStreamToFile(uploadedStream, depositedFile);
                        fileSize = depositedFile.length();
                        logger.info("File copied with length:" + fileSize);
                    }
                } else { // ignore
                    String formFieldValue = Streams.asString(stream);
                    // logger.info("Regular form value: " + formFieldValue);
                }
            }
            logger.info("Upload copying complete");
        } catch (IOException | FileUploadException ex) {
            logger.info("Error processing request:" +  ex);
            return "error";
        }

        // Add info for file size (for web side):

        final List<FileData> fileDataList = new ArrayList<>();

        FileData fileMetadata = new FileData();
        fileMetadata.setName(fileName);
        fileMetadata.setSize(fileSize);
        fileMetadata.setNicesize(FileUtils.byteCountToDisplaySize(fileSize));
        final Date date = new Date();
        fileMetadata.setLastmoddatetime(date.toString());
        fileDataList.add(fileMetadata);

        model.addAttribute("filedata", fileDataList);
        model.addAttribute("totalfilesize", FileUtils.byteCountToDisplaySize(fileSize));

        // Details for each file ?

        List<RsaFileDataForm> fileDataForms = new ArrayList<RsaFileDataForm>();

        final Format format = new Format();

        for (final FileData fileDetails : fileDataList) {
            RsaFileDataForm rsaFileData = new RsaFileDataForm();
            rsaFileData.setName(fileDetails.getName());
            rsaFileData.setSize(fileDetails.getSize());
            rsaFileData.setNicesize(format.displayBytes(fileDetails.getSize()));
            rsaFileData.setLastmoddatetime(fileDetails.getLastmoddatetime());
            rsaFileData.setTransferRequest(rsa);
            rsaFileData = filedatarepo.save(rsaFileData);
            fileDataForms.add(rsaFileData);
        }

        rsa.setRsaFileDataForms(fileDataForms);
        rsa.setExtent(format.getTotalfilesize());
        rsa.setExtentstr(format.getTotalfilesizestr());

        rsa = rsarepo.save(rsa); // Saved!

        logger.log(Level.INFO, "Saved form:"+ rsa.getId());

        model.addAttribute("rsaid", rsa.getId());

        rsa.setPath(DROP_OFF_DIRECTORY);

        fileDataForms = new ArrayList<>();

        // Copy files:

        //logger.info("Copying file locally to drop off directory");

        //final List<FileData> uploadFileInfo = utils.uploadFiles(files, DROP_OFF_DIRECTORY, fileData);

        final List<FileData> uploadFileInfo = new ArrayList<>();
        final FileData filedata = new FileData();
        filedata.setName(fileName);
        filedata.setSize(fileSize);

        if (depositedFile.getPath() != null) {
            filedata.setPath(depositedFile.getPath());
        }

        uploadFileInfo.add(filedata);

        // Create metadata

        final Map<String, String> metadata = new LinkedHashMap<>();

        try {
            metadata.put("SSA Id", String.valueOf(ssa.getId()));

            if (ssa.getDepartment() != null) {
                metadata.put("Department Id", String.valueOf(ssa.getDepartment().getId()));
                metadata.put("Department Name", ssa.getDepartment().getName());
            }

            metadata.put("RSA Id", String.valueOf(rsa.getId()));

            if (request.getHeader("mail") != null) {
                metadata.put("User Email", (String) request.getHeader("mail"));
            } else if (request.getAttribute("mail")!= null) {
                metadata.put("User Email", (String) request.getAttribute("mail"));
            } else {
                logger.log(Level.INFO, "Unknown user email");
            }

            metadata.put("Transfer Date", Instant.now().toString());
            metadata.put("Inventory Documents", "1");

            metadata.put("Beginning Year", startYear);
            metadata.put("Ending Year", endYear);
            metadata.put("Description", description);

            metadata.put("Effective date for submission agreement ", ssa.getEffectivedate());
            metadata.put("Retention schedule", ssa.getRetentionschedule());

            // Add theses information

            metadata.put("Degrees (Theses Submission)", degrees);
            metadata.put("Theses (Theses Submission)", theses);
            metadata.put("Department (Theses Submission)", department);

            // Creator(s) of the records:

            final SubmissionAgreement sa = rsa.getSubmissionAgreement();
            metadata.put("Head of Department/Unit", sa.getDepartmenthead());
            metadata.put("Record or collection identifier", sa.getRecordid());

            for (int i = 0; i < ssa.getSsaContactsForms().size(); i++) {
                final SsaContactsForm sc = ssa.getSsaContactsForms().get(i);
                metadata.put("Person authorized to transfer the records to archives [" + i + "]", sc.getName());
                metadata.put("Phone Number [" + i + "]", sc.getPhone());
                metadata.put("Email [" + i + "]", sc.getEmail());
                metadata.put("Campus Address [" + i + "]", sc.getAddress());
            }

            metadata.put("Type of records:", ssa.getRecordstitle());

            for (int i = 0; i < ssa.getSsaCopyrightsForms().size(); i++) {
                metadata.put("Copyright and licensing agreement [" + i + "]", ssa.getSsaCopyrightsForms().get(i).getCopyright());
            }

            for (int i = 0; i < ssa.getSsaAccessRestrictionsForms().size(); i++) {
                metadata.put("Access restrictions [" + i + "]", ssa.getSsaAccessRestrictionsForms().get(i).getRestriction());
            }

            metadata.put("Retention period:", ssa.getRetentionperiod());
        } catch (Exception e) {
            logger.info("Error extracting value:" + e); // TODO remove exception
        }


        final Map<String, String> checksums = new HashMap<>();

        for (final FileData fileInfo : uploadFileInfo) {

            logger.log(Level.INFO, "Processing file:" + fileInfo.getName());

            final String md5 = getMD5(fileInfo.getPath());
            checksums.put(fileInfo.getName(), md5);

            if (fileInfo.getName().equals("")) { // ignore cases where filename is empty... happens when file tag is created in page but not populated
                logger.log(Level.INFO, "for rsa={0} filename is blank as happens when file tag used " +
                        "but not populated", new Object[]{rsa.getId()});
                continue;
            }

            final List<RsaFileDataForm> fds = filedatarepo.findBasedOnIdAndFilename(rsa.getId(), fileInfo.getName());

           /* if (!fileInfo.getSetmoddatetimestatus().equals("success")) {
                logger.log(Level.SEVERE, "Error: setmoddatetimestatus={0} for " +
                        "rsa={1} filename={2}", new Object[]{fileInfo.getSetmoddatetimestatus(), rsa.getId(),
                        fileInfo.getName()});
            }*/

            // update with extra info
            RsaFileDataForm rsaFileDataForm = fds.get(0);
            rsaFileDataForm.setStatus(fileInfo.getStatus());
            rsaFileDataForm.setSize(fileInfo.getSize());
            rsaFileDataForm.setNicesize(format.displayBytes(fileInfo.getSize()));
            rsaFileDataForm.setStatus(fileInfo.getStatus());
            rsaFileDataForm = filedatarepo.save(rsaFileDataForm);
            fileDataForms.add(rsaFileDataForm);

            // write the checksums to file:

            logger.info("Creating metadata and checksum files");

            try {
                logger.info("Checksums:" + checksums);
                FileUtils.writeStringToFile(new File(DROP_OFF_DIRECTORY + "/" + "att-manifest-md5.txt"), formattedChecksum(checksums));
                logger.info("Metadata:" + metadata);
                FileUtils.writeStringToFile(new File(DROP_OFF_DIRECTORY + "/" + "att-metadata.txt"), formattedMetadata(metadata));
            } catch (IOException e) {
                logger.info("Error writing checksum file" + e);
            }

            rsa.setRsaFileDataForms(fileDataForms);
            rsa = rsarepo.saveAndFlush(rsa);
            logger.log(Level.INFO, "Saved RSA:" + rsa.getId());
        }

        // Send mail

        // notifyUser(rsa.getId(), ssa.getDepartment().getName(), fileList);

        logger.log(Level.INFO, "UploadComplete done!");
        logger.log(Level.INFO, "=====================");

        return "UploadComplete";
    }

    // convert hashmap to

    private String formattedChecksum(Map<String, String> checksums) {
        final StringBuffer sb = new StringBuffer();
        Set<String> keys = checksums.keySet();

        for (String k : keys) {
            String v = checksums.get(k);
            sb.append(v);
            sb.append("  ");
            sb.append(k);
            sb.append("\n");
        }

        return sb.toString();
    }


    private String formattedMetadata(Map<String, String> checksums) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(checksums);
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
     *
     * @param DEPARTMENT_ID
     * @param rsa
     * @return
     */
    private synchronized String getDrop_off_dir(String DEPARTMENT_ID, TransferRequest rsa) {

        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

        String time = String.valueOf(System.currentTimeMillis());

        String randomStr = String.format("%s.%s.%s", date, time, RandomStringUtils.randomAlphanumeric(6));

        return env.getRequiredProperty("dropoff.dir") + "/" +
                DEPARTMENT_ID + "/" + randomStr + "/" + rsa.getId();
    }

    // TODO Policy - what happens if the file is copied but the mail is never sent?
    // TODO extract email builiding logic

    private void notifyUser(Integer rsaId, String department, List<String> fileList) {

        emailSubject += " #" + rsaId + " (" + department + ")";
        emailPrefix += "\n\n";
        emailPrefix += fileList.toString();

        try {
            emailUtil.notify(adminEmail, emailSubject, emailPrefix);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error sending mail:{}", e);
        }
    }
}
