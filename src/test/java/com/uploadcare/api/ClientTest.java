package com.uploadcare.api;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ClientTest {

    @Test
    public void test_getAccount() {
        Client client = Client.demoClient();
        Account account = client.getAccount();
        assertEquals(client.getPublicKey(), account.getPubKey());
    }

}
