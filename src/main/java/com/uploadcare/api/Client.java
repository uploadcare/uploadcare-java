package com.uploadcare.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.uploadcare.data.*;
import com.uploadcare.exceptions.UploadcareApiException;
import com.uploadcare.urls.*;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.TextUtils;

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
     * @param groupId Resource UUID
     * @return Group resource
     */
    public Group getGroup(String groupId) {
        URI url = Urls.apiGroup(groupId);
        RequestHelper requestHelper = getRequestHelper();
        GroupData groupData = requestHelper.executeQuery(new HttpGet(url), true, GroupData.class);
        return new Group(this, groupData);
    }

    /**
     * Request group info. Does not require "privatekey" set for UploadcareClient.
     *
     * @param groupId Resource UUID
     * @return Group resource
     */
    public Group getUploadedGroup(String groupId) {
        URI url = Urls.apiUploadedGroup(getPublicKey(), groupId);

        RequestHelper requestHelper = getRequestHelper();
        GroupData groupData = requestHelper.executeQuery(new HttpGet(url), false, GroupData.class);
        return new Group(this, groupData);
    }

    /**
     * Request file data for uploaded file. Does not require "privatekey" set for UploadcareClient.
     *
     * @param fileId Resource UUID
     * @return File resource
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
     * Requests file data, with Rekognition Info if available.
     *
     * @param fileId Resource UUID
     * @return File resource
     */
    public File getFileWithRekognitionInfo(String fileId) {
        URI url = Urls.getFileWithFields(fileId, "rekognition_info");
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
     * Requests Webhooks data.
     *
     * @return list of Webhooks resources.
     */
    public List<Webhook> getWebhooks() {
        List<Webhook> result = new ArrayList<Webhook>();
        URI url = Urls.apiWebhooks();

        WebhookDataWrapper dataWrapper = new WebhookDataWrapper(this);
        RequestHelper requestHelper = getRequestHelper();
        List<WebhookData> webhookDataList = requestHelper.executeQuery(
                new HttpGet(url),
                true,
                new TypeReference<ArrayList<WebhookData>>() {
                });
        for (WebhookData webhookData : webhookDataList) {
            result.add(dataWrapper.wrap(webhookData));
        }

        return result;
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
     * Unsubscribe and delete webhook.
     *
     * @param targetUrl A URL that is triggered by an event from Webhook.
     */
    public void deleteWebhook(URI targetUrl) {
        WebhookOptionsData tempWebhookData = new WebhookOptionsData();
        tempWebhookData.targetUrl = targetUrl;

        String requestBodyContent = trySerializeRequestBodyContent(tempWebhookData);
        StringEntity requestEntity = new StringEntity(
                requestBodyContent,
                ContentType.APPLICATION_JSON);

        URI url = Urls.apiDeleteWebhook();
        HttpDeleteWithBody request = new HttpDeleteWithBody(url);
        request.setEntity(requestEntity);

        RequestHelper requestHelper = getRequestHelper();
        requestHelper.executeCommand(request, true, DigestUtils.md5Hex(requestBodyContent));
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
     * Create files group from a set of files by using their UUIDs.
     *
     * @param fileIds  That parameter defines a set of files you want to join in a group.
     *
     * @return New created Group resource instance.
     */
    public Group createGroup(List<String> fileIds) {
        return createGroupSigned(fileIds, null, null, null);
    }

    /**
     * Create files group from a set of files by using their UUIDs.
     *
     * @param fileIds  That parameter defines a set of files you want to join in a group.
     * @param callback Sets the name of your JSONP callback function.
     *
     * @return New created Group resource instance.
     */
    public Group createGroup(List<String> fileIds, String callback) {
        return createGroupSigned(fileIds, callback, null, null);
    }

    /**
     * Create files group from a set of files by using their UUIDs. Using Signed Uploads.
     *
     * @param fileIds   That parameter defines a set of files you want to join in a group.
     * @param callback  Sets the name of your JSONP callback function.
     * @param signature is a string sent along with your upload request. It requires your Uploadcare
     *                  project secret key and hence should be crafted on your back end.
     * @param expire    sets the time until your signature is valid. It is a Unix time.(ex 1454902434)
     *
     * @return New created Group resource instance.
     */
    public Group createGroupSigned(List<String> fileIds, String callback, String signature, String expire) {
        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        entityBuilder.addTextBody("pub_key", getPublicKey());

        if (callback != null) {
            entityBuilder.addTextBody("callback", callback);
        }

        if (!TextUtils.isEmpty(signature) && !TextUtils.isEmpty(expire)) {
            entityBuilder.addTextBody("signature", signature);
            entityBuilder.addTextBody("expire", expire);
        }

        for (int i = 0; i < fileIds.size(); i++) {
            entityBuilder.addTextBody("files[" + i + "]", fileIds.get(i));
        }

        URI createUrl = Urls.apiCreateGroup();
        HttpPost createRequest = new HttpPost(createUrl);
        createRequest.setEntity(entityBuilder.build());
        GroupData groupData = getRequestHelper().executeQuery(createRequest, false, GroupData.class);

        return new Group(this, groupData);
    }

    /**
     * Create and subscribe to webhook.
     *
     * @param targetUrl A URL that is triggered by an event.
     * @param event An event you subscribe to. Only "file.uploaded" event supported.
     * @param isActive Marks a subscription as either active or not.
     * @return New created webhook resource instance.
     */
    public Webhook createWebhook(URI targetUrl, String event, boolean isActive) {
        WebhookOptionsData tempWebhookData = new WebhookOptionsData();
        tempWebhookData.targetUrl = targetUrl;
        tempWebhookData.event = event;
        tempWebhookData.isActive = isActive;

        String requestBodyContent = trySerializeRequestBodyContent(tempWebhookData);
        StringEntity requestEntity = new StringEntity(
                requestBodyContent,
                ContentType.APPLICATION_JSON);

        URI url = Urls.apiWebhooks();
        HttpPost request = new HttpPost(url);
        request.setEntity(requestEntity);

        RequestHelper requestHelper = getRequestHelper();
        WebhookData webhookData = requestHelper.executeQuery(
                request,
                true,
                WebhookData.class,
                DigestUtils.md5Hex(requestBodyContent));
        return new Webhook(this, webhookData);
    }

    /**
     * Update webhook attributes.
     *
     * @param webhookId Webhook id. If {@code null} then this field won't be updated.
     * @param targetUrl A URL that is triggered by an event. If {@code null} then this field won't be updated.
     * @param event     An event you subscribe to. Only "file.uploaded" event supported. If {@code null} then this field
     *                  won't be updated.
     * @param isActive  Marks a subscription as either active or not. If {@code null} then this field won't be updated.
     *
     * @return New webhook resource instance.
     */
    public Webhook updateWebhook(int webhookId, URI targetUrl, String event, Boolean isActive) {
        WebhookOptionsData tempWebhookData = new WebhookOptionsData();
        tempWebhookData.targetUrl = targetUrl;
        tempWebhookData.event = event;
        tempWebhookData.isActive = isActive;

        String requestBodyContent = trySerializeRequestBodyContent(tempWebhookData);
        StringEntity requestEntity = new StringEntity(
                requestBodyContent,
                ContentType.APPLICATION_JSON);

        URI url = Urls.apiWebhook(webhookId);
        HttpPut request = new HttpPut(url);
        request.setEntity(requestEntity);

        RequestHelper requestHelper = getRequestHelper();
        WebhookData webhookData = requestHelper.executeQuery(
                request,
                true,
                WebhookData.class,
                DigestUtils.md5Hex(requestBodyContent));
        return new Webhook(this, webhookData);
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
        requestHelper.executeCommand(new HttpPut(url), true);
    }

    /**
     * Mark all files in a group as stored.
     *
     * @param groupId Group Resource Id
     */
    public void saveGroup(String groupId) {
        URI url = Urls.apiGroupStorage(groupId);
        RequestHelper requestHelper = getRequestHelper();
        requestHelper.executeCommand(new HttpPut(url), true);
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
     * Copy file to local storage. Copy original files or their modified versions to default storage. Source files MAY
     * either be stored or just uploaded and MUST NOT be deleted.
     *
     * @param fileId     Resource UUID
     * @param store      The parameter only applies to the Uploadcare storage and MUST be either true or false.
     * @param makePublic Applicable to custom storage only. MUST be either true or false. true to make copied files
     *                   available via public links, false to reverse the behavior.
     *
     * @return An object containing the results of the copy request
     */
    public CopyFileData copyFileLocalStorage(String fileId, Boolean store, Boolean makePublic) {
        RequestHelper requestHelper = getRequestHelper();
        HttpPost request = new HttpPost(Urls.apiFileLocalCopy());

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("source", fileId));
        nameValuePairs.add(new BasicNameValuePair("store", store.toString()));
        nameValuePairs.add(new BasicNameValuePair("make_public", makePublic.toString()));

        try {
            request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        } catch (UnsupportedEncodingException e) {
            throw new UploadcareApiException("Illegal argument exception", e);
        }

        return requestHelper.executeQuery(
                request,
                true,
                CopyFileData.class,
                RequestHelper.getFormMD5(nameValuePairs));
    }

    /**
     * Copy file to remote storage. Copy original files or their modified versions to a custom storage. Source files
     * MAY either be stored or just uploaded and MUST NOT be deleted.
     *
     * @param fileId     Resource UUID
     * @param target     Identifies a custom storage name related to your project. Implies you are copying a file to a
     *                   specified custom storage. Keep in mind you can have multiple storages associated with a single
     *                   S3 bucket.
     * @param makePublic MUST be either true or false. true to make copied files available via public links, false to
     *                   reverse the behavior.
     * @param pattern    The parameter is used to specify file names Uploadcare passes to a custom storage. In case the
     *                   parameter is omitted, we use pattern of your custom storage. Use any combination of allowed
     *                   values.
     *
     * @return An object containing the results of the copy request
     */
    public CopyFileData copyFileRemoteStorage(String fileId, String target, Boolean makePublic, String pattern) {
        RequestHelper requestHelper = getRequestHelper();
        HttpPost request = new HttpPost(Urls.apiFileRemoteCopy());

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("source", fileId));
        nameValuePairs.add(new BasicNameValuePair("target", target));
        nameValuePairs.add(new BasicNameValuePair("make_public", makePublic.toString()));
        nameValuePairs.add(new BasicNameValuePair("pattern", pattern));

        try {
            request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        } catch (UnsupportedEncodingException e) {
            throw new UploadcareApiException("Illegal argument exception", e);
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
