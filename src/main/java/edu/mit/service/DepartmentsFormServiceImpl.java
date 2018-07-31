package edu.mit.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import edu.mit.entity.*;
import edu.mit.repository.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class DepartmentsFormServiceImpl implements DepartmentsFormService {
    private final static Logger LOGGER = Logger.getLogger(DepartmentsFormServiceImpl.class.getCanonicalName());

    @Resource
    private DepartmentsFormRepository departmentrepo;

    @Resource
    private SsasFormRepository ssarepo;

    // ------------------------------------------------------------------------
    @Transactional
    public Set<DepartmentsForm> findSkipUserid(int userid) {

        List<DepartmentsForm> dfs = departmentrepo.findAllOrderByNameAsc();
        if (dfs == null) {
            LOGGER.log(Level.SEVERE, "findAllOrderByNameAsc null");
            return null;
        }

        Set<DepartmentsForm> newdfs = new HashSet<>();
        for (DepartmentsForm df : dfs) {
            List<UsersForm> ufs = df.getUsersForms();
            boolean found = false;
            for (UsersForm uf : ufs) {
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
    public List<DepartmentsForm> findAllOrderByName() {
        return departmentrepo.findAll(sortByNameAsc());
    }

    // ------------------------------------------------------------------------
    private Sort sortByNameAsc() {
        return new Sort(Sort.Direction.ASC, "name");
    }

    // ------------------------------------------------------------------------
    @Transactional
    public boolean isDuplicate(String departmentname) {
        List<DepartmentsForm> ds = departmentrepo.findByName(departmentname);
        return ds.size() > 0;
    }

    // ------------------------------------------------------------------------
    @Transactional
    public boolean departmentAssignedToUser(int departmentid, int userid) {

        LOGGER.log(Level.INFO, "initial departmentAssignedToUser: departmentid={0} userid={1}", new Object[]{departmentid, userid});

        List<DepartmentsForm> ds = departmentrepo.findBasedOnIdAndUserid(departmentid, userid);

        DepartmentsForm df = departmentrepo.findById(departmentid);
        if (df == null) {
            return false;
        }

        List<UsersForm> ufs = df.getUsersForms();
        if (ufs == null) {
            return false;
        }

        boolean found = false;
        for (UsersForm uf : ufs) {
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
    public List<DepartmentsForm> findAllNotAssociatedWithOtherSsaOrderByName(int thisssaid) {

        List<DepartmentsForm> dfs = departmentrepo.findAllOrderByNameAsc();

        if (dfs == null) {
            LOGGER.log(Level.INFO, "no departments");
            return dfs;
        }

        List<SsasForm> ssas = ssarepo.findAll();

        List<DepartmentsForm> newdfs = new ArrayList<DepartmentsForm>();
        for (DepartmentsForm df : dfs) {
            //LOGGER.log(Level.INFO, "checking: thisssaid={0} depart ssaid={1}", new Object[]{thisssaid, df.getSsasForm().getId()});

            if (df == null || df.getSsasForm() == null) {

            } else if (df.getSsasForm().getId() == thisssaid) {
                //LOGGER.log(Level.INFO, "adding the department for this ssaid={0}", new Object[]{df.getSsasForm().getId()});
                newdfs.add(df);
            } else {
                boolean found = false;
                for (SsasForm ssa : ssas) {
                    if (ssa.getDepartmentForm().getId() == df.getId()) {
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
