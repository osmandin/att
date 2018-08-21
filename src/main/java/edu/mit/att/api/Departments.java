package edu.mit.att.api;


import edu.mit.att.entity.Department;
import edu.mit.att.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class Departments {

    @Autowired
    private DepartmentRepository repo;

    @RequestMapping("/departments")
    public Department getusers(@RequestParam(value = "name", defaultValue = "test") String name) {
        final List<Department> departmentList = repo.findByName(name);
        if (departmentList.isEmpty()) {
            return new Department();
        }
        return departmentList.get(0);
    }

    @RequestMapping("/departments/all")
    public List<Department> getDepartmentsAll() {
        final List<Department> departmentList = repo.findAll();
        return departmentList;
    }
}
