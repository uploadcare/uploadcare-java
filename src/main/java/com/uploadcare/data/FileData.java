package com.uploadcare.data;

import com.uploadcare.api.File;

import java.net.URI;
import java.util.Date;
import java.util.Map;

public class FileData {

    public String uuid;
    public URI url;
    public long size;
    public String source;
    public boolean isReady;
    public boolean isImage;
    public String mimeType;
    public String originalFilename;
    public URI originalFileUrl;
    public Date datetimeUploaded;
    public Date datetimeStored;
    public Date datetimeRemoved;
    public File.ImageInfo imageInfo;
    public File.VideoInfo videoInfo;
    public Map<String, Float> rekognitionInfo;
    public Map<String, String> variations;
    public File.ContentInfo contentInfo;
    public Map<String, String> metadata;
    public Map<String, File.AppData> appdata;

    @Override
    public String toString() {
        return "FileData{" +
                "uuid='" + uuid + '\'' +
                '}';
    }
}
