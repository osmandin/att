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
 * Checks whether the entity pages exists (e.g., list, edit, add)
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SsaHttpRequestTest {

    // Change these as necessary.
    private static final String HTTP_LOCALHOST = "http://localhost:";
    static final String POST_ENDPOINT = "/att/CreateSsa";
    static final String LIST_ENDPOINT = "/att/ListSsas";
    static final String EDIT_ENDPOINT = "/att/EditSsa/";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testListPage() throws Exception {
        assertThat(this.restTemplate.getForObject(HTTP_LOCALHOST + port + LIST_ENDPOINT,
                String.class)).contains("Manage Submission Agreements");
    }

    /**
     * Test doing a GET for adding an entity
     */
    @Test
    public void testAddPage() throws Exception {
        assertThat(this.restTemplate.getForObject(HTTP_LOCALHOST + port + POST_ENDPOINT,
                String.class)).containsIgnoringCase("Create a Submission Agreement");
    }

    /**
     * Test adding a POST (add an entity)
     */
    @Test
    public void testAddSSa() throws Exception {
        final MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
        final String testDepartment = "1";
        requestMap.add("departmentid", testDepartment);
        requestMap.add("recordid", "test");
        //System.out.println("Value:" + restTemplate.postForObject(HTTP_LOCALHOST+ port + POST_ENDPOINT, requestMap, String.class));
        assertThat(restTemplate.postForObject(HTTP_LOCALHOST+ port + POST_ENDPOINT, requestMap, String.class))
                .contains(testDepartment);
    }

    /**
     * Tests whether edit page is up and running
     */
    @Test
    public void testEditPage() throws Exception {
        final MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
        final String testDepartment = "1";
        requestMap.add("departmentid", testDepartment);
        requestMap.add("recordid", "test");

        // First create object:

        assertThat(restTemplate.postForObject(HTTP_LOCALHOST+ port + POST_ENDPOINT, requestMap, String.class))
                .contains(testDepartment);

        assertThat(this.restTemplate.getForObject(HTTP_LOCALHOST + port + EDIT_ENDPOINT + "?id=1",
                String.class)).contains("Edit Submission Agreement");
    }


}