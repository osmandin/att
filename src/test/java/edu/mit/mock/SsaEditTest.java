package edu.mit.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.mit.controllers.DepartmentAdmin;
import edu.mit.controllers.SsaAdmin;
import edu.mit.entity.DepartmentsForm;
import edu.mit.entity.SsasForm;
import edu.mit.repository.DepartmentsFormRepository;
import edu.mit.repository.SsasFormRepository;
import edu.mit.service.DepartmentsFormService;
import edu.mit.service.SsasFormService;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.View;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class SsaEditTest {

    @InjectMocks
    private SsaAdmin controller;

    @Mock
    private View view;

    private MockMvc mockMvc;

    @Mock
    private DepartmentsFormRepository departmentsService;

    @Mock
    private DepartmentsFormService departmentservice;

    @Mock
    private SsasFormRepository ssasFormRepository;

    @Mock
    private SsasFormService ssasFormService;

    @Mock
    private Environment env;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setSingleView(view)
                .build();
    }

    //TODO
    @Ignore
    @Test
    public void test() throws Exception {
        final DepartmentsForm dept = new DepartmentsForm();
        dept.setName("test123");
        when(departmentsService.save(dept)).thenReturn(dept);
        List<DepartmentsForm> testList = new ArrayList<>();
        testList.add(dept);
        when(departmentsService.findAll()).thenReturn(testList);
        when(ssasFormRepository.findAllForDepartmentId(1)).thenReturn(Collections.emptyList());

        SsasForm s = new SsasForm();


        // see: https://stackoverflow.com/questions/4339207/http-post-with-request-content-type-form-not-working-in-spring-mvc-3/31083802#31083802

        mockMvc.perform(post("/CreateSsa?departmentid=1")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("name", "test123"),
                        new BasicNameValuePair("recordstitle", "test"))
                ))))                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void testEdit() throws Exception {
        final DepartmentsForm dept = new DepartmentsForm();
        dept.setName("test123");
        when(departmentsService.save(dept)).thenReturn(dept);
        List<DepartmentsForm> testList = new ArrayList<>();
        testList.add(dept);
        when(departmentsService.findAll()).thenReturn(testList);
        when(ssasFormRepository.findAllForDepartmentId(1)).thenReturn(Collections.emptyList());
        when(departmentservice.findAllNotAssociatedWithOtherSsaOrderByName(1)).thenReturn(Collections.emptyList());
        mockMvc.perform(post("/EditSsa?id=1")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("name", "test123"),
                        new BasicNameValuePair("recordstitle", "test"))
                ))))                .andExpect(status().isOk())
                .andReturn();
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