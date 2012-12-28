package com.uploadcare.api;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ApiUrlTest {

    private static final String fileId = "27c7846b-a019-4516-a5e4-de635f822161";

    @Test
    public void test_filesUrl() {
        ApiUrl url = ApiUrl.filesUrl(fileId);
        assertEquals("/files/" + fileId + "/", url.getRawPath());
    }

    @Test
    public void test_filesUrlStorage() {
        ApiUrl url = ApiUrl.filesUrl(fileId, true);
        assertEquals("/files/" + fileId + "/storage/", url.getRawPath());
    }
}
