package edu.mit.service;

import org.springframework.ui.ModelMap;
import edu.mit.entity.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public interface SubmissionAgreementService {
    public boolean handleSubmissionAgreementForm(SubmitData submitData, HttpServletRequest request, HttpSession session, ModelMap model);
}
