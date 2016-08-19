package com.uploadcare.api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.uploadcare.data.CopyFileData;
import com.uploadcare.data.FileData;
import com.uploadcare.data.ProjectData;
import com.uploadcare.exceptions.UploadcareApiException;
import com.uploadcare.urls.Urls;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Uploadcare API client.
 *
 * Provides simple access to {@code File} and {@code Project} resources.
 *
 * @see com.uploadcare.api.File
 * @see Project
 */
public class Client {

    private final String publicKey;
    private final String privateKey;
    private final boolean simpleAuth;

    private final CloseableHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final RequestHelperProvider requestHelperProvider;

    /**
     * Initializes a client with custom access keys and simple authentication.
     *
     * @param publicKey Public key
     * @param privateKey Private key
     */
    public Client(String publicKey, String privateKey) {
        this(publicKey, privateKey, true, null);
    }

    /**
     * Initializes a client with custom access keys and simple authentication.
     *
     * @param publicKey Public key
     * @param privateKey Private key
     * @param privateKey CloseableHttpClient
     */
    public Client(String publicKey, String privateKey, CloseableHttpClient httpClient) {
        this(publicKey, privateKey, true, null, httpClient);
    }

    /**
     * Initializes a client with custom access keys.
     * Can use simple or secure authentication.
     *
     * @param publicKey Public key
     * @param privateKey Private key
     * @param simpleAuth If {@code false}, HMAC-based authentication is used
     * @param requestHelperProvider Should be {@code null} to use {@link DefaultRequestHelperProvider}
     */
    public Client(
            String publicKey,
            String privateKey,
            boolean simpleAuth,
            RequestHelperProvider requestHelperProvider) {
        this(publicKey, privateKey, simpleAuth, requestHelperProvider, null);
    }

    /**
     * Initializes a client with custom access keys.
     * Can use simple or secure authentication.
     *
     * @param publicKey Public key
     * @param privateKey Private key
     * @param simpleAuth If {@code false}, HMAC-based authentication is used
     * @param requestHelperProvider Should be {@code null} to use {@link DefaultRequestHelperProvider}
     * @param httpClient Custom HttpClient
     */
    public Client(
            String publicKey,
            String privateKey,
            boolean simpleAuth,
            RequestHelperProvider requestHelperProvider,
            CloseableHttpClient httpClient) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.simpleAuth = simpleAuth;

        if (requestHelperProvider != null) {
            this.requestHelperProvider = requestHelperProvider;
            this.httpClient = null;
            this.objectMapper = null;
        } else {
            this.requestHelperProvider = new DefaultRequestHelperProvider();

            if (httpClient != null) {
                this.httpClient = httpClient;
            } else {
                PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
                cm.setMaxTotal(200);
                cm.setDefaultMaxPerRoute(20);
                this.httpClient = HttpClients.custom()
                        .setConnectionManager(cm)
                        .build();
            }
            this.objectMapper = new ObjectMapper();
            this.objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
            this.objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        }
    }

    /**
     * Creates a client with demo credentials.
     * Useful for tests and anonymous access.
     *
     * <b>Warning!</b> Do not use in production.
     * All demo account files are eventually purged.
     *
     * @return A demo client
     */
    public static Client demoClient() {
        return new Client("demopublickey", "demoprivatekey");
    }

    /**
     * Returns the public key.
     *
     * @return Public key
     */
    public String getPublicKey() {
        return publicKey;
    }

    /**
     * Returns the private key.
     *
     * @return Private key
     */
    public String getPrivateKey() {
        return privateKey;
    }

    /**
     * Returns {@code true}, if simple authentication is used.
     *
     * @return {@code true}, if simple authentication is used, {@code false} otherwise
     */
    public boolean isSimpleAuth() {
        return simpleAuth;
    }

    CloseableHttpClient getHttpClient() {
        return httpClient;
    }

    ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public RequestHelper getRequestHelper() {
        return requestHelperProvider.get(this);
    }

    /**
     * Requests project info from the API.
     *
     * @return Project resource
     */
    public Project getProject() {
        URI url = Urls.apiProject();
        RequestHelper requestHelper = getRequestHelper();
        ProjectData projectData = requestHelper.executeQuery(new HttpGet(url), true, ProjectData.class);
        return new Project(this, projectData);
    }

    /**
     * Requests file data.
     *
     * @param fileId Resource UUID
     * @return File resource
     */
    public File getFile(String fileId) {
        URI url = Urls.apiFile(fileId);
        RequestHelper requestHelper = getRequestHelper();
        FileData fileData = requestHelper.executeQuery(new HttpGet(url), true, FileData.class);
        return new File(this, fileData);
    }

    /**
     * Begins to build a request for uploaded files for the current account.
     *
     * @return File resource request builder
     */
    public FilesQueryBuilder getFiles() {
        return new FilesQueryBuilder(this);
    }

    /**
     * Marks a file as deleted.
     *
     * @param fileId Resource UUID
     */
    public void deleteFile(String fileId) {
        URI url = Urls.apiFile(fileId);
        RequestHelper requestHelper = getRequestHelper();
        requestHelper.executeCommand(new HttpDelete(url), true);
    }

    /**
     * Marks a file as saved.
     *
     * This has to be done for all files you want to keep.
     * Unsaved files are eventually purged.
     *
     * @param fileId Resource UUID
     */
    public void saveFile(String fileId) {
        URI url = Urls.apiFileStorage(fileId);
        RequestHelper requestHelper = getRequestHelper();
        requestHelper.executeCommand(new HttpPost(url), true);
    }

    /**
     * Closes client.
     *
     * Ensures that all connections kept alive by the manager get closed and system resources
     * allocated by those connections are released.
     */
    public void close() {
        if (httpClient != null) {
            try {
                httpClient.close();
            } catch (IOException e) {
                throw new UploadcareApiException("Error during closing CloseableHttpClient", e);
            }
        }
    }

    /**
     * @param fileId  Resource UUID
     * @param storage Target storage name
     * @return An object containing the results of the copy request
     */
    public CopyFileData copyFile(String fileId, String storage) {
        RequestHelper requestHelper = getRequestHelper();
        HttpPost request = new HttpPost(Urls.apiFiles());

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("source", fileId));
        if (storage != null && !storage.isEmpty()) {
            nameValuePairs.add(new BasicNameValuePair("target", storage));
        }

        try {
            request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        } catch (UnsupportedEncodingException e) {
            throw new UploadcareApiException("Error during copyFile request creation", e);
        }

        return requestHelper.executeQuery(request, true, CopyFileData.class);
    }
}
