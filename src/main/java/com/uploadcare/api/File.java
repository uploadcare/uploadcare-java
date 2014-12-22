package com.uploadcare.api;

import com.uploadcare.data.FileData;
import com.uploadcare.urls.CdnPathBuilder;

import java.net.URI;
import java.util.Date;

/**
 * The main Uploadcare resource, represents a user-uploaded file.
 */
public class File {

    private final Client client;
    private final FileData fileData;

    File(Client client, FileData fileData) {
        this.client = client;
        this.fileData = fileData;
    }

    public String getFileId() {
        return fileData.uuid;
    }

    public boolean isStored() {
        return fileData.datetimeStored != null;
    }

    public String getMimeType() {
        return fileData.mimeType;
    }

    public boolean hasOriginalFileUrl() {
        return fileData.originalFileUrl != null;
    }

    public URI getOriginalFileUrl() {
        return fileData.originalFileUrl;
    }

    public String getOriginalFilename() {
        return fileData.originalFilename;
    }

    public boolean isRemoved() {
        return fileData.datetimeRemoved != null;
    }

    public Date getRemoved() {
        return fileData.datetimeRemoved;
    }

    public int getSize() {
        return fileData.size;
    }

    public Date getUploadDate() {
        return fileData.datetimeUploaded;
    }

    /**
     * Returns the unique REST URL for this resource.
     *
     * @return REST URL
     */
    public URI getUrl() {
        return fileData.url;
    }

    /**
     * Refreshes file data from Uploadcare.
     *
     * This does not mutate the current {@code File} instance,
     * but creates a new one.
     *
     * @return New file resource instance
     */
    public File update() {
        return client.getFile(fileData.uuid);
    }

    /**
     * Deletes this file from Uploadcare.
     *
     * This does not mutate the current {@code File} instance,
     * but creates a new one.
     *
     * @return New file resource instance
     */
    public File delete() {
        client.deleteFile(fileData.uuid);
        return update();
    }

    /**
     * Saves this file on Uploadcare (marks it to be kept).
     *
     * This does not mutate the current {@code File} instance,
     * but creates a new one.
     *
     * @return New file resource instance
     */
    public File save() {
        client.saveFile(fileData.uuid);
        return update();
    }

    /**
     * Creates a CDN path builder for this file.
     *
     * @return CDN path builder
     *
     * @see com.uploadcare.urls.Urls#cdn(CdnPathBuilder)
     */
    public CdnPathBuilder cdnPath() {
        return new CdnPathBuilder(this);
    }
}
