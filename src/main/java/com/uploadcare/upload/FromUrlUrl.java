package com.uploadcare.upload;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.util.Key;

class FromUrlUrl extends GenericUrl {

    @Key("source_url")
    private String sourceUrl;

    @Key("pub_key")
    private String pubKey;

    public FromUrlUrl(String sourceUrl, String pubKey) {
        super("https://upload.uploadcare.com/from_url/");
        this.sourceUrl = sourceUrl;
        this.pubKey = pubKey;
    }

}
