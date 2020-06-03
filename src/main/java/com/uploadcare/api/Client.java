package com.uploadcare.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.uploadcare.data.CopyFileData;
import com.uploadcare.data.FileData;
import com.uploadcare.data.GroupData;
import com.uploadcare.data.ProjectData;
import com.uploadcare.exceptions.UploadcareApiException;
import com.uploadcare.urls.*;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
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
    private final String secretKey;
    private final boolean simpleAuth;

    private final CloseableHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final RequestHelperProvider requestHelperProvider;

    private final int MAX_SAVE_DELETE_BATCH_SIZE = 100;

    /**
     * Initializes a client with custom access keys and simple authentication.
     *
     * @param publicKey Public key
     * @param secretKey Secret key, if {@code null}, client will only be able to upload files and get info about them.
     */
    public Client(String publicKey, String secretKey) {
        this(publicKey, secretKey, false, null);
    }

    /**
     * Initializes a client with custom access keys and simple authentication.
     *
     * @param publicKey  Public key
     * @param secretKey  Secret key
     * @param httpClient CloseableHttpClient
     */
    public Client(String publicKey, String secretKey, CloseableHttpClient httpClient) {
        this(publicKey, secretKey, false, null, httpClient);
    }

    /**
     * Initializes a client with custom access keys.
     * Can use simple or secure authentication.
     *
     * @param publicKey             Public key
     * @param secretKey             Secret key, if {@code null}, client will only be able to upload files and get info
     *                              about them.
     * @param simpleAuth            If {@code false}, HMAC-based authentication is used, otherwise simple
     *                              authentication is used.
     * @param requestHelperProvider Should be {@code null} to use {@link DefaultRequestHelperProvider}
     */
    public Client(
            String publicKey,
            String secretKey,
            boolean simpleAuth,
            RequestHelperProvider requestHelperProvider) {
        this(publicKey, secretKey, simpleAuth, requestHelperProvider, null);
    }

    /**
     * Initializes a client with custom access keys.
     * Can use simple or secure authentication.
     *
     * @param publicKey             Public key
     * @param secretKey             Secret key, if {@code null}, client will only be able to upload files and get info
     *                              about them.
     * @param simpleAuth            If {@code false}, HMAC-based authentication is used, otherwise simple
     *                              authentication is used.
     * @param requestHelperProvider Should be {@code null} to use {@link DefaultRequestHelperProvider}
     * @param httpClient            Custom HttpClient
     */
    public Client(
            String publicKey,
            String secretKey,
            boolean simpleAuth,
            RequestHelperProvider requestHelperProvider,
            CloseableHttpClient httpClient) {
        this.publicKey = publicKey;
        this.secretKey = secretKey;
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
            this.objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
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
        return new Client("demopublickey", "demosecretkey");
    }

    /**
     * Creates a client with demo credentials for uploading files only, Rest API cannot be accessed using this client.
     * Useful for testing uploading.
     *
     * <b>Warning!</b> Do not use in production.
     * All demo account files are eventually purged.
     *
     * @return A demo client for uploading files
     */
    public static Client demoClientUploadOnly() {
        return new Client("demopublickey", null);
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
     * Returns the Secret key.
     *
     * @return Secret key
     */
    public String getSecretKey() {
        return secretKey;
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
     * Requests group info from the API.
     *
     * @param groupId
     * @return Group resource
     */
    public Group getGroup(String groupId) {
        URI url = Urls.apiGroup(groupId);
        RequestHelper requestHelper = getRequestHelper();
        GroupData groupData = requestHelper.executeQuery(new HttpGet(url), true, GroupData.class);
        return new Group(this, groupData);
    }

    /**
     * Request file data for uploaded file. Does not require "privatekey" set for UploadcareClient.
     */
    public File getUploadedFile(String fileId) {
        URI url = Urls.apiUploadedFile(getPublicKey(), fileId);

        RequestHelper requestHelper = getRequestHelper();
        FileData fileData = requestHelper.executeQuery(new HttpGet(url), false, FileData.class);
        return new File(this, fileData);
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
     * Begins to build a request for groups for the current account.
     *
     * @return Group resource request builder
     */
    public GroupQueryBuilder getGroups() {
        return new GroupQueryBuilder(this);
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
     * Marks a files as deleted.
     * Maximum 100 file id's can be provided.
     *
     * @param fileIds Resource UUIDs
     */
    public void deleteFiles(List<String> fileIds) {
        if (fileIds.size() <= MAX_SAVE_DELETE_BATCH_SIZE) {
            // Make single request.
            URI url = Urls.apiFilesBatch();
            RequestHelper requestHelper = getRequestHelper();
            String requestBodyContent = trySerializeRequestBodyContent(fileIds);
            StringEntity requestEntity = new StringEntity(
                    requestBodyContent,
                    ContentType.APPLICATION_JSON);

            HttpDeleteWithBody request = new HttpDeleteWithBody(url);
            request.setEntity(requestEntity);
            requestHelper.executeCommand(request, true, DigestUtils.md5Hex(requestBodyContent));
        } else {
            // Make batch requests.
            executeSaveDeleteBatchCommand(false, fileIds);
        }

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
     * Marks multiple files as saved.
     *
     * This has to be done for all files you want to keep.
     * Unsaved files are eventually purged.
     *
     * @param fileIds Resource UUIDs
     */
    public void saveFiles(List<String> fileIds) {
        if (fileIds.size() <= MAX_SAVE_DELETE_BATCH_SIZE) {
            // Make single request.
            URI url = Urls.apiFilesBatch();
            RequestHelper requestHelper = getRequestHelper();
            String requestBodyContent = trySerializeRequestBodyContent(fileIds);
            StringEntity requestEntity = new StringEntity(
                    requestBodyContent,
                    ContentType.APPLICATION_JSON);

            HttpPut request = new HttpPut(url);
            request.setEntity(requestEntity);
            requestHelper.executeCommand(request, true, DigestUtils.md5Hex(requestBodyContent));
        } else {
            // Make batch requests.
            executeSaveDeleteBatchCommand(true, fileIds);
        }
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

        return requestHelper.executeQuery(
                request,
                true,
                CopyFileData.class,
                RequestHelper.getFormMD5(nameValuePairs));
    }

    private void executeSaveDeleteBatchCommand(boolean save, List<String> fileIds) {
        URI url = Urls.apiFilesBatch();

        for (int offset = 0; offset < fileIds.size(); offset += MAX_SAVE_DELETE_BATCH_SIZE) {
            int endIndex = offset + MAX_SAVE_DELETE_BATCH_SIZE;
            if (endIndex > fileIds.size()) {
                endIndex = fileIds.size();
            }

            HttpEntityEnclosingRequestBase request;
            if (save) {
                request = new HttpPut(url);
            } else {
                request = new HttpDeleteWithBody(url);
            }

            List<String> ids = fileIds.subList(offset, endIndex);
            String requestBodyContent = trySerializeRequestBodyContent(ids);
            StringEntity requestEntity = new StringEntity(
                    requestBodyContent,
                    ContentType.APPLICATION_JSON);
            request.setEntity(requestEntity);

            RequestHelper requestHelper = getRequestHelper();
            requestHelper.executeCommand(request, true, DigestUtils.md5Hex(requestBodyContent));
        }
    }

    private String trySerializeRequestBodyContent(Object object) {
        String requestBodyContent = null;
        try {
            requestBodyContent = getObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new UploadcareApiException("Error input arguments", e);
        }

        return requestBodyContent;
    }
}
