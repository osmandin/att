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
 * Checks whether info pages exist
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class InfoPageRequestTest {

    private static final String HTTP_LOCALHOST = "http://localhost:";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testHelpPage() throws Exception {
        assertThat(this.restTemplate.getForObject(HTTP_LOCALHOST + port + "/att/Help",
                String.class)).contains("Help");
    }

    @Test
    public void testAboutPage() throws Exception {
        assertThat(this.restTemplate.getForObject(HTTP_LOCALHOST + port + "/att/About",
                String.class)).contains("About");
    }

    @Test
    public void testFaqPage() throws Exception {
        assertThat(this.restTemplate.getForObject(HTTP_LOCALHOST + port + "/att/Faq",
                String.class)).contains("FAQ");
    }




}