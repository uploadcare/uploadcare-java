package com.uploadcare.urls;

public class ApiFileStorageUrl extends ApiUrl {

    public ApiFileStorageUrl(String fileId) {
      super();
      setRawPath("/files/" + fileId + "/storage/");
    }

}
