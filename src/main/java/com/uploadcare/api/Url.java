package com.uploadcare.api;

import com.google.api.client.http.GenericUrl;

public class Url extends GenericUrl {

    public static final String API_ROOT = "https://api.uploadcare.com";

    private Url(String encodedUrl) {
      super(encodedUrl);
    }

    public static Url filesUrl(String fileId) {
        return new Url(API_ROOT + "/files/" + fileId + "/");
    }

}
