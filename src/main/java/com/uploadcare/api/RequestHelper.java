package com.uploadcare.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.uploadcare.data.DataWrapper;
import com.uploadcare.data.PageData;
import com.uploadcare.exceptions.UploadcareApiException;
import com.uploadcare.exceptions.UploadcareAuthenticationException;
import com.uploadcare.exceptions.UploadcareInvalidRequestException;
import com.uploadcare.exceptions.UploadcareNetworkException;
import com.uploadcare.urls.UrlParameter;

import com.uploadcare.urls.Urls;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.activation.MimetypesFileTypeMap;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import static com.uploadcare.urls.UrlUtils.trustedBuild;

/**
 * A helper class for doing API calls to the Uploadcare API. Supports API version 0.6.
 *
 * TODO Support of throttled requests needs to be added
 */
public class RequestHelper {

    private final Client client;

    public static final String LIBRARY_VERSION = "3.5.0";

    public static final String DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss Z";

    public static final String DATE_FORMAT_ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss";

    public static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    public static final TimeZone GMT = TimeZone.getTimeZone("GMT");

    private static final String EMPTY_MD5 = DigestUtils.md5Hex("");

    private static final String JSON_CONTENT_TYPE = "application/json";

    private static final String MAC_ALGORITHM = "HmacSHA1";

    RequestHelper(Client client) {
        this.client = client;
    }

    public static String rfc2822(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(RequestHelper.DATE_FORMAT, Locale.US);
        dateFormat.setTimeZone(GMT);
        return dateFormat.format(date);
    }

    public static String iso8601(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(RequestHelper.DATE_FORMAT_ISO_8601, Locale.US);
        dateFormat.setTimeZone(UTC);
        return dateFormat.format(date);
    }

    public static String getMimeType(String fileName) {
        MimetypesFileTypeMap fileTypeMap = new MimetypesFileTypeMap();
        return fileTypeMap.getContentType(fileName);
    }

    public String makeSignature(HttpUriRequest request, String date, String requestBodyMD5)
            throws NoSuchAlgorithmException, InvalidKeyException {
        if (requestBodyMD5 == null) {
            requestBodyMD5 = EMPTY_MD5;
        }

        StringBuilder sb = new StringBuilder();
        String uriWithoutBase = request.getURI().getRawPath();
        String query = request.getURI().getRawQuery();
        if (query != null) {
            uriWithoutBase += String.format("?%s", query);
        }
        sb.append(request.getMethod())
                .append("\n").append(requestBodyMD5)
                .append("\n").append(JSON_CONTENT_TYPE)
                .append("\n").append(date)
                .append("\n").append(uriWithoutBase);
        byte[] secretKeyBytes;
        if (client.getSecretKey() != null) {
            secretKeyBytes = client.getSecretKey().getBytes();
        } else {
            throw new UploadcareAuthenticationException("Secret key is required for this request.");
        }

        SecretKeySpec signingKey = new SecretKeySpec(secretKeyBytes, MAC_ALGORITHM);
        Mac mac = Mac.getInstance(MAC_ALGORITHM);
        mac.init(signingKey);
        byte[] hmacBytes = mac.doFinal(sb.toString().getBytes());
        return Hex.encodeHexString(hmacBytes);
    }

    public void setApiHeaders(HttpUriRequest request, String requestBodyMD5) {
        Calendar calendar = new GregorianCalendar(GMT);
        String formattedDate = rfc2822(calendar.getTime());

        request.addHeader("Content-Type", JSON_CONTENT_TYPE);
        request.setHeader("Accept", "application/vnd.uploadcare-v0.6+json");
        request.setHeader("Date", formattedDate);
        request.setHeader("User-Agent",
                String.format("javauploadcare/%s/%s", LIBRARY_VERSION, client.getPublicKey()));

        String authorization;
        if (client.isSimpleAuth()) {
            authorization = "Uploadcare.Simple " + client.getPublicKey() + ":" + client
                    .getSecretKey();
        } else {
            try {
                String signature = makeSignature(request, formattedDate, requestBodyMD5);
                authorization = "Uploadcare " + client.getPublicKey() + ":" + signature;
            } catch (GeneralSecurityException e) {
                throw new UploadcareApiException("Error when signing the request", e);
            }
        }
        request.setHeader("Authorization", authorization);
    }

    public <T> T executeQuery(
            HttpUriRequest request,
            boolean apiHeaders,
            Class<T> dataClass) {
        return executeQuery(request, apiHeaders, dataClass, null);
    }

    public <T> T executeQuery(
            HttpUriRequest request,
            boolean apiHeaders,
            TypeReference<T> dataType) {
        return executeQuery(request, apiHeaders, dataType, null);
    }

    public <T> T executeQuery(
            HttpUriRequest request,
            boolean apiHeaders,
            Class<T> dataClass,
            String requestBodyMD5) {
        if (apiHeaders) {
            setApiHeaders(request, requestBodyMD5);
        }
        try {
            CloseableHttpResponse response = client.getHttpClient().execute(request);
            checkResponseStatus(response);
            try {
                HttpEntity entity = response.getEntity();
                String data = EntityUtils.toString(entity);
                return client.getObjectMapper().readValue(data, dataClass);
            } finally {
                response.close();
            }
        } catch (IOException e) {
            throw new UploadcareNetworkException(e);
        }
    }

