package com.uploadcare.integration;

import com.uploadcare.api.Client;
import com.uploadcare.api.File;
import com.uploadcare.upload.UploadFailureException;
import com.uploadcare.upload.UrlUploader;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UploadTest {

    @Test
    public void test_upload() throws UploadFailureException {
        Client client = Client.demoClient();
        UrlUploader uploader = new UrlUploader(client, "https://ucarecdn.com/assets/images/olympia.0939bbb3e820.jpg");
        File file = uploader.upload();
        assertEquals("olympia.0939bbb3e820.jpg", file.getOriginalFilename());
    }

}
