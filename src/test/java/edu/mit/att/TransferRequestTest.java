package edu.mit.att;


import edu.mit.att.entity.Department;
import edu.mit.att.entity.SubmissionAgreement;
import edu.mit.att.entity.TransferRequest;
import edu.mit.att.repository.DepartmentRepository;
import edu.mit.att.repository.SubmissionAgreementRepository;
import edu.mit.att.repository.TransferRequestRepository;
import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.*;
import java.util.Date;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks whether the entity pages exists (e.g., list, edit, add)
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TransferRequestTest {

    // Change these as necessary.
    private static final String HTTP_LOCALHOST = "http://localhost:";
    //static final String POST_ENDPOINT = "/att/CreateSsa";
    static final String LIST_ENDPOINT = "/att/ListDraftRsas";
    static final String DOWNLOAD_ENDPOINT = "/att/DownloadZipFile?rsaid=1&redirect=ListDraftRsas/";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    TransferRequestRepository repo;

    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    SubmissionAgreementRepository submissionAgreementRepository;

    @Test
    public void testListPage() throws Exception {
        final Department department = new Department();
        department.setName("SIGMA-123");
        departmentRepository.save(department);
        SubmissionAgreement submissionAgreement = new SubmissionAgreement();
        submissionAgreement.setDepartment(department);
        submissionAgreementRepository.save(submissionAgreement);

        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setSubmissionAgreement(submissionAgreement);

        final String path = "/tmp/test-att/"; //TODO ideally we create a file here.

        transferRequest.setPath(path);

        final Date d = new Date();
        transferRequest.setTransferdate(d.toString());
        repo.save(transferRequest);

        final String s = this.restTemplate.getForObject(HTTP_LOCALHOST + port + LIST_ENDPOINT,
                String.class);

        assertThat(s).contains("Review Draft Transfer Requests");
        assertThat(s).contains("SIGMA-123");
        assertThat(s).contains(d.toString());
        //assertThat(s).contains("1 document");

        // test that file was saved:

        final ResponseEntity<byte[]> result = restTemplate.exchange(HTTP_LOCALHOST + port + DOWNLOAD_ENDPOINT, HttpMethod.GET, null, byte[].class);

        assertThat(result.getStatusCode().is2xxSuccessful());

        final byte[] contents = result.getBody();

        final String outputPath = "/tmp/test-att-output/"; //TODO ideally we create a file here.

        final File f = new File(outputPath + "download.zip");
        final FileOutputStream out = new FileOutputStream(f);

        out.write(contents, 0, contents.length);
        out.close();

        String zipContents = readString(outputPath + "download.zip");
        System.out.println("zip contents: " + zipContents);
        assertNotNull(zipContents);
        String test = "input match";
        System.out.println(zipContents.length());
        assertThat(zipContents).contains(test);

    }

    private String readString(final String path) throws IOException {
        System.out.println("Reading:" + path);
        final ZipFile zipFile = new ZipFile(path);
        final Enumeration<? extends ZipEntry> entries = zipFile.entries();

        while(entries.hasMoreElements()){
            ZipEntry entry = entries.nextElement();
            System.out.println(
                    entry.getName());
            if (entry.getName().equals("a.txt")) {
                final StringWriter writer = new StringWriter();
                IOUtils.copy(zipFile.getInputStream(entry), writer, "UTF-8");
                final String s = writer.toString();
                System.out.println("Returning:" + s);
                return s;
            }
        }

        return " ";
    }



}