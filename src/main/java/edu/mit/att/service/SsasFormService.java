package edu.mit.att.service;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import edu.mit.att.entity.Department;
import edu.mit.att.entity.SsasForm;
import edu.mit.att.entity.SubmitData;
import edu.mit.att.entity.SubmitRequestErrors;

public interface SsasFormService {
    public SubmitRequestErrors checkForDups(SubmitData submitData);

    public void saveForm(SsasForm ssasForm, Department selectedDepartment);

    public void saveFormTest(SsasForm ssasForm, Department selectedDepartment);

    public void saveSsaFormForRsa(SsasForm ssasForm);

    public void create(SsasForm ssasForm, Department selectedDepartment, HttpSession session, HttpServletRequest request);
}
