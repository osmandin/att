package edu.mit.att.service;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import edu.mit.att.entity.Department;
import edu.mit.att.entity.SubmissionAgreement;
import edu.mit.att.entity.SubmitData;
import edu.mit.att.entity.SubmitRequestErrors;

public interface SsasFormService {
    public SubmitRequestErrors checkForDups(SubmitData submitData);

    public void saveForm(SubmissionAgreement submissionAgreement, Department selectedDepartment);

    public void saveFormTest(SubmissionAgreement submissionAgreement, Department selectedDepartment);

    public void saveSsaFormForRsa(SubmissionAgreement submissionAgreement);

    public void create(SubmissionAgreement submissionAgreement, Department selectedDepartment, HttpSession session, HttpServletRequest request);
}
