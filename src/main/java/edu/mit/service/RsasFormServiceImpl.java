package edu.mit.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import edu.mit.entity.*;
import edu.mit.repository.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.logging.Logger;

@Service
public class RsasFormServiceImpl implements RsasFormService {
    private final static Logger LOGGER = Logger.getLogger(RsasFormServiceImpl.class.getCanonicalName());

    @Resource
    private RsasFormRepository repo;

    @Resource
    private SsasFormRepository ssarepo;

    @Resource
    private SsasFormService ssaservice;

    @Resource
    private RsaFileDataFormRepository filedatarepo;

    @Transactional
    public void saveForm(RsasForm rsa) {

        ssaservice.saveSsaFormForRsa(rsa.getSsasForm());

        List<RsaFileDataForm> fds = rsa.getRsaFileDataForms();
        filedatarepo.save(fds);

        repo.save(rsa);
    }
}
