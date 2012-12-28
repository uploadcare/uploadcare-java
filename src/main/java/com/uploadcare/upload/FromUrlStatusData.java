package com.uploadcare.upload;

import com.google.api.client.util.Key;

public class FromUrlStatusData {

    @Key
    public String status;

    @Key("file_id")
    public String fileId;

}
