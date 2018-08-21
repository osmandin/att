package edu.mit.att.service;

import edu.mit.att.entity.*;
import edu.mit.att.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

@Service
public class SsasFormServiceImpl implements SsasFormService {
    private final static Logger LOGGER = Logger.getLogger(SsasFormServiceImpl.class.getCanonicalName());

    private int ssaid = -1;

    @Resource
    private SubmissionAgreementRepository ssarepo;

    @Resource
    private SsaContactsFormRepository contactrepo;

    @Resource
    private SsaAccessRestrictionsFormRepository accessrestrictionrepo;

    @Resource
    private SsaCopyrightsFormRepository copyrightrepo;

    @Resource
    private SsaFormatTypesFormRepository formattyperepo;

    @Resource
    private DepartmentRepository departmentRepository;

    // ------------------------------------------------------------------------
    @Transactional
    public void saveForm(SubmissionAgreement submissionAgreement, Department selectedDepartment) {

        int ssaid = submissionAgreement.getId();


        submissionAgreement.setDepartment(selectedDepartment);

        // in form
        //submissionAgreement.setCreatedby(session.getAttribute("name").toString());
        //submissionAgreement.setIP(request.getRemoteAddr());
        //submissionAgreement.setEditdate(String.format("%1$tY-%1$tm-%1$td", Calendar.getInstance()));

        SubmissionAgreement ssa = ssarepo.findById(ssaid);

        List<SsaContactsForm> repocontacts = ssa.getSsaContactsForms();
        if (repocontacts != null) {
            contactrepo.delete(repocontacts);
        }

        List<SsaCopyrightsForm> repocopyrights = ssa.getSsaCopyrightsForms();
        if (repocopyrights != null) {
            copyrightrepo.delete(repocopyrights);
        }

        List<SsaAccessRestrictionsForm> reporestrictions = ssa.getSsaAccessRestrictionsForms();
        if (reporestrictions != null) {
            accessrestrictionrepo.delete(reporestrictions);
        }

        List<SsaFormatTypesForm> repotypes = ssa.getSsaFormatTypesForms();
        if (repotypes != null) {
            formattyperepo.delete(repotypes);
        }

        // added contacts, copyrights, ... works just by saving, but deleted content does not delete just by saving, hence the above
        ssarepo.save(submissionAgreement);
    }

    @Transactional
    public void saveFormTest(SubmissionAgreement submissionAgreement, Department selectedDepartment) {

        int ssaid = submissionAgreement.getId();


        submissionAgreement.setDepartment(departmentRepository.findAll().get(0));

        // in form
        //submissionAgreement.setCreatedby(session.getAttribute("name").toString());
        //submissionAgreement.setIP(request.getRemoteAddr());
        //submissionAgreement.setEditdate(String.format("%1$tY-%1$tm-%1$td", Calendar.getInstance()));

        SubmissionAgreement ssa = ssarepo.findById(ssaid);

        List<SsaContactsForm> repocontacts = ssa.getSsaContactsForms();
        if (repocontacts != null) {
            contactrepo.delete(repocontacts);
        }

        List<SsaCopyrightsForm> repocopyrights = ssa.getSsaCopyrightsForms();
        if (repocopyrights != null) {
            copyrightrepo.delete(repocopyrights);
        }

        List<SsaAccessRestrictionsForm> reporestrictions = ssa.getSsaAccessRestrictionsForms();
        if (reporestrictions != null) {
            accessrestrictionrepo.delete(reporestrictions);
        }

        List<SsaFormatTypesForm> repotypes = ssa.getSsaFormatTypesForms();
        if (repotypes != null) {
            formattyperepo.delete(repotypes);
        }

        // added contacts, copyrights, ... works just by saving, but deleted content does not delete just by saving, hence the above
        ssarepo.save(submissionAgreement);

    }

