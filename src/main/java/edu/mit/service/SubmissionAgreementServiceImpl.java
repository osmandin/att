package edu.mit.service;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

import edu.mit.entity.*;
import edu.mit.repository.*;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class SubmissionAgreementServiceImpl implements SubmissionAgreementService {
    private final static Logger LOGGER = Logger.getLogger(SubmissionAgreementServiceImpl.class.getCanonicalName());

    @Resource
    private Environment env;

    @Resource
    ServletContext context;

    @Resource
    private SsasFormRepository ssarepo;

    @Resource
    private SsaContactsFormRepository contactrepo;

    @Resource
    private SsaAccessRestrictionsFormRepository accessrestrictionrepo;

    @Resource
    private SsaCopyrightsFormRepository copyrightrepo;

    @Resource
    private SsaFormatTypesFormRepository formattyperepo;

    @Resource
    DepartmentsFormRepository departmentrepo;

    @Resource
    DepartmentsFormService departmentservice;

    @Resource
    OnlineSubmissionRequestFormRepository onlinesubmissionrequestrepo;

    @Resource
    private UsersFormRepository userrepo;

    @Resource
    private UsersFormService userservice;

    @Resource
    SsasFormService ssasservice;

    // ------------------------------------------------------------------------
    @Transactional
    public boolean handleSubmissionAgreementForm(
            SubmitData submitData,
            HttpServletRequest request,
            HttpSession session,
            ModelMap model
    ) {

        String[] nameparts = submitData.getName().split("\\s+");

        String firstname = "unknown";
        String lastname = "unknown";
        if (nameparts.length == 3) {
            firstname = nameparts[0];
            lastname = nameparts[2];
        } else if (nameparts.length == 2) {
            firstname = nameparts[0];
            lastname = nameparts[1];
        } else if (nameparts.length == 1) {
            lastname = nameparts[0];
        }

        submitData.setFirstname(firstname);
        submitData.setLastname(lastname);

        String[] emailparts = submitData.getEmail().split("@");
        String username = "unknown";
        if (emailparts.length > 0) {
            username = emailparts[0];
        }

        submitData.setUsername(username);

        String[] staffrecipts = env.getRequiredProperty("org.email").split(",");
        String from = env.getRequiredProperty("submit.from");

        String address = "\"" + submitData.getName().trim() + "\" <" + submitData.getEmail().trim() + ">";

        EmailSetup useremailsetup = new EmailSetup();
        useremailsetup.setTo(address);
        useremailsetup.setSubject("Submission Agreement Request");
        useremailsetup.setFrom(from);

        EmailSetup staffemailsetup = new EmailSetup();
        staffemailsetup.setToarray(staffrecipts);
        staffemailsetup.setSubject("Submission Agreement Request");
        staffemailsetup.setFrom(from);

        SubmitRequestErrors errors = null; // ssasservice.checkForDups(submitData);

        if (errors.isFullDuplicates()) {
            request.setAttribute("duplicateuser", true);
            request.setAttribute("duplicateusername", true);
            request.setAttribute("duplicatedepartment", true);
            request.setAttribute("departmentalreadyassignedtothisuser", true);

            request.setAttribute("fullduplicate", "1");

            submitData.setSsaid(errors.getSsaid());

            errors = new SubmitRequestErrors();
            errors.setDuplicateuser(true);
            errors.setDuplicateusername(true);
            errors.setDuplicatedepartment(true);
            errors.setDepartmentalreadyassignedtothisuser(true);

           /* Email email = new Email(useremailsetup, sender, velocityEngine, env, context, session, model);

            email.sendSubmitEmail(
                    staffemailsetup,
                    submitData,
                    errors,
                    request
            );*/

            return false;
        }

        request.setAttribute("fullduplicate", "0");

        request.setAttribute("correct", "1");

        boolean duplicateuser = false;
        UsersForm uf = null;

        boolean duplicateusername = false;
        List<UsersForm> matchingusers = userrepo.findByUsername(username);
        LOGGER.log(Level.INFO, "findByUsername: username={0} size={1}", new Object[]{username, matchingusers.size()});
        if (matchingusers.size() > 0) {
            duplicateusername = true;
        }

        if (userservice.duplicate(username, firstname, lastname, submitData.getEmail())) {
            duplicateuser = true;
        }

        if (!duplicateusername && !duplicateuser) {
            uf = new UsersForm();
            uf.setUsername(username);
            uf.setFirstname(firstname);
            uf.setLastname(lastname);
            uf.setEmail(submitData.getEmail());
            uf.setIsadmin(false);
            uf.setEnabled(false);
            uf = userrepo.save(uf);
        } else {
            if (duplicateuser) {

                List<UsersForm> ufs = userrepo.findByUsernameAndFirstnameAndLastnameAndEmail(username, firstname, lastname, submitData.getEmail());
                LOGGER.log(Level.SEVERE, "duplicateuser: username={0} size={1}", new Object[]{username, ufs.size()});

                if (ufs.size() != 1) {
                    LOGGER.log(Level.SEVERE, "invalid matching users 2: size={0}", new Object[]{ufs.size()});

                    submitData.setSsaid(-1);

                    errors = new SubmitRequestErrors();
                    errors.setDuplicateuser(true);
                    //errors.setDuplicateusername(false); // default is false
                    //errors.setDuplicatedepartment(false);
                    //errors.setDepartmentalreadyassignedtothisuser(false);
                    errors.setWrongnumberofusermatches(true);

                   /* Email email = new Email(useremailsetup, sender, velocityEngine, env, context, session, model);

                    email.sendSubmitEmail(
                            staffemailsetup,
                            submitData,
                            errors,
                            request
                    );*/

                    request.setAttribute("wrongnumberofusermatches", true);

                    return false;
                } else {
                    uf = ufs.get(0);
                }
            } else if (duplicateusername) {
                LOGGER.log(Level.SEVERE, "duplicateusername: username={0} size={1}", new Object[]{username, matchingusers.size()});

                if (matchingusers.size() == 1) {
                    uf = matchingusers.get(0);
                    LOGGER.log(Level.SEVERE, "duplicateusername size=1: username={0} id={1}", new Object[]{username, uf.getId()});
                } else {
                    LOGGER.log(Level.SEVERE, "invalid matching users: size={0}", new Object[]{matchingusers.size()});

                    submitData.setSsaid(-1);

                    errors = new SubmitRequestErrors();
                    //errors.setDuplicateuser(false);
                    errors.setDuplicateusername(true);
                    //errors.setDuplicatedepartment(false);
                    //errors.setDepartmentalreadyassignedtothisuser(false);
                    errors.setWrongnumberofusermatches(true);

                   /* Email email = new Email(useremailsetup, sender, velocityEngine, env, context, session, model);

                    email.sendSubmitEmail(
                            staffemailsetup,
                            submitData,
                            errors,
                            request
                    );*/

                    request.setAttribute("wrongnumberofusermatches", true);

                    return false;
                }
            }
        }


        DepartmentsForm depart = new DepartmentsForm();
        depart.setName(submitData.getDepartment());
        depart = departmentrepo.save(depart);
        int departmentid = depart.getId();
        LOGGER.log(Level.INFO, "new department: id={0}", new Object[]{depart.getId()});


        boolean departmentalreadyassignedtothisuser = false;
        if (departmentservice.departmentAssignedToUser(departmentid, uf.getId())) {
            departmentalreadyassignedtothisuser = true;
        } else {
            if (depart != null) {
                List<UsersForm> newufs = new ArrayList<UsersForm>();
                List<UsersForm> ufs = depart.getUsersForms();
                if (ufs != null && ufs.size() > 0) {
                    for (UsersForm u : ufs) {
                        newufs.add(u);
                    }
                }
                newufs.add(uf);

                depart.setUsersForms(newufs);
                depart = departmentrepo.save(depart);
            }
        }

        request.setAttribute("duplicateuser", duplicateuser);
        request.setAttribute("duplicateusername", duplicateusername);
        request.setAttribute("departmentalreadyassignedtothisuser", departmentalreadyassignedtothisuser);

        String remoteip = request.getRemoteAddr();

        String sqldate = String.format("%1$tY-%1$tm-%1$td", Calendar.getInstance());

        SsasForm ssa = new SsasForm();
        ssa.setDepartmenthead(submitData.getDepartmenthead());
        ssa.setCreatedby(submitData.getSignature());
        ssa.setIP(remoteip);

        String recordstitle = env.getRequiredProperty("defaults.recordstitle");
        String retentionschedule = env.getRequiredProperty("defaults.retentionschedule");
        String retentionperiod = env.getRequiredProperty("defaults.retentionperiod");
        String archivedescriptionstandards = env.getRequiredProperty("defaults.archivedescriptionstandards");

        ssa.setRecordstitle(recordstitle);
        ssa.setRetentionschedule(retentionschedule);
        ssa.setRetentionperiod(retentionperiod);
        ssa.setDescriptionstandards(archivedescriptionstandards);
        ssa.setCreationdate(sqldate);
        ssa.setEffectivedate(sqldate);
        ssa.setEditdate(sqldate);
        ssa.setOnlinesubmission(true);


        ssa.setDepartmentForm(depart);

        ssa = ssarepo.save(ssa); // for id

        List<SsaContactsForm> newcs = new ArrayList<SsaContactsForm>();
        SsaContactsForm contact = new SsaContactsForm();
        contact.setName(submitData.getName());
        contact.setPhone(submitData.getPhone());
        contact.setEmail(submitData.getEmail());
        contact.setAddress(submitData.getAddress());
        contact = contactrepo.save(contact);
        contact.setSsasForm(ssa);
        newcs.add(contact);
        ssa.setSsaContactsForms(newcs);

        String copyrightstatement = env.getRequiredProperty("defaults.copyrightstatement");

        List<SsaCopyrightsForm> newcr = new ArrayList<SsaCopyrightsForm>();
        SsaCopyrightsForm cr = new SsaCopyrightsForm();
        cr.setCopyright(copyrightstatement);
        cr = copyrightrepo.save(cr);
        cr.setSsasForm(ssa);
        newcr.add(cr);
        ssa.setSsaCopyrightsForms(newcr);

	/*
    List<SsaFormatTypesForm> newfs = new ArrayList<SsaFormatTypesForm>();
	SsaFormatTypesForm f = new SsaFormatTypesForm();
	f.setFormattype("None");
	f = formattyperepo.save(f);
	f.setSsasForm(ssa);
	newfs.add(f);
	ssa.setSsaFormatTypesForms(newfs);
	*/

        ssa = ssarepo.save(ssa);

        submitData.setSsaid(ssa.getId());

        OnlineSubmissionRequestForm osrf = new OnlineSubmissionRequestForm();
        osrf.setSsaid(ssa.getId());
        osrf.setDepartment(submitData.getDepartment());
        osrf.setAddress(submitData.getAddress());
        osrf.setName(submitData.getName());
        osrf.setEmail(submitData.getEmail());
        osrf.setPhone(submitData.getPhone());
        osrf.setDepartmenthead(submitData.getDepartmenthead());
        osrf.setSignature(submitData.getSignature());
        osrf.setDate(sqldate);

        errors = new SubmitRequestErrors();
        errors.setDuplicateuser(duplicateuser);
        errors.setDuplicateusername(duplicateusername);
        errors.setDepartmentalreadyassignedtothisuser(departmentalreadyassignedtothisuser);

       /* Email email = new Email(useremailsetup, sender, velocityEngine, env, context, session, model);

        email.sendSubmitEmail(
                staffemailsetup,
                submitData,
                errors,
                request
        );*/

        request.setAttribute("ssa", ssa);

        return true;
    }
}
