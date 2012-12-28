package com.uploadcare.urls;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.util.Key;
import com.uploadcare.Constants;

public class UploadFromUrlUrl extends GenericUrl {

    @Key("source_url")
    private String sourceUrl;

    @Key("pub_key")
    private String pubKey;

    public UploadFromUrlUrl(String sourceUrl, String pubKey) {
        super(Constants.UPLOAD_BASE + "from_url/");
        this.sourceUrl = sourceUrl;
        this.pubKey = pubKey;
    }

}
