package com.uploadcare.upload;

import com.uploadcare.api.Client;
import com.uploadcare.api.File;
import com.uploadcare.api.RequestHelper;
import com.uploadcare.api.RequestHelperProvider;
import com.uploadcare.data.FileData;
import com.uploadcare.data.UploadBaseData;
import com.uploadcare.urls.Urls;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.HttpUriRequest;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Test;
import org.mockito.InOrder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class FileUploaderTest {

    public static final String FILE_ID = "unique_file_id";

    @Test
    public void test_upload() throws UploadFailureException, IOException {
        final RequestHelper requestHelper = mock(RequestHelper.class);

        when(requestHelper.executeQuery(requestThat(Urls.uploadBase()), eq(false), eq(UploadBaseData.class)))
                .thenReturn(uploadBaseData());

        Client client = new Client("public", "private", true, new RequestHelperProvider() {
            @Override
            public RequestHelper get(Client client) {
                return requestHelper;
            }
        });

        String filename = "olympia.jpg";
        InputStream is = getClass().getClassLoader().getResourceAsStream(filename);
        byte[] bytes = IOUtils.toByteArray(is);

        Uploader uploader = new FileUploader(client, bytes, filename);
        File file = uploader.upload();

        InOrder inOrder = inOrder(requestHelper);
        inOrder.verify(requestHelper).executeQuery(requestThat(Urls.uploadBase()), eq(false), eq(UploadBaseData.class));
        inOrder.verify(requestHelper).executeQuery(requestThat(Urls.apiFile(FILE_ID)), eq(true), eq(FileData.class));
    }

    private HttpUriRequest requestThat(final URI uri) {
        return argThat(new BaseMatcher<HttpUriRequest>() {
            @Override
            public boolean matches(Object o) {
                HttpUriRequest request = (HttpUriRequest) o;
                return request.getURI().equals(uri);
            }

            @Override
            public void describeTo(Description description) {
            }
        });
    }

    private UploadBaseData uploadBaseData() {
        UploadBaseData baseData = new UploadBaseData();
        baseData.file = FILE_ID;
        return baseData;
    }

}
