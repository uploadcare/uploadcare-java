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
    private String store = "auto";

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
    public File upload() throws UploadFailureException {
        return upload(500);
    }

    /**
     * Store the file upon uploading.
     *
     * @param store is set true - store the file upon uploading. Requires “automatic file storing” setting to be enabled.
     *              is set false - do not store file upon uploading.
     */
    public UrlUploader store(boolean store) {
        this.store = store ? String.valueOf(1) : String.valueOf(0);
        return this;
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
        URI uploadUrl = Urls.uploadFromUrl(sourceUrl, client.getPublicKey(), store);
        String token = requestHelper.executeQuery(new HttpGet(uploadUrl), false, UploadFromUrlData.class).token;
        URI statusUrl = Urls.uploadFromUrlStatus(token);
        while (true) {
            sleep(pollingInterval);
            HttpGet request = new HttpGet(statusUrl);
            UploadFromUrlStatusData data = requestHelper.executeQuery(
                    request,
                    false,
                    UploadFromUrlStatusData.class);
            if (data.status.equals("success")) {
                if (client.getSecretKey() != null) {
                    // If Client have "secretkey", we use Rest API to get full file info.
                    return client.getFile(data.fileId);
                } else {
                    // If Client doesn't have "secretkey" info about file might not have all info.
                    return client.getUploadedFile(data.fileId);
                }
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
