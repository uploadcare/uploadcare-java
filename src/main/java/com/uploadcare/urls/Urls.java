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
     * Creates a URL to a group resource.
     *
     * @see com.uploadcare.api.Client
     */
    public static URI apiGroup(String groupId) {
        return URI.create(API_BASE + "/groups/" + groupId + "/");
    }

    /**
     * Creates a URL to a uploaded file resource.
     *
     * @param publicKey
     * @param fileId File UUID
     *
     * @see com.uploadcare.api.Client
     */
    public static URI apiUploadedFile(String publicKey, String fileId) {
        URIBuilder builder = new URIBuilder(URI.create(UPLOAD_BASE));
        builder.setPath("/info/")
                .setParameter("pub_key", publicKey)
                .setParameter("file_id", fileId);
        return trustedBuild(builder);
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
     * Creates a URL to a file resource with specific fields.
     *
     * @param fileId File UUID
     * @param fields Add special fields to the file object, such as: rekognition_info.
     *
     * @see com.uploadcare.api.Client
     */
    public static URI getFileWithFields(String fileId, String fields) {
        URIBuilder builder = new URIBuilder(URI.create(API_BASE));
        builder.setPath("/files/" + fileId + "/")
                .setParameter("add_fields", fields);
        return trustedBuild(builder);
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
     * Creates a URL to the file local copy resource.
     *
     * @see com.uploadcare.api.Client
     */
    public static URI apiFileLocalCopy() {
        return URI.create(API_BASE + "/files/local_copy/");
    }

    /**
     * Creates a URL to the file remote copy resource.
     *
     * @see com.uploadcare.api.Client
     */
    public static URI apiFileRemoteCopy() {
        return URI.create(API_BASE + "/files/remote_copy/");
    }

    /**
     * Creates a URL to the storage action for a multiple files (saving/deleting the files).
     *
     * @see com.uploadcare.api.Client
     */
    public static URI apiFilesBatch() {
        return URI.create(API_BASE + "/files/storage/");
    }

    /**
     * Creates a URL to the group collection resource.
     *
     * @see com.uploadcare.api.Client
     */
    public static URI apiGroups(){
        return URI.create(API_BASE + "/groups/");
    }

    /**
     * Creates a URL to the storage action for a group (saving the group).
     *
     * @param groupId Group Id
     *
     * @see com.uploadcare.api.Client
     */
    public static URI apiGroupStorage(String groupId) {
        return URI.create(API_BASE + "/groups/" + groupId + "/storage/");
    }

    /**
     * Creates a URL to the webhook collection resource.
     *
     * @see com.uploadcare.api.Client
     */
    public static URI apiWebhooks(){
        return URI.create(API_BASE + "/webhooks/");
    }

    /**
     * Creates a URL for the webhook delete.
     *
     * @see com.uploadcare.api.Client
     */
    public static URI apiWebhook(int webhookId) {
        return URI.create(API_BASE + "/webhooks/"+webhookId+"/");
    }

    /**
     * Creates a URL for the webhook delete.
     *
     * @see com.uploadcare.api.Client
     */
    public static URI apiDeleteWebhook() {
        return URI.create(API_BASE + "/webhooks/unsubscribe/");
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
     * @see com.uploadcare.upload.UrlUploader
     */
    public static URI uploadFromUrl() {
        return URI.create(UPLOAD_BASE + "/from_url/");
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

    /**
     * Creates a URL to the file upload using multipart.
     *
     * @see com.uploadcare.upload.FileUploader
     */
    public static URI uploadMultipartStart() {
        return URI.create(UPLOAD_BASE + "/multipart/start/");
    }

    /**
     * Creates a URL to the file chunk upload using multipart.
     *
     * @see com.uploadcare.upload.FileUploader
     */
    public static URI uploadMultipartPart(String preSignedPartUrl) {
        return URI.create(preSignedPartUrl);
    }

    /**
     * Creates a URL for multipart upload complete.
     *
     * @see com.uploadcare.upload.FileUploader
     */
    public static URI uploadMultipartComplete() {
        return URI.create(UPLOAD_BASE + "/multipart/complete/");
    }
}
