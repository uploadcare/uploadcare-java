package com.uploadcare.urls;

import org.apache.http.client.utils.URIBuilder;

import java.net.URI;

import static com.uploadcare.urls.UrlUtils.trustedBuild;

/**
 * Uploadcare API URL factory methods.
 */
public class Urls {

    private static final String API_BASE = "https://api.uploadcare.com";
    private static final String CDN_BASE = "https://ucarecdn.com";
    private static final String UPLOAD_BASE = "https://upload.uploadcare.com";

    /**
     * Creates a URL to a project resource.
     *
     * @see com.uploadcare.api.Client
     */
    public static URI apiProject() {
        return URI.create(API_BASE + "/project/");
    }

    /**
     * Creates a URL to a file resource.
     *
     * @param fileId File UUID
     *
     * @see com.uploadcare.api.Client
     */
    public static URI apiFile(String fileId) {
        return URI.create(API_BASE + "/files/" + fileId + "/");
    }

    /**
     * Creates a URL to the storage action for a file (saving the file).
     *
     * @param fileId File UUID
     *
     * @see com.uploadcare.api.Client
     */
    public static URI apiFileStorage(String fileId) {
        return URI.create(API_BASE + "/files/" + fileId + "/storage/");
    }

    /**
     * Creates a URL to the file collection resource.
     *
     * @see com.uploadcare.api.Client
     */
    public static URI apiFiles() {
        return URI.create(API_BASE + "/files/");
    }

    /**
     * Creates a full CDN URL with a CDN path builder.
     *
     * @param builder Configured CDN path builder
     */
    public static URI cdn(CdnPathBuilder builder) {
        return URI.create(CDN_BASE + builder.build());
    }

    /**
     * Creates a URL to the file upload endpoint.
     *
     * @see com.uploadcare.upload.FileUploader
     */
    public static URI uploadBase() {
        return URI.create(UPLOAD_BASE + "/base/");
    }

    /**
     * Creates a URL for URL upload.
     *
     * @param sourceUrl URL to upload from
     * @param pubKey Public key
     * @param store Store the file upon uploading.
     *
     * @see com.uploadcare.upload.UrlUploader
     */
    public static URI uploadFromUrl(String sourceUrl, String pubKey, String store) {
        URIBuilder builder = new URIBuilder(URI.create(UPLOAD_BASE));
        builder.setPath("/from_url/")
                .setParameter("source_url", sourceUrl)
                .setParameter("pub_key", pubKey)
                .setParameter("store", store);
        return trustedBuild(builder);
    }

    /**
     * Creates a URL for URL upload status (e.g. progress).
     *
     * @param token Token, received after a URL upload request
     *
     * @see com.uploadcare.upload.UrlUploader
     */
    public static URI uploadFromUrlStatus(String token) {
        URIBuilder builder = new URIBuilder(URI.create(UPLOAD_BASE));
        builder.setPath("/from_url/status/")
                .setParameter("token", token);
        return trustedBuild(builder);
    }

}
