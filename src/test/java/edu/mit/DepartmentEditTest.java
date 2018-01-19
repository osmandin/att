package edu.mit;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.mit.controllers.DepartmentAdmin;
import edu.mit.entity.DepartmentsForm;
import edu.mit.repository.DepartmentsFormRepository;
import edu.mit.repository.SsasFormRepository;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.View;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.sun.org.apache.xerces.internal.util.PropertyState.is;
import static org.hamcrest.core.AllOf.allOf;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class DepartmentEditTest {

    @InjectMocks
    private DepartmentAdmin controller;

    @Mock
    private View view;

    private MockMvc mockMvc;

    @Mock
    private DepartmentsFormRepository departmentsService;

    @Mock
    private SsasFormRepository ssasFormRepository;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setSingleView(view)
                .build();
    }

    @Test
    public void test() throws Exception {
        final DepartmentsForm dept = new DepartmentsForm();
        dept.setName("test123");
        when(departmentsService.save(dept)).thenReturn(dept);
        List<DepartmentsForm> testList = new ArrayList<>();
        testList.add(dept);
        when(departmentsService.findAll()).thenReturn(testList);
        when(ssasFormRepository.findAllForDepartmentId(1)).thenReturn(Collections.emptyList());

        // see: https://stackoverflow.com/questions/4339207/http-post-with-request-content-type-form-not-working-in-spring-mvc-3/31083802#31083802

        mockMvc.perform(post("/EditDepartment?departmentid=1")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("name", "test123"))
                ))))
                .andExpect(status().isOk())
                .andReturn();

       /* mockMvc.perform(get("/EditDepartment?departmentid=1"))
                .andExpect(model().attribute("departmentForm", allOf(
                        hasProperty("name", equalTo("test123")))
                ));*/


    }

    /*
     * converts a Java object into JSON representation
     */
    static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}