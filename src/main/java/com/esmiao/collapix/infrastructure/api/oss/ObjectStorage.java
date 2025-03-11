package com.esmiao.collapix.infrastructure.api.oss;

import java.io.File;

/**
 * An object storage template class
 * @author Steven Chen
 */
public interface ObjectStorage<R> {

    R putObject(String key, File file);

    /**
     * Delete object
     *
     * @param key File key
     */
     void deleteObject(String key);

     /**
      * Generate a pre-signed url for temporary access
      * */
     String generatePresignedUrl(String key);

     String convertUrlToKey(String url);
}
