package com.uploadcare.upload;

import com.uploadcare.api.Client;
import com.uploadcare.api.File;
import com.uploadcare.data.DataUtils;
import com.uploadcare.data.UploadBaseData;
import com.uploadcare.urls.Urls;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import java.net.URI;

public class FileUploader implements Uploader {

    private final Client client;
    private final java.io.File file;
    private byte[] bytes;
    private final String filename;

    public FileUploader(Client client, java.io.File file) {
        this.client = client;
        this.file = file;
        this.bytes = null;
        this.filename = null;
    }

    public FileUploader(Client client, byte[] bytes, String filename) {
        this.client = client;
        this.file = null;
        this.bytes = bytes;
        this.filename = filename;
    }

    @Override
    public File upload() throws UploadFailureException {
        URI uploadUrl = Urls.uploadBase();
        HttpPost request = new HttpPost(uploadUrl);

        MultipartEntity entity = new MultipartEntity();
        StringBody pubKeyBody = StringBody.create(client.getPublicKey(), "text/plain", null);
        entity.addPart("UPLOADCARE_PUB_KEY", pubKeyBody);
        if (file != null) {
            entity.addPart("file", new FileBody(file));
        } else {
            entity.addPart("file", new ByteArrayBody(bytes, filename));
        }
        request.setEntity(entity);

        String fileId = DataUtils.executeQuery(client, request, UploadBaseData.class).file;
        return client.getFile(fileId);
    }
}
