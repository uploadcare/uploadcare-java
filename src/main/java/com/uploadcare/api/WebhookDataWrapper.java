package com.uploadcare.api;

import com.uploadcare.data.DataWrapper;
import com.uploadcare.data.WebhookData;

public class WebhookDataWrapper implements DataWrapper<Webhook, WebhookData> {

    private final Client client;

    public WebhookDataWrapper(Client client) {
        this.client = client;
    }

    @Override
    public Webhook wrap(WebhookData data) {
        return new Webhook(client, data);
    }
}