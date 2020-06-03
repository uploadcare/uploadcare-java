package com.uploadcare.upload;

import com.uploadcare.api.Client;
import com.uploadcare.api.File;
import com.uploadcare.data.UploadBaseData;
import com.uploadcare.urls.Urls;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.util.TextUtils;

import java.io.InputStream;
import java.net.URI;

/**
 * Uploadcare uploader for files and binary data.
 */
public class FileUploader implements Uploader {

    private final Client client;

    private final java.io.File file;

    private final InputStream stream;

    private final byte[] bytes;

    private final String filename;

    private String store = "auto";

    private String signature = null;

    private String expire = null;

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
        this.stream = null;
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
        this.stream = null;
        this.bytes = bytes;
        this.filename = filename;
    }

    public FileUploader(Client client, InputStream stream, String filename) {
        this.client = client;
        this.file = null;
        this.stream = stream;
        this.bytes = null;
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

        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        entityBuilder.addTextBody("UPLOADCARE_PUB_KEY", client.getPublicKey());
        entityBuilder.addTextBody("UPLOADCARE_STORE", store);

        if (!TextUtils.isEmpty(signature) && !TextUtils.isEmpty(expire)) {
            entityBuilder.addTextBody("signature", signature);
            entityBuilder.addTextBody("expire", expire);
        }

        if (file != null) {
            entityBuilder.addPart("file", new FileBody(file));
        } else if (stream != null) {
            entityBuilder.addBinaryBody("file", stream);
        } else {
            entityBuilder.addPart("file", new ByteArrayBody(bytes, filename));
        }
        request.setEntity(entityBuilder.build());

        String fileId = client.getRequestHelper()
                .executeQuery(request, false, UploadBaseData.class).file;
        if (client.getSecretKey() != null) {
            // If Client have "secretkey", we use Rest API to get full file info.
            return client.getFile(fileId);
        } else {
            // If Client doesn't have "secretkey" info about file might not have all info.
            return client.getUploadedFile(fileId);
        }
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

    /**
     * Signed Upload - let you control who and when can upload files to a specified Uploadcare
     * project.
     *
     * @param signature is a string sent along with your upload request. It requires your Uploadcare
     *                  project secret key and hence should be crafted on your back end.
     * @param expire    sets the time until your signature is valid. It is a Unix time.(ex 1454902434)
     */
    public FileUploader signedUpload(String signature, String expire) {
        this.signature = signature;
        this.expire = expire;
        return this;
    }
}
