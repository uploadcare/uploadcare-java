package com.uploadcare.upload;

import com.uploadcare.api.Client;
import com.uploadcare.api.File;
import com.uploadcare.data.UploadFromUrlData;
import com.uploadcare.data.UploadFromUrlStatusData;
import com.uploadcare.urls.Urls;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;

public class UrlUploader {

    private final Client client;
    private String sourceUrl;

    public UrlUploader(Client client, String sourceUrl) {
        this.client = client;
        this.sourceUrl = sourceUrl;
    }

    public File upload() throws UploadFailureException {
        return upload(500);
    }

    public File upload(int pollingInterval) throws UploadFailureException {
        URI uploadUrl = Urls.uploadFromUrl(sourceUrl, client.getPublicKey());
        String token = request(new HttpGet(uploadUrl), UploadFromUrlData.class).token;
        URI statusUrl = Urls.uploadFromUrlStatus(token);
        while (true) {
            sleep(pollingInterval);
            UploadFromUrlStatusData data = request(new HttpGet(statusUrl), UploadFromUrlStatusData.class);
            if (data.status.equals("success")) {
                return client.getFile(data.fileId);
            } else if (data.status.equals("error") || data.status.equals("failed")) {
                throw new UploadFailureException();
            }
        }
    }

    private <T> T request(HttpUriRequest request, Class<T> dataClass) {
        try {
            HttpResponse response = client.getHttpClient().execute(request);
            HttpEntity entity = response.getEntity();
            String data = EntityUtils.toString(entity);
            return client.getObjectMapper().readValue(data, dataClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
