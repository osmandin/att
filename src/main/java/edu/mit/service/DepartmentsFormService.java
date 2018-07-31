package edu.mit.service;

import edu.mit.entity.*;

import java.util.List;
import java.util.Set;

public interface DepartmentsFormService {
    public Set<DepartmentsForm> findSkipUserid(int userid);

    public List<DepartmentsForm> findAllOrderByName();

    public List<DepartmentsForm> findAllNotAssociatedWithOtherSsaOrderByName(int thisssaid);

    public boolean isDuplicate(String departmentname);

    public boolean departmentAssignedToUser(int departmentid, int userid);
}
