package edu.mit;


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
 * Checks whether the department pages exists (e.g., list departments, edit department)
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class DepartmentHttpRequestTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void greetingShouldReturnDefaultMessage() throws Exception {
        assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/att/ListDepartments",
                String.class)).contains("Manage Departments");
    }

    /**
     * Tests whether edit department page is up and running
     */
    @Test
    public void testEditPage() throws Exception {
        //TODO better way to pass param?
        assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/att/EditDepartment/?departmentid=1",
                String.class)).contains("Update Item");
    }

    /**
     * Test doing a GET
     */
    @Test
    public void testAddPage() throws Exception {
        assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/att/AddDepartment",
                String.class)).contains("New");
    }

    /**
     * Test adding a POST (add a department)
     */
    @Test
    public void testAddDepartment() throws Exception {
        MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
        final String testDepartment = "whackydepartment";
        requestMap.add("name", testDepartment);
        assertThat(restTemplate.postForObject("http://localhost:" + port + "/att/AddDepartment", requestMap, String.class)).contains(testDepartment);
    }


}