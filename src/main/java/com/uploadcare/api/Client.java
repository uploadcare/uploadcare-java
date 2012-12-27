package com.uploadcare.api;

import com.google.api.client.http.HttpMethods;
import com.uploadcare.json.File;

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
        Url url = Url.filesUrl(fileId);
        Request request = new Request(this, HttpMethods.GET, url);
        return request.executeQuery(File.class);
    }

    public void deleteFile(File file) {
        deleteFile(file.fileId);
    }

    public void deleteFile(String fileId) {
        Url url = Url.filesUrl(fileId);
        Request request = new Request(this, HttpMethods.DELETE, url);
        request.executeCommand();
    }

    public void saveFile(File file) {
        saveFile(file.fileId);
    }

    public void saveFile(String fileId) {
        Url url = Url.filesUrl(fileId, true);
        Request request = new Request(this, HttpMethods.POST, url);
        request.executeCommand();
    }
}
