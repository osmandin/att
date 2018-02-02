package edu.mit.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import edu.mit.entity.*;
import edu.mit.repository.*;

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
    private DepartmentsFormRepository departmentsFormRepository;

    // ------------------------------------------------------------------------
    @Transactional
    public void saveForm(SsasForm ssasForm, DepartmentsForm selectedDepartmentsForm) {

        int ssaid = ssasForm.getId();


        ssasForm.setDepartmentForm(selectedDepartmentsForm);

        // in form
        //ssasForm.setCreatedby(session.getAttribute("name").toString());
        //ssasForm.setIP(request.getRemoteAddr());
        //ssasForm.setEditdate(String.format("%1$tY-%1$tm-%1$td", Calendar.getInstance()));

        SsasForm ssa = ssarepo.findById(ssaid);

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
        ssarepo.save(ssasForm);
    }

    @Transactional
    public void saveFormTest(SsasForm ssasForm, DepartmentsForm selectedDepartmentsForm) {

        int ssaid = ssasForm.getId();


        ssasForm.setDepartmentForm(departmentsFormRepository.findAll().get(0));

        // in form
        //ssasForm.setCreatedby(session.getAttribute("name").toString());
        //ssasForm.setIP(request.getRemoteAddr());
        //ssasForm.setEditdate(String.format("%1$tY-%1$tm-%1$td", Calendar.getInstance()));

        SsasForm ssa = ssarepo.findById(ssaid);

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
        ssarepo.save(ssasForm);

    }

    // ------------------------------------------------------------------------
    @Transactional
    public void create(SsasForm ssasForm, DepartmentsForm selectedDepartmentsForm, HttpSession session, HttpServletRequest request) {
        ssasForm.setCreatedby("osman"); //TODO

        //ssasForm.setIP(request.getRemoteAddr());
        ssasForm.setEditdate(String.format("%1$tY-%1$tm-%1$td", Calendar.getInstance()));

        ssasForm.setDepartmentForm(selectedDepartmentsForm);
        List<SsaCopyrightsForm> crs = ssasForm.getSsaCopyrightsForms();
        List<SsaFormatTypesForm> fts = ssasForm.getSsaFormatTypesForms();
        List<SsaAccessRestrictionsForm> ars = ssasForm.getSsaAccessRestrictionsForms();
        ssasForm.setSsaCopyrightsForms(null);
        ssasForm.setSsaFormatTypesForms(null);
        ssasForm.setSsaAccessRestrictionsForms(null);
        ssasForm = ssarepo.save(ssasForm);

        if (crs != null) {
            for (SsaCopyrightsForm cr : crs) {
                cr.setSsasForm(ssasForm);
            }
            copyrightrepo.save(crs);
            ssasForm.setSsaCopyrightsForms(crs);
        }

        if (fts != null) {
            for (SsaFormatTypesForm ft : fts) {
                ft.setSsasForm(ssasForm);
            }
            formattyperepo.save(fts);
            ssasForm.setSsaFormatTypesForms(fts);
        }

        if (ars != null) {
            for (SsaAccessRestrictionsForm ar : ars) {
                ar.setSsasForm(ssasForm);
            }
            accessrestrictionrepo.save(ars);
            ssasForm.setSsaAccessRestrictionsForms(ars);
        }

        ssasForm = ssarepo.save(ssasForm);
    }

    // ------------------------------------------------------------------------
    @Transactional
    public void saveSsaFormForRsa(SsasForm ssasForm) {
        SsasForm ssa = ssarepo.findById(ssasForm.getId());

        List<SsaContactsForm> repocontacts = ssa.getSsaContactsForms();
        contactrepo.delete(repocontacts);

        List<SsaCopyrightsForm> repocopyrights = ssa.getSsaCopyrightsForms();
        copyrightrepo.delete(repocopyrights);

        List<SsaAccessRestrictionsForm> reporestrictions = ssa.getSsaAccessRestrictionsForms();
        accessrestrictionrepo.delete(reporestrictions);

        ssarepo.save(ssasForm);
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

        List<SsasForm> ssas = ssarepo.findAllSsasForUsername(username);
        if (ssas.size() == 1) {
            SsasForm ssa = ssas.get(0);
            if (ssa.getDepartmenthead().equals(submitData.getDepartmenthead()) && ssa.getCreatedby().equals(submitData.getSignature())) {
                DepartmentsForm df = ssa.getDepartmentForm();
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
