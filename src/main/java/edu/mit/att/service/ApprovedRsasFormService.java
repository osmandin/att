package edu.mit.att.service;

import edu.mit.att.entity.RsasForm;

import javax.servlet.http.HttpSession;

public interface ApprovedRsasFormService {
    public String findAllApprovedTransfersCSV();

    public void recordDeletedRsa(RsasForm rsa, HttpSession session);
}
