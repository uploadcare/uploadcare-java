package com.uploadcare.api;

public class Client {

    private String publicKey;
    private String privateKey;

    public Client(String publicKey, String privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public static Client demoClient() {
        return new Client("demopublickey", "demoprivatekey");
    }

}
