package com.uploadcare.data;

import java.net.URI;
import java.util.Date;

public class FileData {
//    public String fileId; // version 0.2
//    public Date lastKeepClaim; // version 0.2
//    public boolean madePublic; // version 0.2
//    public boolean onS3; // version 0.2
//    public Date removed; // version 0.2
//    public Date uploadDate; // version 0.2
    
    public URI originalFileUrl;
    public String mimeType;
    public boolean isReady;
    public URI url;
    public String uuid;
    public String originalFilename;
    public Date datetimeUploaded;
    public int size;
    public boolean isImage;
    public Date datetimeStored;
    public Date datetimeRemoved;
    

}
