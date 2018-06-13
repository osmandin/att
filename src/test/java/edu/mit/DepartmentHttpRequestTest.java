package edu.mit;


import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks whether the department pages exists (e.g., list departments, edit department, add department)
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class DepartmentHttpRequestTest {

    private static final String HTTP_LOCALHOST = "http://localhost:";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testListPage() throws Exception {
        assertThat(this.restTemplate.getForObject(HTTP_LOCALHOST + port + "/att/ListDepartments",
                String.class)).containsIgnoringCase("Manage Departments");
    }

    /**
     * Tests whether edit department page is up and running
     */
    @Ignore
    @Test
    public void testEditPage() throws Exception {
        assertThat(this.restTemplate.getForObject(HTTP_LOCALHOST + port + "/att/EditDepartment/?departmentid=1",
                String.class)).containsIgnoringCase("Update department");
    }

    /**
     * Test doing a GET for adding a department
     */
    @Test
    public void testAddPage() throws Exception {
        assertThat(this.restTemplate.getForObject(HTTP_LOCALHOST + port + "/att/AddDepartment",
                String.class)).containsIgnoringCase("Add new department");
    }

    /**
     * Test adding a POST (add a department)
     */
    @Test
    public void testAddDepartment() throws Exception {
        final MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
        final String testDepartment = "whackydepartment";
        requestMap.add("name", testDepartment);
        assertThat(restTemplate.postForObject(HTTP_LOCALHOST+ port + "/att/AddDepartment", requestMap, String.class))
                .contains(testDepartment);
    }


}