    // ------------------------------------------------------------------------
    @Transactional
    public void create(SubmissionAgreement submissionAgreement, Department selectedDepartment, HttpSession session, HttpServletRequest request) {
        submissionAgreement.setCreatedby("osman"); //TODO

        //submissionAgreement.setIP(request.getRemoteAddr());
        submissionAgreement.setEditdate(String.format("%1$tY-%1$tm-%1$td", Calendar.getInstance()));

        submissionAgreement.setDepartment(selectedDepartment);
        List<SsaCopyrightsForm> crs = submissionAgreement.getSsaCopyrightsForms();
        List<SsaFormatTypesForm> fts = submissionAgreement.getSsaFormatTypesForms();
        List<SsaAccessRestrictionsForm> ars = submissionAgreement.getSsaAccessRestrictionsForms();
        submissionAgreement.setSsaCopyrightsForms(null);
        submissionAgreement.setSsaFormatTypesForms(null);
        submissionAgreement.setSsaAccessRestrictionsForms(null);
        submissionAgreement = ssarepo.save(submissionAgreement);

        if (crs != null) {
            for (SsaCopyrightsForm cr : crs) {
                cr.setSubmissionAgreement(submissionAgreement);
            }
            copyrightrepo.save(crs);
            submissionAgreement.setSsaCopyrightsForms(crs);
        }

        if (fts != null) {
            for (SsaFormatTypesForm ft : fts) {
                ft.setSubmissionAgreement(submissionAgreement);
            }
            formattyperepo.save(fts);
            submissionAgreement.setSsaFormatTypesForms(fts);
        }

        if (ars != null) {
            for (SsaAccessRestrictionsForm ar : ars) {
                ar.setSubmissionAgreement(submissionAgreement);
            }
            accessrestrictionrepo.save(ars);
            submissionAgreement.setSsaAccessRestrictionsForms(ars);
        }

        submissionAgreement = ssarepo.save(submissionAgreement);
    }

    // ------------------------------------------------------------------------
    @Transactional
    public void saveSsaFormForRsa(SubmissionAgreement submissionAgreement) {
        SubmissionAgreement ssa = ssarepo.findById(submissionAgreement.getId());

        List<SsaContactsForm> repocontacts = ssa.getSsaContactsForms();
        contactrepo.delete(repocontacts);

        List<SsaCopyrightsForm> repocopyrights = ssa.getSsaCopyrightsForms();
        copyrightrepo.delete(repocopyrights);

        List<SsaAccessRestrictionsForm> reporestrictions = ssa.getSsaAccessRestrictionsForms();
        accessrestrictionrepo.delete(reporestrictions);

        ssarepo.save(submissionAgreement);
    }

    // ------------------------------------------------------------------------
    @Transactional
    public SubmitRequestErrors checkForDups(SubmitData submitData) {
        SubmitRequestErrors errors = new SubmitRequestErrors();

        String[] emailparts = submitData.getEmail().split("@");
        String username = "unknown";
        if (emailparts.length < 2) {
            return errors; // all false
        }

        username = emailparts[0];

        List<SubmissionAgreement> ssas = ssarepo.findAllSsasForUsername(username);
        if (ssas.size() == 1) {
            SubmissionAgreement ssa = ssas.get(0);
            if (ssa.getDepartmenthead().equals(submitData.getDepartmenthead()) && ssa.getCreatedby().equals(submitData.getSignature())) {
                Department df = ssa.getDepartment();
                if (df.getName().equals(submitData.getDepartment())) {
                    List<SsaContactsForm> cfs = ssa.getSsaContactsForms();
                    SsaContactsForm matchcf = null;
                    for (SsaContactsForm cf : cfs) {
                        if (cf.getPhone().equals(submitData.getPhone())) {
                            matchcf = cf;
                            break;
                        }
                    }
                    if (matchcf != null) {
                        if (matchcf.getEmail().equals(submitData.getEmail())) {
                            if (matchcf.getName().equals(submitData.getName())) {
                                if (matchcf.getAddress().equals(submitData.getAddress())) {
                                    errors.setSsaid(ssa.getId());
                                    errors.setFullDuplicates(true);
                                }
                            }
                        }
                    }
                }
            }
        }
        return errors;
    }
}
