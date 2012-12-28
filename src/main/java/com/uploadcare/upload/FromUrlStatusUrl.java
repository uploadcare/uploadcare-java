package com.uploadcare.upload;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.util.Key;

class FromUrlStatusUrl extends GenericUrl {

    @Key
    private String token;

    public FromUrlStatusUrl(String token) {
        super("https://upload.uploadcare.com/from_url/status/");
        this.token = token;
    }

}
