package com.uploadcare.urls;

import com.google.api.client.http.GenericUrl;
import com.uploadcare.Constants;

public class CdnUrl extends GenericUrl {

    public CdnUrl() {
      super(Constants.CDN_BASE);
    }

}
