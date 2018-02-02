package edu.mit.service;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import edu.mit.entity.*;

public interface SsasFormService {
    public SubmitRequestErrors checkForDups(SubmitData submitData);

    public void saveForm(SsasForm ssasForm, DepartmentsForm selectedDepartmentsForm);

    public void saveFormTest(SsasForm ssasForm, DepartmentsForm selectedDepartmentsForm);

    public void saveSsaFormForRsa(SsasForm ssasForm);

    public void create(SsasForm ssasForm, DepartmentsForm selectedDepartmentsForm, HttpSession session, HttpServletRequest request);
}
