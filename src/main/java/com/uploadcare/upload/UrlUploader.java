package com.uploadcare.upload;

import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.uploadcare.api.Client;
import com.uploadcare.api.File;
import com.uploadcare.data.UploadFromUrlData;
import com.uploadcare.data.UploadFromUrlStatusData;
import com.uploadcare.urls.UploadFromUrlStatusUrl;
import com.uploadcare.urls.UploadFromUrlUrl;

import java.io.IOException;

public class UrlUploader {

    private final Client client;
    private String sourceUrl;

    private static final HttpRequestFactory requestFactory = new NetHttpTransport()
            .createRequestFactory(new HttpRequestInitializer() {
                @Override
                public void initialize(HttpRequest httpRequest) throws IOException {
                    httpRequest.setParser(new JsonObjectParser(new JacksonFactory()));
                }
            });

    public UrlUploader(Client client, String sourceUrl) {
        this.client = client;
        this.sourceUrl = sourceUrl;
    }

    public File upload() throws UploadFailureException {
        return upload(500);
    }

    public File upload(int pollingInterval) throws UploadFailureException {
        UploadFromUrlUrl uploadUrl = new UploadFromUrlUrl(sourceUrl, client.getPublicKey());
        String token = request(HttpMethods.GET, uploadUrl, UploadFromUrlData.class).token;
        UploadFromUrlStatusUrl statusUrl = new UploadFromUrlStatusUrl(token);
        while (true) {
            sleep(pollingInterval);
            UploadFromUrlStatusData data = request(HttpMethods.GET, statusUrl, UploadFromUrlStatusData.class);
            if (data.status.equals("success")) {
                return client.getFile(data.fileId);
            } else if (data.status.equals("error") || data.status.equals("failed")) {
                throw new UploadFailureException();
            }
        }
    }

    private <T> T request(String requestMethod, GenericUrl url, Class<T> dataClass) {
        try {
            HttpRequest request = requestFactory.buildRequest(requestMethod, url, null);
            HttpResponse response = request.execute();
            return response.parseAs(dataClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

}
