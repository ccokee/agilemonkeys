package com.agilemonkeys.repository.impl;

import com.agilemonkeys.repository.FileStorageRepository;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Acl;
import java.util.ArrayList;
import java.util.Arrays;

public class GoogleCloudStorage implements FileStorageRepository {

    private Storage storage;
    private String bucketName;

    public GoogleCloudStorage(String projectId, String bucketName) throws Exception{
        GoogleCredentials credentials = GoogleCredentials.fromStream(
                this.getClass().getClassLoader().getResourceAsStream("gcp-credentials.json"));
        this.storage = StorageOptions.newBuilder().setCredentials(credentials)
                .setProjectId(projectId).build().getService();
        this.bucketName = bucketName;
    }


    public String uploadFile(byte[] file, String fileName) {
        BlobInfo blobInfo = storage.create(
            BlobInfo.newBuilder(bucketName, fileName)
                // Modify access list to allow all users with link to read file
                .setAcl(new ArrayList<>(Arrays.asList(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER))))
                .build(),
                file);
        return blobInfo.getMediaLink();
    }

    public boolean deleteFile(String fileName) {
        BlobId blobId = BlobId.of(bucketName, fileName);
        return storage.delete(blobId);
    }
}
