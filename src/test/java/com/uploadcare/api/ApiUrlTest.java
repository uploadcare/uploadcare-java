package com.uploadcare.api;

import com.uploadcare.urls.ApiFileStorageUrl;
import com.uploadcare.urls.ApiFileUrl;
import com.uploadcare.urls.ApiUrl;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ApiUrlTest {

    private static final String fileId = "27c7846b-a019-4516-a5e4-de635f822161";

    @Test
    public void test_filesUrl() {
        ApiFileUrl url = new ApiFileUrl(fileId);
        assertEquals("/files/" + fileId + "/", url.getRawPath());
    }

    @Test
    public void test_filesUrlStorage() {
        ApiFileStorageUrl url = new ApiFileStorageUrl(fileId);
        assertEquals("/files/" + fileId + "/storage/", url.getRawPath());
    }
}
