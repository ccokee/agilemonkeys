package com.agilemonkeys.repository;

import com.agilemonkeys.exception.FileStorageException;
import com.agilemonkeys.repository.impl.GoogleCloudStorage;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.StorageException;
import org.junit.Test;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.eq;

/**
 * Unit tests for {@link GoogleCloudStorage} implementation of {@link FileStorageRepository}.
 */
public class GoogleCloudStorageTest {

    private static final String bucketName = "crm";

    private Storage storage = mock(Storage.class);
    private Blob blob = mock(Blob.class);

    private GoogleCloudStorage googleCloudStorage = new GoogleCloudStorage(storage, bucketName);

    @Test
    public void testUploadFile() {
        String string = "test";
        byte[] bytes = string.getBytes();

        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, "test.txt")
                // Modify access list to allow all users with link to read file
                .setAcl(new ArrayList<>(Arrays.asList(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER))))
                .build();
        when(blob.getMediaLink()).thenReturn("www.google.com/test.png");
        when(storage.create(blobInfo, bytes)).thenReturn(blob);

        googleCloudStorage.uploadFile(bytes,"test.txt");
        verify(storage, times(1)).create(any(), eq (bytes));
    }

    @Test(expected = FileStorageException.class)
    public void testUploadFileWithException() {
        when(storage.create(any(BlobInfo.class), any())).thenThrow(new StorageException(new IOException()));
        googleCloudStorage.uploadFile(new byte[1], "test.png");
    }

    @Test
    public void testDeleteFile() {
        when(storage.delete(any(BlobId.class))).thenReturn(true);
        googleCloudStorage.deleteFile("test.txt");
        verify(storage, times(1)).delete(any(BlobId.class));
    }

    @Test(expected = FileStorageException.class)
    public void testDeleteFileWithException() {
        when(storage.delete(any(BlobId.class))).thenThrow(new StorageException(new IOException()));
        googleCloudStorage.deleteFile("test.txt");
    }
}
