package com.uploadcare.integration;

import com.uploadcare.api.Client;
import com.uploadcare.api.File;
import com.uploadcare.upload.FileUploader;
import com.uploadcare.upload.UploadFailureException;
import com.uploadcare.upload.Uploader;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

public class UploadTest {

    @Test
    public void test_uploadFile() throws UploadFailureException, IOException {
        String filename = "olympia.jpg";
        InputStream is = getClass().getClassLoader().getResourceAsStream(filename);
        byte[] bytes = IOUtils.toByteArray(is);

        Client client = Client.demoClient();
        Uploader uploader = new FileUploader(client, bytes, filename);
        File file = uploader.upload();
        assertEquals(filename, file.getOriginalFilename());
    }

}
