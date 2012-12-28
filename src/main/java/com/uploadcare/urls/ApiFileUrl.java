package com.uploadcare.urls;

public class ApiFileUrl extends ApiUrl {

    public ApiFileUrl(String fileId) {
      super();
      setRawPath("/files/" + fileId + "/");
    }

}
