package edu.mit.att.service;

import edu.mit.att.entity.Department;
import edu.mit.att.entity.SubmissionAgreement;
import edu.mit.att.entity.User;
import edu.mit.att.repository.DepartmentRepository;
import edu.mit.att.repository.SubmissionAgreementRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class DepartmentServiceImpl implements DepartmentService {
    private final static Logger LOGGER = Logger.getLogger(DepartmentServiceImpl.class.getCanonicalName());

    @Resource
    private DepartmentRepository departmentrepo;

    @Resource
    private SubmissionAgreementRepository ssarepo;

    // ------------------------------------------------------------------------
    @Transactional
    public Set<Department> findSkipUserid(int userid) {

        List<Department> dfs = departmentrepo.findAllOrderByNameAsc();
        if (dfs == null) {
            LOGGER.log(Level.SEVERE, "findAllOrderByNameAsc null");
            return null;
        }

        Set<Department> newdfs = new HashSet<>();
        for (Department df : dfs) {
            List<User> ufs = df.getUsers();
            boolean found = false;
            for (User uf : ufs) {
                if (uf.getId() == userid) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                //LOGGER.log(Level.INFO, "displaying department depid={0} depname={1} active={2} for userid={3}", new Object[]{df.getId(), df.getName(), df.isActive(), userid});
                newdfs.add(df);
            }
        }
        return newdfs;
    }

    // ------------------------------------------------------------------------
    @Transactional
    public List<Department> findAllOrderByName() {
        return departmentrepo.findAll(sortByNameAsc());
    }

    // ------------------------------------------------------------------------
    private Sort sortByNameAsc() {
        return new Sort(Sort.Direction.ASC, "name");
    }

    // ------------------------------------------------------------------------
    @Transactional
    public boolean isDuplicate(String departmentname) {
        List<Department> ds = departmentrepo.findByName(departmentname);
        return ds.size() > 0;
    }

    // ------------------------------------------------------------------------
    @Transactional
    public boolean departmentAssignedToUser(int departmentid, int userid) {

        LOGGER.log(Level.INFO, "initial departmentAssignedToUser: departmentid={0} userid={1}", new Object[]{departmentid, userid});

        List<Department> ds = departmentrepo.findBasedOnIdAndUserid(departmentid, userid);

        Department df = departmentrepo.findById(departmentid);
        if (df == null) {
            return false;
        }

        List<User> ufs = df.getUsers();
        if (ufs == null) {
            return false;
        }

        boolean found = false;
        for (User uf : ufs) {
            if (uf.getId() == userid) {
                found = true;
                break;
            }
        }
        //LOGGER.log(Level.INFO, "departmentAssignedToUser: departmentid={0} userid={1} found={2} size={3}", new Object[]{departmentid, userid, found, ds.size()});
        return found;
    }

    // ------------------------------------------------------------------------
    @Transactional
    public List<Department> findAllNotAssociatedWithOtherSsaOrderByName(int thisssaid) {

        List<Department> dfs = departmentrepo.findAllOrderByNameAsc();

        if (dfs == null) {
            LOGGER.log(Level.INFO, "no departments");
            return dfs;
        }

        List<SubmissionAgreement> ssas = ssarepo.findAll();

        List<Department> newdfs = new ArrayList<Department>();
        for (Department df : dfs) {
            //LOGGER.log(Level.INFO, "checking: thisssaid={0} depart ssaid={1}", new Object[]{thisssaid, df.getSubmissionAgreement().getId()});

            if (df == null || df.getSubmissionAgreement() == null) {

            } else if (df.getSubmissionAgreement().getId() == thisssaid) {
                //LOGGER.log(Level.INFO, "adding the department for this ssaid={0}", new Object[]{df.getSubmissionAgreement().getId()});
                newdfs.add(df);
            } else {
                boolean found = false;
                for (SubmissionAgreement ssa : ssas) {

                    if (ssa.getDepartment() == null) {
                        LOGGER.log(Level.INFO, "Null department for SSA:{}" + ssa.getId());
                        continue;
                    }

                    if (ssa.getDepartment().getId() == df.getId()) {
                        //LOGGER.log(Level.INFO, "found for ssaid={0}", new Object[]{ssa.getId()});
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    newdfs.add(df);
                }
            }
        }
        //LOGGER.log(Level.INFO, "returning newdfs.size()={0}", new Object[]{newdfs.size()});
        return newdfs;
    }
}
