package com.uploadcare.api;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Request {

    private Client client;
    private String method;
    private Url url;

    public static final String DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss Z";
    private static final String EMPTY_MD5 = DigestUtils.md5Hex("");
    private static final String JSON_CONTENT_TYPE = "application/json";

    public Request(Client client, String method, Url url) {
        this.client = client;
        this.method = method;
        this.url = url;
    }

    public String makeSignature(String date) throws NoSuchAlgorithmException, InvalidKeyException {
        StringBuilder sb = new StringBuilder();
        sb.append(method)
                .append("\n").append(EMPTY_MD5)
                .append("\n").append(JSON_CONTENT_TYPE)
                .append("\n").append(date)
                .append("\n").append(url.getRawPath());

        byte[] privateKeyBytes = client.getPrivateKey().getBytes();
        SecretKeySpec signingKey = new SecretKeySpec(privateKeyBytes, "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signingKey);
        byte[] hmacBytes = mac.doFinal(sb.toString().getBytes());
        return Hex.encodeHexString(hmacBytes);
    }
}
