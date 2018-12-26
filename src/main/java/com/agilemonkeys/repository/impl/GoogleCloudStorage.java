package com.agilemonkeys.repository.impl;

import com.agilemonkeys.exception.FileStorageException;
import com.agilemonkeys.repository.FileStorageRepository;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.BlobId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.Arrays;

@Getter
@Setter
@AllArgsConstructor
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

    @Override
    public String uploadFile(byte[] file, String fileName) throws FileStorageException {
        try {
            BlobInfo blobInfo = storage.create(
                    BlobInfo.newBuilder(bucketName, fileName)
                            // Modify access list to allow all users with link to read file
                            .setAcl(new ArrayList<>(Arrays.asList(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER))))
                            .build(),
                    file);
            return blobInfo.getMediaLink();
        }catch (Exception e) {
            throw new FileStorageException(e);
        }
    }

    @Override
    public boolean deleteFile(String fileName) {
        try{
            BlobId blobId = BlobId.of(bucketName, fileName);
            return storage.delete(blobId);
        } catch (Exception e){
            throw new FileStorageException(e);
        }
    }
}
