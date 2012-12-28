package com.uploadcare.api;

import com.google.api.client.util.Key;

public class FileData {

    @Key("file_id")
    public String fileId;

    @Key("last_keep_claim")
    public String lastKeepClaim;

    @Key("made_public")
    public boolean madePublic;

    @Key("mime_type")
    public String mimeType;

    @Key("on_s3")
    public boolean onS3;

    @Key("original_file_url")
    public String originalFileUrl;

    @Key("original_filename")
    public String originalFilename;

    @Key("removed")
    public String removed;

    @Key("size")
    public Integer size;

    @Key("upload_date")
    public String uploadDate;

    @Key("url")
    public String url;

}
