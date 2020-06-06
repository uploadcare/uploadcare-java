package com.uploadcare.upload;

import com.uploadcare.api.Client;
import com.uploadcare.api.File;
import com.uploadcare.api.RequestHelper;
import com.uploadcare.data.UploadBaseData;
import com.uploadcare.data.UploadMultipartCompleteData;
import com.uploadcare.data.UploadMultipartStartData;
import com.uploadcare.urls.Urls;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.util.TextUtils;

import java.io.*;
import java.net.URI;
import java.util.Arrays;

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

    private java.io.File temporaryFile = null;

    private static final long MIN_MULTIPART_SIZE = 10485760; //~10.5 mb

    private static final int CHUNK_SIZE = 5242880;
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

    /**
     * Creates a new uploader from InputStream.
     *
     * Uploading InputStream (as InputStreamBody or as BinaryBody) is not supported because Upload API API needs to know
     * content size (ContentLength) info, which is unknown (-1L) for input stream. We save input stream content first to
     * temporary file, to be able to upload content.
     *
     * After upload is complete/canceled/error temporary file will be deleted.
     *
     * @param client   Uploadcare client
     * @param stream   InputStream
     * @param filename Original filename
     */
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
        String name;
        long size;
        String contentType;

        if (file != null) {
            name = file.getName();
            size = file.length();
            contentType = RequestHelper.getMimeType(name);
        } else if (stream != null && filename != null) {
            try {
                temporaryFile = generateTemporaryFile(stream, filename);
            } catch (IOException e) {
                throw new UploadFailureException(e);
            }

            name = filename;
            size = temporaryFile.length();
            contentType = RequestHelper.getMimeType(name);
        } else if (bytes != null && filename != null) {
            name = filename;
            size = bytes.length;
            contentType = RequestHelper.getMimeType(name);
        } else {
            throw new UploadFailureException(new IllegalArgumentException());
        }

        if (size > MIN_MULTIPART_SIZE) {
            // We can use multipart upload
            return multipartUpload(name, size, contentType);
        } else {
            // We can use only direct upload
            return directUpload(name, contentType);
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

    private File directUpload(String name, String contentType) throws UploadFailureException {
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
            entityBuilder.addPart("file", new FileBody(file, ContentType.create(contentType), name));
        } else if (temporaryFile != null) {
            entityBuilder.addPart("file", new FileBody(temporaryFile, ContentType.create(contentType), name));
        } else if (bytes != null) {
            entityBuilder.addPart("file", new ByteArrayBody(bytes, ContentType.create(contentType), name));
        }

        request.setEntity(entityBuilder.build());

        String fileId = null;
        try {
            fileId = client.getRequestHelper()
                    .executeQuery(request, false, UploadBaseData.class).file;
        } finally {
            //Clean up. Delete temporary file if exist for InputStream upload.
            if (temporaryFile != null) {
                temporaryFile.delete();
            }
        }

        if (client.getSecretKey() != null) {
            // If Client have "secretkey", we use Rest API to get full file info.
            return client.getFile(fileId);
        } else {
            // If Client doesn't have "secretkey" info about file might not have all info.
            return client.getUploadedFile(fileId);
        }
    }

    private File multipartUpload(String name, long size, String contentType) throws UploadFailureException {
        // start multipart upload
        UploadMultipartStartData multipartData = startMultipartUpload(name, size, contentType);

        // upload parts
        try {
            uploadParts(multipartData, name, contentType);
        } catch (IOException e) {
            throw new UploadFailureException(e);
        } finally {
            //Clean up. Delete temporary file if exist for InputStream upload.
            if (temporaryFile != null) {
                temporaryFile.delete();
            }
        }

        //complete upload
        UploadMultipartCompleteData multipartComplete = completeMultipartUpload(multipartData.uuid);

        //fetch file info
        if (client.getSecretKey() != null) {
            // If Client have "secretkey", we use Rest API to get full file info.
            return client.getFile(multipartComplete.uuid);
        } else {
            // If Client doesn't have "secretkey" info about file might not have all info.
            return client.getUploadedFile(multipartComplete.uuid);
        }
    }

    private UploadMultipartStartData startMultipartUpload(String name, long size, String contentType) {
        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        entityBuilder.addTextBody("UPLOADCARE_PUB_KEY", client.getPublicKey());
        entityBuilder.addTextBody("UPLOADCARE_STORE", store);

        if (!TextUtils.isEmpty(signature) && !TextUtils.isEmpty(expire)) {
            entityBuilder.addTextBody("signature", signature);
            entityBuilder.addTextBody("expire", expire);
        }

        entityBuilder.addTextBody("filename", name);
        entityBuilder.addTextBody("size", String.valueOf(size));
        entityBuilder.addTextBody("content_type", contentType);

        URI uploadUrl = Urls.uploadMultipartStart();
        HttpPost request = new HttpPost(uploadUrl);
        request.setEntity(entityBuilder.build());

        return client.getRequestHelper().executeQuery(request, false, UploadMultipartStartData.class);
    }

    private UploadMultipartCompleteData completeMultipartUpload(String uuid) {
        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        entityBuilder.addTextBody("UPLOADCARE_PUB_KEY", client.getPublicKey());
        entityBuilder.addTextBody("uuid", uuid);

        URI uploadUrl = Urls.uploadMultipartComplete();
        HttpPost request = new HttpPost(uploadUrl);
        request.setEntity(entityBuilder.build());

        return client.getRequestHelper().executeQuery(request, false, UploadMultipartCompleteData.class);
    }

    private void partRequest(String partUrl, byte[] bytes, String contentType) {
        URI uploadUrl = Urls.uploadMultipartPart(partUrl);
        HttpPut request = new HttpPut(uploadUrl);
        EntityBuilder entityBuilder = EntityBuilder.create();
        entityBuilder.setBinary(bytes);
        entityBuilder.setContentType(ContentType.create(contentType));
        request.setEntity(entityBuilder.build());
        client.getRequestHelper().executeCommand(request, false);
    }

    private void uploadParts(UploadMultipartStartData multipartData, String name, String contentType)
            throws IOException {
        InputStream is = null;
        if (file != null) {
            is = new FileInputStream(file);
        } else if (temporaryFile != null) {
            is = new FileInputStream(temporaryFile);
        } else if (bytes != null) {
            is = new ByteArrayInputStream(bytes);
        }

        if (is == null) {
            throw new IOException();
        }

        byte[] buffer = new byte[CHUNK_SIZE];
        int i = 0;
        int read = 0;
        while ((read = is.read(buffer)) > 0) {
            byte[] bytes = Arrays.copyOf(buffer, read);
            String partUrl = multipartData.parts.get(i);
            //Upload request
            partRequest(partUrl, bytes, contentType);
            i += 1;
        }

        is.close();
    }

    private java.io.File generateTemporaryFile(InputStream inputStream, String name) throws IOException {
        java.io.File tempFile = java.io.File.createTempFile("temp_upload_file", null);
        FileUtils.copyInputStreamToFile(inputStream, tempFile);
        return tempFile;
    }
}
