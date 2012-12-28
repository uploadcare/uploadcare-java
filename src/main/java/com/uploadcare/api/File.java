package com.uploadcare.api;

import com.google.api.client.util.Data;
import com.uploadcare.cdn.CdnUrlBuilder;

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
        return !Data.isNull(fileData.lastKeepClaim);
    }

    public String getLastKeepClaim() {
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
        return !Data.isNull(fileData.originalFileUrl);
    }

    public String getOriginalFileUrl() {
        return fileData.originalFileUrl;
    }

    public String getOriginalFilename() {
        return fileData.originalFilename;
    }

    public boolean isRemoved() {
        return !Data.isNull(fileData.removed);
    }

    public String getRemoved() {
        return fileData.removed;
    }

    public Integer getSize() {
        return fileData.size;
    }

    public String getUploadDate() {
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
    }

    public CdnUrlBuilder cdnUrl() {
        return new CdnUrlBuilder(this);
    }

}
