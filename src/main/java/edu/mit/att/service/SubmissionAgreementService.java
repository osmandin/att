package edu.mit.att.service;

import edu.mit.att.entity.SubmitData;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public interface SubmissionAgreementService {
    public boolean handleSubmissionAgreementForm(SubmitData submitData, HttpServletRequest request, HttpSession session, ModelMap model);
}
