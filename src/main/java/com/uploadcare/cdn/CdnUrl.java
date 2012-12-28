package com.uploadcare.cdn;

import com.google.api.client.http.GenericUrl;

public class CdnUrl extends GenericUrl {

    CdnUrl(String encodedUrl) {
      super(encodedUrl);
    }

}
