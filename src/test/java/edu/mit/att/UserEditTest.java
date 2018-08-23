package edu.mit.att;

import edu.mit.att.controllers.UserAdmin;
import edu.mit.att.entity.Department;
import edu.mit.att.entity.User;
import edu.mit.att.repository.DepartmentRepository;
import edu.mit.att.repository.SubmissionAgreementRepository;
import edu.mit.att.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.View;

import java.util.*;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


public class UserEditTest {

    @InjectMocks
    private UserAdmin controller;

    @Mock
    private View view;

    private MockMvc mockMvc;

    @Mock
    private DepartmentRepository departmentsService;

    @Mock
    private SubmissionAgreementRepository submissionAgreementRepository;

    @Mock
    private UserRepository userrepo;

    @Mock
    private DepartmentRepository departmentrepo;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setSingleView(view)
                .build();
    }

    @Test
    public void testGet() throws Exception {
        final Department dept = new Department();
        dept.setName("test123");
        when(departmentsService.save(dept)).thenReturn(dept);

        List<Department> departments = new ArrayList<>();
        departments.add(dept);

        when(departmentsService.findAll()).thenReturn(departments);
        when(submissionAgreementRepository.findAllForDepartmentId(1)).thenReturn(Collections.emptyList());

        final Set<Department> set = new HashSet<>();
        set.add(dept);
        final User user = new User();
        user.setDepartments(set);
        user.setEmail("test@mit.edu");

        final List<User> userList = new ArrayList<>();
        userList.add(user);

        when(userrepo.findByEmail("test@mit.edu")).thenReturn(userList);
        when(departmentrepo.findById(1)).thenReturn(dept);

        mockMvc.perform(get("/ListUsers").requestAttr("mail", "test@mit.edu"))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void testGetWithNoPermissions() throws Exception {
        final Set<Department> set = new HashSet<>();
        set.add(new Department());
        final User user = new User();
        user.setDepartments(set);
        user.setEmail("test@mit.edu");

        final List<User> userList = new ArrayList<>();
        userList.add(user);

        when(userrepo.findByEmail("test@mit.edu")).thenReturn(Collections.emptyList()); // note the empty

        mockMvc.perform(get("/ListUsers").requestAttr("mail", "test@mit.edu"))
                .andExpect(status().isOk())
                .andExpect(view().name("Permissions"));
    }


}