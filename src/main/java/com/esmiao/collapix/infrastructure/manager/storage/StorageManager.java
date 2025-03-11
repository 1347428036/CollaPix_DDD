package com.esmiao.collapix.infrastructure.manager.storage;

/**
 * Storage base operation definition
 * @author Steven Chen
 */
public interface StorageManager<R> {

    /**
     * Upload file to storage
     *
     * @param inputSource file to upload
     * @param pathPrefix  The file uploading path prefix
     */
    R putObject(Object inputSource, String pathPrefix);
}
