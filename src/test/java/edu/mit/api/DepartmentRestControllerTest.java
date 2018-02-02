package edu.mit.api;


import edu.mit.entity.DepartmentsForm;
import edu.mit.repository.DepartmentsFormRepository;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;


@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class DepartmentRestControllerTest {

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));


    private MockMvc mockMvc;

    private String departmentName = "testdept";

    private HttpMessageConverter mappingJackson2HttpMessageConverter;


    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private DepartmentsFormRepository repo;


    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
                .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
                .findAny()
                .orElse(null);

        assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();

        // create a test user object:

        DepartmentsForm departmentsForm = new DepartmentsForm();
        departmentsForm.setName(departmentName);
        this.repo.save(departmentsForm);
    }

    @Ignore
    @Test
    public void findDepartments() throws Exception {
        mockMvc.perform(get("/departments/all"))
                .andExpect(content().string(containsString(departmentName)));
    }

    @Ignore
    @Test
    public void findDepartmentsByName() throws Exception {
        mockMvc.perform(get("/departments").param("name", departmentName))
                .andExpect(content().string(containsString(departmentName)));
    }



}