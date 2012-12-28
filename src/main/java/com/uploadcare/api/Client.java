package com.uploadcare.api;

import com.google.api.client.http.HttpMethods;
import com.uploadcare.data.FileData;
import com.uploadcare.urls.ApiFileStorageUrl;
import com.uploadcare.urls.ApiFileUrl;
import com.uploadcare.urls.ApiUrl;

public class Client {

    private final String publicKey;
    private final String privateKey;

    public Client(String publicKey, String privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
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

    public File getFile(String fileId) {
        ApiFileUrl url = new ApiFileUrl(fileId);
        Request request = new Request(this, HttpMethods.GET, url);
        FileData fileBean = request.executeQuery(FileData.class);
        return new File(this, fileBean);
    }

    public void deleteFile(String fileId) {
        ApiFileUrl url = new ApiFileUrl(fileId);
        Request request = new Request(this, HttpMethods.DELETE, url);
        request.executeCommand();
    }

    public void saveFile(String fileId) {
        ApiFileStorageUrl url = new ApiFileStorageUrl(fileId);
        Request request = new Request(this, HttpMethods.POST, url);
        request.executeCommand();
    }
}
