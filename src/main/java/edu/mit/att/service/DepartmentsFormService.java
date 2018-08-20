package edu.mit.att.service;

import edu.mit.att.entity.DepartmentsForm;

import java.util.List;
import java.util.Set;

public interface DepartmentsFormService {
    public Set<DepartmentsForm> findSkipUserid(int userid);

    public List<DepartmentsForm> findAllOrderByName();

    public List<DepartmentsForm> findAllNotAssociatedWithOtherSsaOrderByName(int thisssaid);

    public boolean isDuplicate(String departmentname);

    public boolean departmentAssignedToUser(int departmentid, int userid);
}
