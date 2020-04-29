package edu.mit.att;


import edu.mit.att.entity.Department;
import edu.mit.att.entity.SubmissionAgreement;
import edu.mit.att.entity.TransferRequest;
import edu.mit.att.repository.DepartmentRepository;
import edu.mit.att.repository.SubmissionAgreementRepository;
import edu.mit.att.repository.TransferRequestRepository;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
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
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * Checks whether the entity pages exists (e.g., list, edit, add)
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TransferRequestTest {

    // Change these as necessary.
    private static final String HTTP_LOCALHOST = "http://localhost:";

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

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testListPage() throws Exception {
        final Department department = new Department();
        department.setName("SIGMA-123");
        departmentRepository.save(department);
        SubmissionAgreement submissionAgreement = new SubmissionAgreement();
        submissionAgreement.setDepartment(department);
        submissionAgreementRepository.save(submissionAgreement);

        final TransferRequest transferRequest = new TransferRequest();
        transferRequest.setSubmissionAgreement(submissionAgreement);
        transferRequest.setDepartment("CSAIL");
        transferRequest.setTheses("1");
        transferRequest.setDegrees("BS");

        // TODO just use TemporaryFolder
        final Path rootDirectory = FileSystems.getDefault().getPath(temporaryFolder.newFolder().getAbsolutePath());
        final Path tempDirectory = Files.createTempDirectory(rootDirectory, "");

        if (!tempDirectory.toFile().exists()) {
            fail("Could not create temporary file");
        }


        final String testString = "input is good"; // string to write and expect back

        try {

            final File f = new File(tempDirectory + "/a.txt");
            FileUtils.writeStringToFile(f, testString);

            transferRequest.setPath(tempDirectory.toString());

            final Date d = new Date();
            transferRequest.setTransferdate(d.toString());
            repo.save(transferRequest);


        } catch (IOException e) {
            fail(e.toString());
        }

        final String s = this.restTemplate.getForObject(HTTP_LOCALHOST + port + LIST_ENDPOINT,
                String.class);

        assertThat(s).contains("Review Draft Transfer Requests");
        assertThat(s).contains("SIGMA-123");
        //assertThat(s).contains(d.toString());
        //assertThat(s).contains("1 document");

        // test that file was saved (by downloading it and reading it):

        final ResponseEntity<byte[]> result = restTemplate.exchange(HTTP_LOCALHOST + port + DOWNLOAD_ENDPOINT,
                HttpMethod.GET, null, byte[].class);

        assertThat(result.getStatusCode().is2xxSuccessful());

        final byte[] contents = result.getBody();
        final Path outputDir = Files.createTempDirectory(tempDirectory, "output");
        final String outputPath = outputDir.toString();
        final File f = new File(outputPath + "download.zip");
        final FileOutputStream out = new FileOutputStream(f);

        out.write(contents, 0, contents.length);
        out.close();

        final String zipFileString = readString(outputPath + "download.zip");
        assertNotNull(zipFileString);
        assertThat(zipFileString).contains(testString);

    }

    private String readString(final String path) throws IOException {
        final ZipFile zipFile = new ZipFile(path);
        final Enumeration<? extends ZipEntry> entries = zipFile.entries();

        while(entries.hasMoreElements()){
            ZipEntry entry = entries.nextElement();
            if (entry.getName().equals("a.txt")) {
                final StringWriter writer = new StringWriter();
                IOUtils.copy(zipFile.getInputStream(entry), writer, "UTF-8");
                final String s = writer.toString();
                return s;
            }
        }

        return " ";
    }

}