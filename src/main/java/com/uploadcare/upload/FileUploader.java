package com.uploadcare.upload;

import com.uploadcare.api.Client;
import com.uploadcare.api.File;
import com.uploadcare.data.UploadBaseData;
import com.uploadcare.urls.Urls;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import java.net.URI;

/**
 * Uploadcare uploader for files and binary data.
 */
public class FileUploader implements Uploader {

    private final Client client;

    private final java.io.File file;

    private final byte[] bytes;

    private final String filename;

    private String store = "auto";

    /**
     * Creates a new uploader from a file on disk
     * (not to be confused with a file resource from Uploadcare API).
     *
     * @param client Uploadcare client
     * @param file   File on disk
     */
    public FileUploader(Client client, java.io.File file) {
        this.client = client;
        this.file = file;
        this.bytes = null;
        this.filename = null;
    }

    /**
     * Creates a new uploader from binary data.
     *
     * @param client   Uploadcare client
     * @param bytes    File contents as binary data
     * @param filename Original filename
     */
    public FileUploader(Client client, byte[] bytes, String filename) {
        this.client = client;
        this.file = null;
        this.bytes = bytes;
        this.filename = filename;
    }

    /**
     * Synchronously uploads the file to Uploadcare.
     *
     * The calling thread will be busy until the upload is finished.
     *
     * @return An Uploadcare file
     */
    public File upload() throws UploadFailureException {
        URI uploadUrl = Urls.uploadBase();
        HttpPost request = new HttpPost(uploadUrl);

        MultipartEntity entity = new MultipartEntity();
        StringBody pubKeyBody = StringBody.create(client.getPublicKey(), "text/plain", null);
        StringBody storeBody = StringBody.create(store, "text/plain", null);
        entity.addPart("UPLOADCARE_PUB_KEY", pubKeyBody);
        entity.addPart("UPLOADCARE_STORE", storeBody);
        if (file != null) {
            entity.addPart("file", new FileBody(file));
        } else {
            entity.addPart("file", new ByteArrayBody(bytes, filename));
        }
        request.setEntity(entity);

        String fileId = client.getRequestHelper()
                .executeQuery(request, false, UploadBaseData.class).file;
        return client.getFile(fileId);
    }

    /**
     * Store the file upon uploading.
     *
     * @param store is set true - store the file upon uploading. Requires “automatic file storing”
     *              setting to be enabled.
     *              is set false - do not store file upon uploading.
     */
    public FileUploader store(boolean store) {
        this.store = store ? String.valueOf(1) : String.valueOf(0);
        return this;
    }
}
