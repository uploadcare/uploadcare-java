package com.uploadcare.api;

import com.uploadcare.data.DataWrapper;
import com.uploadcare.data.PageData;
import com.uploadcare.exceptions.UploadcareApiException;
import com.uploadcare.exceptions.UploadcareAuthenticationException;
import com.uploadcare.exceptions.UploadcareInvalidRequestException;
import com.uploadcare.exceptions.UploadcareNetworkException;
import com.uploadcare.urls.UrlParameter;

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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import static com.uploadcare.urls.UrlUtils.trustedBuild;

/**
 * A helper class for doing API calls to the Uploadcare API. Supports API version 0.4.
 *
 * TODO Support of throttled requests needs to be added
 */
public class RequestHelper {

    private final Client client;

    public static final String LIBRARY_VERSION = "3.0";

    public static final String DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss Z";

    public static final String DATE_FORMAT_ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

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

    public static String iso8601(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(RequestHelper.DATE_FORMAT_ISO_8601);
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

        request.setHeader("Accept", "application/vnd.uploadcare-v0.4+json");
        request.setHeader("Date", formattedDate);
        request.setHeader("User-Agent",String.format("javauploadcare/%s/%s",LIBRARY_VERSION,client.getPublicKey()));

        String authorization;
        if (client.isSimpleAuth()) {
            authorization = "Uploadcare.Simple " + client.getPublicKey() + ":" + client
                    .getPrivateKey();
        } else {
            try {
                String signature = makeSignature(request, formattedDate);
                authorization = "Uploadcare " + client.getPublicKey() + ":" + signature;
            } catch (GeneralSecurityException e) {
                throw new UploadcareApiException("Error when signing the request", e);
            }
        }
        request.setHeader("Authorization", authorization);
    }

    public <T> T executeQuery(HttpUriRequest request, boolean apiHeaders, Class<T> dataClass) {
        if (apiHeaders) {
            setApiHeaders(request);
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

    public static void setQueryParameters(URIBuilder builder, List<UrlParameter> parameters) {
        for (UrlParameter parameter : parameters) {
            builder.setParameter(parameter.getParam(), parameter.getValue());
        }
    }

    public <T, U> Iterable<T> executePaginatedQuery(
            final URI url,
            final List<UrlParameter> urlParameters,
            final boolean apiHeaders,
            final Class<? extends PageData<U>> dataClass,
            final DataWrapper<T, U> dataWrapper) {
        return new Iterable<T>() {
            public Iterator<T> iterator() {
                return new Iterator<T>() {
                    private int offset = 0;

                    private boolean more;
                    private Iterator<U> pageIterator;

                    {
                        getNext();
                    }

                    private void getNext() {
                        URIBuilder builder = new URIBuilder(url);
                        setQueryParameters(builder, urlParameters);
                        builder.setParameter("offset", Integer.toString(offset));
                        URI pageUrl = trustedBuild(builder);
                        PageData<U> pageData = executeQuery(new HttpGet(pageUrl), apiHeaders,
                                dataClass);
                        more = pageData.hasMore();
                        offset += pageData.getResults().size();
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
        if (apiHeaders) {
            setApiHeaders(request);
        }

        try {
            CloseableHttpResponse response = client.getHttpClient().execute(request);
            try {
                checkResponseStatus(response);
                return response;
            }finally {
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
