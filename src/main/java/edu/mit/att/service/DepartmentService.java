package edu.mit.att.service;

import edu.mit.att.entity.Department;

import java.util.List;
import java.util.Set;

public interface DepartmentService {
    public Set<Department> findSkipUserid(int userid);

    public List<Department> findAllOrderByName();

    public List<Department> findAllNotAssociatedWithOtherSsaOrderByName(int thisssaid);

    public boolean isDuplicate(String departmentname);

    public boolean departmentAssignedToUser(int departmentid, int userid);
}
