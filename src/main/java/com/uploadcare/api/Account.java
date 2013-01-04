package com.uploadcare.api;

import com.uploadcare.data.AccountData;

public class Account {

    private Client client;
    private AccountData accountData;

    public Account(Client client, AccountData accountData) {
        this.client = client;
        this.accountData = accountData;
    }

    public String getEmail() {
        return accountData.email;
    }

    public String getPubKey() {
        return accountData.pubKey;
    }
}
