package com.uploadcare.urls;

import com.google.api.client.http.GenericUrl;
import com.uploadcare.Constants;

public class ApiUrl extends GenericUrl {

    public ApiUrl() {
      super(Constants.API_BASE);
    }

}
