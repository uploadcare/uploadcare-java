package com.uploadcare.api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.uploadcare.data.AccountData;
import com.uploadcare.data.FileData;
import com.uploadcare.data.FilePageData;
import com.uploadcare.urls.Urls;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.net.URI;
import java.util.List;

public class Client {

    private final String publicKey;
    private final String privateKey;
    private final boolean simpleAuth;

    private final HttpClient httpClient = new DefaultHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Client(String publicKey, String privateKey) {
        this(publicKey, privateKey, true);
    }

    public Client(String publicKey, String privateKey, boolean simpleAuth) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.simpleAuth = simpleAuth;

        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    public static Client demoClient() {
        return new Client("demopublickey", "demoprivatekey");
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public boolean isSimpleAuth() {
        return simpleAuth;
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public Account getAccount() {
        URI url = Urls.apiAccount();
        RequestHelper requestHelper = new RequestHelper(this);
        AccountData accountData = requestHelper.executeQuery(new HttpGet(url), true, AccountData.class);
        return new Account(this, accountData);
    }

    public File getFile(String fileId) {
        URI url = Urls.apiFile(fileId);
        RequestHelper requestHelper = new RequestHelper(this);
        FileData fileData = requestHelper.executeQuery(new HttpGet(url), true, FileData.class);
        return new File(this, fileData);
    }

    public List<File> getFiles() {
        URI url = Urls.apiFiles();
        RequestHelper requestHelper = new RequestHelper(this);
        FileDataWrapper dataWrapper = new FileDataWrapper(this);
        return requestHelper.executePaginatedQuery(url, true, FilePageData.class, dataWrapper);
    }

    public void deleteFile(String fileId) {
        URI url = Urls.apiFile(fileId);
        RequestHelper requestHelper = new RequestHelper(this);
        requestHelper.executeCommand(new HttpDelete(url), true);
    }

    public void saveFile(String fileId) {
        URI url = Urls.apiFileStorage(fileId);
        RequestHelper requestHelper = new RequestHelper(this);
        requestHelper.executeCommand(new HttpPost(url), true);
    }
}
