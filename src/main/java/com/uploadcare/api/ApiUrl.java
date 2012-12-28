package com.uploadcare.api;

import com.google.api.client.http.GenericUrl;

public class ApiUrl extends GenericUrl {

    public static final String API_ROOT = "https://api.uploadcare.com";

    private ApiUrl(String encodedUrl) {
      super(encodedUrl);
    }

    public static ApiUrl filesUrl(String fileId) {
        return filesUrl(fileId, false);
    }

    public static ApiUrl filesUrl(String fileId, boolean storage) {
        StringBuilder sb = new StringBuilder()
                .append(API_ROOT)
                .append("/files/")
                .append(fileId);
        if (storage) {
            sb.append("/storage/");
        } else {
            sb.append("/");
        }
        return new ApiUrl(sb.toString());
    }

}
