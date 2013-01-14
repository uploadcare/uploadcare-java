package com.uploadcare.upload;

import com.uploadcare.api.Client;
import com.uploadcare.api.File;
import com.uploadcare.api.RequestHelper;
import com.uploadcare.data.UploadFromUrlData;
import com.uploadcare.data.UploadFromUrlStatusData;
import com.uploadcare.urls.Urls;
import org.apache.http.client.methods.HttpGet;

import java.net.URI;

/**
 * Uploadcare uploader for URLs.
 */
public class UrlUploader implements Uploader {

    private final Client client;
    private final String sourceUrl;

    /**
     * Create a new uploader from a URL.
     *
     * @param client Uploadcare client
     * @param sourceUrl URL to upload from
     */
    public UrlUploader(Client client, String sourceUrl) {
        this.client = client;
        this.sourceUrl = sourceUrl;
    }

    /**
     * Synchronously uploads the file to Uploadcare.
     *
     * The calling thread will be busy until the upload is finished.
     * Uploadcare is polled every 500 ms for upload progress.
     *
     * @return An Uploadcare file
     * @throws UploadFailureException
     */
    @Override
    public File upload() throws UploadFailureException {
        return upload(500);
    }

    /**
     * Synchronously uploads the file to Uploadcare.
     *
     * The calling thread will be busy until the upload is finished.
     *
     * @param pollingInterval Progress polling interval in ms
     * @return An Uploadcare file
     * @throws UploadFailureException
     */
    public File upload(int pollingInterval) throws UploadFailureException {
        RequestHelper requestHelper = client.getRequestHelper();
        URI uploadUrl = Urls.uploadFromUrl(sourceUrl, client.getPublicKey());
        String token = requestHelper.executeQuery(new HttpGet(uploadUrl), false, UploadFromUrlData.class).token;
        URI statusUrl = Urls.uploadFromUrlStatus(token);
        while (true) {
            sleep(pollingInterval);
            HttpGet request = new HttpGet(statusUrl);
            UploadFromUrlStatusData data = requestHelper.executeQuery(request, false, UploadFromUrlStatusData.class);
            if (data.status.equals("success")) {
                return client.getFile(data.fileId);
            } else if (data.status.equals("error") || data.status.equals("failed")) {
                throw new UploadFailureException();
            }
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
