package com.uploadcare.api;

import com.uploadcare.urls.CdnPathBuilder;
import com.uploadcare.data.FileData;

import java.util.Date;

public class File {

    private Client client;
    private FileData fileData;

    File(Client client, FileData fileData) {
        this.client = client;
        this.fileData = fileData;
    }

    public String getFileId() {
        return fileData.fileId;
    }

    public boolean isStored() {
        return fileData.lastKeepClaim != null;
    }

    public Date getLastKeepClaim() {
        return fileData.lastKeepClaim;
    }

    public boolean isMadePublic() {
        return fileData.madePublic;
    }

    public String getMimeType() {
        return fileData.mimeType;
    }

    public boolean isOnS3() {
        return fileData.onS3;
    }

    public boolean hasOriginalFileUrl() {
        return fileData.originalFileUrl != null;
    }

    public String getOriginalFileUrl() {
        return fileData.originalFileUrl;
    }

    public String getOriginalFilename() {
        return fileData.originalFilename;
    }

    public boolean isRemoved() {
        return fileData.removed != null;
    }

    public Date getRemoved() {
        return fileData.removed;
    }

    public int getSize() {
        return fileData.size;
    }

    public Date getUploadDate() {
        return fileData.uploadDate;
    }

    public String getUrl() {
        return fileData.url;
    }

    public void delete() {
        client.deleteFile(fileData.fileId);
    }

    public void save() {
        client.saveFile(fileData.fileId);
        fileData = client.getFile(fileData.fileId).fileData;
    }

    public CdnPathBuilder cdnUrl() {
        return new CdnPathBuilder(this);
    }

}
