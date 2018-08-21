package edu.mit.att.service;

import edu.mit.att.entity.TransferRequest;

import javax.servlet.http.HttpSession;

public interface ApprovedRsasFormService {
    public String findAllApprovedTransfersCSV();

    public void recordDeletedRsa(TransferRequest rsa, HttpSession session);
}
