package com.agilemonkeys.repository;

import com.agilemonkeys.repository.impl.GoogleCloudStorage;
import org.junit.Before;
import org.junit.Test;
import java.io.File;
import java.nio.file.Files;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Integration tests for {@link GoogleCloudStorage} implementation of {@link FileStorageRepository}.
 */
public class GoogleCloudStorageIntegrationTest {

    private static final String projectId = "agile-monkeys-test";
    private static final String bucketName = "crm-photos";

    private GoogleCloudStorage repository;

    @Before
    public void setUp() throws Exception {
        repository = new GoogleCloudStorage(projectId, bucketName);
    }

    @Test
    public void uploadFileTestAndDelete() throws Exception {
        File file = new File(getClass().getClassLoader().getResource("image.png").getFile());
        byte[] fileContent = Files.readAllBytes(file.toPath());

        String fileUrl = repository.uploadFile(fileContent, "test.png");
        assertNotNull(fileUrl);
        repository.deleteFile("test.png");
    }

    @Test
    public void uploadExistingFileAndDelete() throws Exception {
        File file = new File(getClass().getClassLoader().getResource("image.png").getFile());
        byte[] fileContent = Files.readAllBytes(file.toPath());

        String fileUrl = repository.uploadFile(fileContent, "test.png");
        assertNotNull(fileUrl);
        String fileUrl2 = repository.uploadFile(fileContent, "test.png");
        assertNotNull(fileUrl2);

        repository.deleteFile("test.png");
    }

    @Test
    public void deleteNotFoundFile() {
        Boolean deleted = repository.deleteFile("test2.png");
        assertEquals(FALSE, deleted);
    }

}
