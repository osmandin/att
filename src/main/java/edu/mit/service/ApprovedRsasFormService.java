package edu.mit.service;

import edu.mit.entity.*;
import javax.servlet.http.HttpSession;

public interface ApprovedRsasFormService {
    public String findAllApprovedTransfersCSV();

    public void recordDeletedRsa(RsasForm rsa, HttpSession session);
}
