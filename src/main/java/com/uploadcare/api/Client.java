package com.uploadcare.api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.uploadcare.data.AccountData;
import com.uploadcare.data.FileData;
import com.uploadcare.urls.Urls;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.net.URI;

public class Client {

    private final String publicKey;
    private final String privateKey;

    private final HttpClient httpClient = new DefaultHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Client(String publicKey, String privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;

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

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public Account getAccount() {
        URI url = Urls.apiAccount();
        Request request = new Request(this, new HttpGet(url));
        AccountData accountData = request.executeQuery(AccountData.class);
        return new Account(this, accountData);
    }

    public File getFile(String fileId) {
        URI url = Urls.apiFile(fileId);
        Request request = new Request(this, new HttpGet(url));
        FileData fileData = request.executeQuery(FileData.class);
        return new File(this, fileData);
    }

    public void deleteFile(String fileId) {
        URI url = Urls.apiFile(fileId);
        Request request = new Request(this, new HttpDelete(url));
        request.executeCommand();
    }

    public void saveFile(String fileId) {
        URI url = Urls.apiFileStorage(fileId);
        Request request = new Request(this, new HttpPost(url));
        request.executeCommand();
    }
}
