package edu.mit.att.authz;

import edu.mit.att.entity.UsersFormBuilder;
import edu.mit.att.service.UsersFormService;
import edu.mit.att.entity.UsersForm;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.when;

public class SubjectTest {


    @Mock
    private UsersFormService usersFormService;

    @InjectMocks
    private Subject subjectTest;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);

    }

    @Test
    public void shouldGetRole() throws Exception {
        final String user1Email = "test1@abc.edu";
        final UsersForm user1= new UsersFormBuilder().setEmail(user1Email).setRole("siteadmin").createUsersForm();

        final String user2Email = "test2@xyz.edu";
        final UsersForm user2= new UsersFormBuilder().setEmail(user2Email).setRole("").createUsersForm();

        final List<UsersForm> users1 = new ArrayList<>();
        users1.add(user1);
        when(usersFormService.findByEmail(user1Email)).thenReturn(users1);

        final List<UsersForm> users2 = new ArrayList<>();
        users2.add(user2);
        when(usersFormService.findByEmail(user2Email)).thenReturn(users2);

        final Role role1 = subjectTest.getRole(user1Email);
        assertTrue(role1.equals(Role.siteadmin));

        final Role role2 = subjectTest.getRole(user2Email);
        assertTrue(role2.equals(Role.visitor));

        final Role role3 = subjectTest.getRole("nope@a.edu");
        assertTrue(role3.equals(Role.visitor));
    }


}
