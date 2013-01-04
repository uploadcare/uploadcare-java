package com.uploadcare.urls;

import org.apache.http.client.utils.URIBuilder;

import java.net.URI;

import static com.uploadcare.urls.UrlUtils.trustedBuild;

public class Urls {

    private static final String API_BASE = "https://api.uploadcare.com";
    private static final String CDN_BASE = "https://ucarecdn.com";
    private static final String UPLOAD_BASE = "https://upload.uploadcare.com";

    public static URI apiAccount() {
        return URI.create(API_BASE + "/account/");
    }

    public static URI apiFile(String fileId) {
        return URI.create(API_BASE + "/files/" + fileId + "/");
    }

    public static URI apiFileStorage(String fileId) {
        return URI.create(API_BASE + "/files/" + fileId + "/storage/");
    }

    public static URI apiFiles() {
        return URI.create(API_BASE + "/files/");
    }

    public static URI cdn(CdnPathBuilder builder) {
        return URI.create(CDN_BASE + builder.build());
    }

    public static URI uploadBase() {
        return URI.create(UPLOAD_BASE + "/base/");
    }

    public static URI uploadFromUrl(String sourceUrl, String pubKey) {
        URIBuilder builder = new URIBuilder(URI.create(UPLOAD_BASE));
        builder.setPath("/from_url/")
                .setParameter("source_url", sourceUrl)
                .setParameter("pub_key", pubKey);
        return trustedBuild(builder);
    }

    public static URI uploadFromUrlStatus(String token) {
        URIBuilder builder = new URIBuilder(URI.create(UPLOAD_BASE));
        builder.setPath("/from_url/status/")
                .setParameter("token", token);
        return trustedBuild(builder);
    }

}
