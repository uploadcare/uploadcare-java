package com.uploadcare.data;

import com.google.api.client.util.Key;

public class UploadFromUrlStatusData {

    @Key
    public String status;

    @Key("file_id")
    public String fileId;

}
