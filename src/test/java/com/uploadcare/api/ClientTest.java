package com.uploadcare.api;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ClientTest {

    private Client client;

    @Before
    public void setUp() {
        client = Client.demoClient();
    }

    @Test
    public void test_getAccount() {
        Account account = client.getAccount();
        assertEquals(client.getPublicKey(), account.getPubKey());
    }

}