    public <T> T executeQuery(
            HttpUriRequest request,
            boolean apiHeaders,
            TypeReference<T> dataType,
            String requestBodyMD5) {
        if (apiHeaders) {
            setApiHeaders(request, requestBodyMD5);
        }
        try {
            CloseableHttpResponse response = client.getHttpClient().execute(request);
            checkResponseStatus(response);
            try {
                HttpEntity entity = response.getEntity();
                String data = EntityUtils.toString(entity);
                return client.getObjectMapper().readValue(data, dataType);
            } finally {
                response.close();
            }
        } catch (IOException e) {
            throw new UploadcareNetworkException(e);
        }
    }

    public static void setQueryParameters(URIBuilder builder, Collection<UrlParameter> parameters) {
        for (UrlParameter parameter : parameters) {
            builder.setParameter(parameter.getParam(), parameter.getValue());
        }
    }

    public <T, U> Iterable<T> executePaginatedQuery(
            final URI url,
            final Collection<UrlParameter> urlParameters,
            final boolean apiHeaders,
            final Class<? extends PageData<U>> dataClass,
            final DataWrapper<T, U> dataWrapper) {
        return new Iterable<T>() {
            public Iterator<T> iterator() {
                return new Iterator<T>() {
                    private URI next = null;

                    private boolean more;

                    private Iterator<U> pageIterator;

                    {
                        getNext();
                    }

                    private void getNext() {
                        URI pageUrl;
                        if (next == null) {
                            URIBuilder builder = new URIBuilder(url);
                            setQueryParameters(builder, urlParameters);

                            pageUrl = trustedBuild(builder);
                        } else {
                            pageUrl = next;
                        }
                        PageData<U> pageData = executeQuery(new HttpGet(pageUrl), apiHeaders,
                                dataClass);
                        more = pageData.hasMore();
                        next = pageData.getNext();
                        pageIterator = pageData.getResults().iterator();
                    }

                    public boolean hasNext() {
                        if (pageIterator.hasNext()) {
                            return true;
                        } else if (more) {
                            getNext();
                            return true;
                        } else {
                            return false;
                        }
                    }

                    public T next() {
                        return dataWrapper.wrap(pageIterator.next());
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    /**
     * Executes the request et the Uploadcare API and return the HTTP Response object.
     *
     * The existence of this method(and it's return type) enables the end user to extend the
     * functionality of the
     * Uploadcare API client by creating a subclass of {@link com.uploadcare.api.Client}.
     *
     * @param request    request to be sent to the API
     * @param apiHeaders TRUE if the default API headers should be set
     * @return HTTP Response object
     */
    public HttpResponse executeCommand(HttpUriRequest request, boolean apiHeaders) {
        return executeCommand(request, apiHeaders, null);
    }

    /**
     * Executes the request et the Uploadcare API and return the HTTP Response object.
     *
     * The existence of this method(and it's return type) enables the end user to extend the
     * functionality of the
     * Uploadcare API client by creating a subclass of {@link com.uploadcare.api.Client}.
     *
     * @param request        request to be sent to the API
     * @param apiHeaders     TRUE if the default API headers should be set
     * @param requestBodyMD5 MD5
     * @return HTTP Response object
     */
    public HttpResponse executeCommand(HttpUriRequest request, boolean apiHeaders, String requestBodyMD5) {
        if (apiHeaders) {
            setApiHeaders(request, requestBodyMD5);
        }

        try {
            CloseableHttpResponse response = client.getHttpClient().execute(request);
            try {
                checkResponseStatus(response);
                return response;
            } finally {
                response.close();
            }
        } catch (IOException e) {
            throw new UploadcareNetworkException(e);
        }
    }

    /**
     * Verifies that the response status codes are within acceptable boundaries and throws
     * corresponding exceptions
     * otherwise.
     *
     * @param response The response object to be checked
     */
    private void checkResponseStatus(HttpResponse response) throws IOException {

        int statusCode = response.getStatusLine().getStatusCode();

        if (statusCode >= 200 && statusCode < 300) {
            return;
        } else if (statusCode == 401 || statusCode == 403) {
            throw new UploadcareAuthenticationException(
                    streamToString(response.getEntity().getContent()));
        } else if (statusCode == 400 || statusCode == 404) {
            throw new UploadcareInvalidRequestException(
                    streamToString(response.getEntity().getContent()));
        } else if(statusCode == 429 ){
            throw new UploadcareApiException(
                    streamToString(response.getEntity().getContent()));
        } else {
            throw new UploadcareApiException(
                    "Unknown exception during an API call, response:" + streamToString(
                            response.getEntity().getContent()));
        }
    }

    /**
     * Convert an InputStream into a String object. Method taken from http://stackoverflow.com/a/5445161/521535
     *
     * @param is The stream to be converted
     * @return The resulting String
     */
    private static String streamToString(InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is, "UTF-8").useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
