package com.uploadcare.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.uploadcare.data.CopyFileData;
import com.uploadcare.data.FileData;
import com.uploadcare.exceptions.UploadcareApiException;

import java.io.IOException;
import java.net.URI;

/**
 * The main Uploadcare resource, represents a result of file Copy operation.
 */
public class CopyFile {

    private final Client client;
    private final CopyFileData copyFileData;

    CopyFile(Client client, CopyFileData copyFileData) {
        this.client = client;
        this.copyFileData = copyFileData;
    }

    public CopyFileType getType() {
        return copyFileData.type;
    }

    public File getFile() {
        if (getType() != CopyFileType.FILE) {
            return null;
        }

        try {
            FileData fileData = client.getObjectMapper().readValue(copyFileData.result, FileData.class);
            return new File(client, fileData);
        } catch (IOException e) {
            throw new UploadcareApiException("Error parsing copy result File", e);
        }
    }

    public URI getUri() {
        if (getType() != CopyFileType.URL) {
            return null;
        }

        try {
            return client.getObjectMapper().readValue(copyFileData.result, URI.class);
        } catch (IOException e) {
            throw new UploadcareApiException("Error parsing copy result URI", e);
        }
    }

    @Override
    public String toString() {
        return "CopyFile{" +
                "copyFileData=" + copyFileData +
                '}';
    }

    public enum CopyFileType {
        @JsonProperty("url")
        URL,
        @JsonProperty("file")
        FILE
    }
}
