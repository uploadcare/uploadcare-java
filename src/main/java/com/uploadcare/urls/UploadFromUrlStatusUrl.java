package com.uploadcare.urls;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.util.Key;
import com.uploadcare.Constants;

public class UploadFromUrlStatusUrl extends GenericUrl {

    @Key
    private String token;

    public UploadFromUrlStatusUrl(String token) {
        super(Constants.UPLOAD_BASE + "from_url/status/");
        this.token = token;
    }

}
