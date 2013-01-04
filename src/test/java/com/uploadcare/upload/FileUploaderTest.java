package com.uploadcare.upload;

import com.uploadcare.api.Client;
import com.uploadcare.api.File;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

public class FileUploaderTest {

    @Test
    public void test_upload() throws UploadFailureException, IOException {
        String filename = "olympia.jpg";
        InputStream is = getClass().getClassLoader().getResourceAsStream(filename);
        byte[] bytes = IOUtils.toByteArray(is);

        Client client = Client.demoClient();
        Uploader uploader = new FileUploader(client, bytes, filename);
        File file = uploader.upload();
        assertEquals(filename, file.getOriginalFilename());
    }

}
