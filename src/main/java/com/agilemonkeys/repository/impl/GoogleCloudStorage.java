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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.Arrays;

@Getter
@Setter
@AllArgsConstructor
public class GoogleCloudStorage implements FileStorageRepository {

    private static final Logger log = LoggerFactory.getLogger(GoogleCloudStorage.class);

    private Storage storage;
    private String bucketName;

    public GoogleCloudStorage(String bucketName) throws Exception{
        this.storage = StorageOptions.newBuilder().build().getService();
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
            log.info("File {} uploaded.", fileName);
            return blobInfo.getMediaLink();
        }catch (Exception e) {
            throw new FileStorageException(e);
        }
    }

    @Override
    public boolean deleteFile(String fileName) {
        try{
            BlobId blobId = BlobId.of(bucketName, fileName);
            boolean deleted = storage.delete(blobId);
            if (deleted){
                log.info("File {} deleted.", fileName);
            }
            else {
                log.info("File {} not found. Can't be deleted.", fileName);
            }
            return deleted;
        } catch (Exception e){
            throw new FileStorageException(e);
        }
    }
}
