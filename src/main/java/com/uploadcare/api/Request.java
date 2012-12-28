package com.uploadcare.api;

import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.uploadcare.urls.ApiUrl;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

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
    private final String method;
    private final ApiUrl url;

    public static final String DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss Z";
    public static final TimeZone UTC = TimeZone.getTimeZone("UTC");
    private static final String EMPTY_MD5 = DigestUtils.md5Hex("");
    private static final String JSON_CONTENT_TYPE = "application/json";

    private static final HttpRequestFactory requestFactory = new NetHttpTransport()
            .createRequestFactory(new HttpRequestInitializer() {
                @Override
                public void initialize(HttpRequest httpRequest) throws IOException {
                    httpRequest.setParser(new JsonObjectParser(new JacksonFactory()));
                }
            });

    public Request(Client client, String method, ApiUrl url) {
        this.client = client;
        this.method = method;
        this.url = url;
    }

    public static String rfc2822(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(Request.DATE_FORMAT);
        dateFormat.setTimeZone(UTC);
        return dateFormat.format(date);
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

    private HttpHeaders makeHeaders() {
        Calendar calendar = new GregorianCalendar(UTC);
        String formattedDate = rfc2822(calendar.getTime());

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept("application/vnd.uploadcare-v0.2+json");
        headers.setDate(formattedDate);
        headers.setContentType(JSON_CONTENT_TYPE);

        try {
            String signature = makeSignature(formattedDate);
            headers.set("Authentication", "Uploadcare " + client.getPublicKey() + ":" + signature);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }

        return headers;
    }

    private HttpResponse execute() throws IOException {
        HttpRequest request = requestFactory.buildRequest(method, url, null);
        request.setHeaders(makeHeaders());
        HttpResponse response = request.execute();
        return response;
    }

    public <T> T executeQuery(Class<T> dataClass) {
        try {
            HttpResponse response = execute();
            return response.parseAs(dataClass);
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
