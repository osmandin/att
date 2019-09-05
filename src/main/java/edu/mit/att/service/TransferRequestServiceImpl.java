package edu.mit.att.service;

import edu.mit.att.entity.RsaFileDataForm;
import edu.mit.att.entity.TransferRequest;
import edu.mit.att.repository.RsaFileDataFormRepository;
import edu.mit.att.repository.TransferRequestRepository;
import edu.mit.att.repository.SubmissionAgreementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class TransferRequestServiceImpl implements TransferRequestService {
    private final static Logger LOGGER = Logger.getLogger(TransferRequestServiceImpl.class.getCanonicalName());

    @Resource
    private TransferRequestRepository repo;

    @Resource
    private SubmissionAgreementRepository ssarepo;

    @Resource
    private SsasFormService ssaservice;

    @Resource
    private RsaFileDataFormRepository filedatarepo;

    @Transactional
    public void saveForm(TransferRequest rsa) {

        ssaservice.saveSsaFormForRsa(rsa.getSubmissionAgreement());

        List<RsaFileDataForm> fds = rsa.getRsaFileDataForms();
        filedatarepo.save(fds);

        repo.save(rsa);

        LOGGER.log(Level.INFO, "Saved:" + rsa);
    }
}
