package com.uploadcare.data;

import java.net.URI;
import java.util.Date;

public class FileData {

    public String fileId;
    public Date lastKeepClaim;
    public boolean madePublic;
    public String mimeType;
    public boolean onS3;
    public URI originalFileUrl;
    public String originalFilename;
    public Date removed;
    public int size;
    public Date uploadDate;
    public URI url;

}
