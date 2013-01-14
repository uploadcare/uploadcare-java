package com.uploadcare.api;

import com.uploadcare.data.DataWrapper;
import com.uploadcare.data.PageData;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.util.EntityUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.uploadcare.urls.UrlUtils.trustedBuild;

public class RequestHelper {

    private final Client client;

    public static final String DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss Z";
    public static final TimeZone UTC = TimeZone.getTimeZone("UTC");
    private static final String EMPTY_MD5 = DigestUtils.md5Hex("");
    private static final String JSON_CONTENT_TYPE = "application/json";

    RequestHelper(Client client) {
        this.client = client;
    }

    public static String rfc2822(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(RequestHelper.DATE_FORMAT);
        dateFormat.setTimeZone(UTC);
        return dateFormat.format(date);
    }

    public String makeSignature(HttpUriRequest request, String date)
            throws NoSuchAlgorithmException, InvalidKeyException {
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

    public void setApiHeaders(HttpUriRequest request) {
        Calendar calendar = new GregorianCalendar(UTC);
        String formattedDate = rfc2822(calendar.getTime());

        request.setHeader("Accept", "application/vnd.uploadcare-v0.2+json");
        request.setHeader("Date", formattedDate);
        request.setHeader("Content-Type", JSON_CONTENT_TYPE);

        String authorization;
        if (client.isSimpleAuth()) {
            authorization = "Uploadcare.Simple " + client.getPublicKey() + ":" + client.getPrivateKey();
        } else {
            try {
                String signature = makeSignature(request, formattedDate);
                authorization = "Uploadcare " + client.getPublicKey() + ":" + signature;
            } catch (GeneralSecurityException e) {
                throw new RuntimeException(e);
            }
        }
        request.setHeader("Authorization", authorization);
    }

    public <T> T executeQuery(HttpUriRequest request, boolean apiHeaders, Class<T> dataClass) {
        if (apiHeaders) {
            setApiHeaders(request);
        }
        try {
            HttpResponse response = client.getHttpClient().execute(request);
            HttpEntity entity = response.getEntity();
            String data = EntityUtils.toString(entity);
            return client.getObjectMapper().readValue(data, dataClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T, U> List<T> executePaginatedQuery(
            URI url,
            boolean apiHeaders,
            Class<? extends PageData<U>> dataClass,
            DataWrapper<T, U> dataWrapper) {
        int page = 1;
        ArrayList<T> itemList = new ArrayList<T>();
        PageData<U> pageData;
        do {
            URIBuilder builder = new URIBuilder(url);
            builder.setParameter("page", Integer.toString(page));
            URI pageUrl = trustedBuild(builder);
            pageData = executeQuery(new HttpGet(pageUrl), apiHeaders, dataClass);
            for (U dataItem : pageData.getResults()) {
                itemList.add(dataWrapper.wrap(dataItem));
            }
        } while (pageData.hasMore());
        return itemList;
    }

    public void executeCommand(HttpUriRequest request, boolean apiHeaders) {
        if (apiHeaders) {
            setApiHeaders(request);
        }
        try {
            client.getHttpClient().execute(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
