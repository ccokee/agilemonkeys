package com.agilemonkeys.repository;

import com.agilemonkeys.exception.FileStorageException;

/**
 * File Storage Repository.
 */
public interface FileStorageRepository {

    /**
     * Uploads new file to File Storage.
     * @param file byte array
     * @param fileName under which the file will be saved.
     * @return Url download link.
     * @throws FileStorageException upon Failure
     */
    String uploadFile(byte[] file, String fileName);

    /**
     * Deletes file from File Storage.
     * @param fileName file defined by its file name.
     * @return TRUE if files was deleted and FALSE if file was not found.
     * @throws FileStorageException upon Failure
     */
    boolean deleteFile(String fileName);
}
