package edu.mit.att.service;

import edu.mit.att.entity.RsaFileDataForm;
import edu.mit.att.entity.RsasForm;
import edu.mit.att.repository.RsaFileDataFormRepository;
import edu.mit.att.repository.RsasFormRepository;
import edu.mit.att.repository.SubmissionAgreementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.logging.Logger;

@Service
public class RsasFormServiceImpl implements RsasFormService {
    private final static Logger LOGGER = Logger.getLogger(RsasFormServiceImpl.class.getCanonicalName());

    @Resource
    private RsasFormRepository repo;

    @Resource
    private SubmissionAgreementRepository ssarepo;

    @Resource
    private SsasFormService ssaservice;

    @Resource
    private RsaFileDataFormRepository filedatarepo;

    @Transactional
    public void saveForm(RsasForm rsa) {

        ssaservice.saveSsaFormForRsa(rsa.getSubmissionAgreement());

        List<RsaFileDataForm> fds = rsa.getRsaFileDataForms();
        filedatarepo.save(fds);

        repo.save(rsa);
    }
}
