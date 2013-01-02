package com.uploadcare.api;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class Request {

    private final Client client;
    private final HttpUriRequest request;

    public static final String DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss Z";
    public static final TimeZone UTC = TimeZone.getTimeZone("UTC");
    private static final String EMPTY_MD5 = DigestUtils.md5Hex("");
    private static final String JSON_CONTENT_TYPE = "application/json";

    public Request(Client client, HttpUriRequest request) {
        this.client = client;
        this.request = request;
    }

    public static String rfc2822(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(Request.DATE_FORMAT);
        dateFormat.setTimeZone(UTC);
        return dateFormat.format(date);
    }

    public String makeSignature(String date) throws NoSuchAlgorithmException, InvalidKeyException {
        StringBuilder sb = new StringBuilder();
        sb.append(request.getMethod())
                .append("\n").append(EMPTY_MD5)
                .append("\n").append(JSON_CONTENT_TYPE)
                .append("\n").append(date)
                .append("\n").append(request.getURI().getPath());

        byte[] privateKeyBytes = client.getPrivateKey().getBytes();
        SecretKeySpec signingKey = new SecretKeySpec(privateKeyBytes, "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signingKey);
        byte[] hmacBytes = mac.doFinal(sb.toString().getBytes());
        return Hex.encodeHexString(hmacBytes);
    }

    private void makeHeaders() {
        Calendar calendar = new GregorianCalendar(UTC);
        String formattedDate = rfc2822(calendar.getTime());

        request.setHeader("Accept", "application/vnd.uploadcare-v0.2+json");
        request.setHeader("Date", formattedDate);
        request.setHeader("Content-Type", JSON_CONTENT_TYPE);

        try {
            String signature = makeSignature(formattedDate);
            request.setHeader("Authentication", "Uploadcare " + client.getPublicKey() + ":" + signature);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpResponse execute() throws IOException {
        makeHeaders();
        return client.getHttpClient().execute(request);
    }

    public <T> T executeQuery(Class<T> dataClass) {
        try {
            HttpResponse response = execute();
            HttpEntity entity = response.getEntity();
            String data = EntityUtils.toString(entity);
            return client.getObjectMapper().readValue(data, dataClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void executeCommand() {
        try {
            execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
