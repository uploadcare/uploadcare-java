package com.uploadcare.api;

import com.uploadcare.data.WebhookData;

import java.net.URI;
import java.util.Date;

/**
 * The resource for Webhook.
 */
public class Webhook {

    private final Client client;
    private final WebhookData webhookData;

    public Webhook(Client client, WebhookData webhookData) {
        this.client = client;
        this.webhookData = webhookData;
    }

    public int getId() {
        return webhookData.id;
    }

    public String getEvent() {
        return webhookData.event;
    }

    public URI getTargetUrl() {
        return webhookData.targetUrl;
    }

    public int getProjectId() {
        return webhookData.project;
    }

    public boolean isActive() {
        return webhookData.isActive;
    }

    public Date getCreateDate() {
        return webhookData.created;
    }

    public Date getUpdateDate() {
        return webhookData.updated;
    }

    /**
     * Update webhook.
     *
     * @param targetUrl A URL that is triggered by an event.
     *
     * @return New webhook resource instance.
     */
    public Webhook update(URI targetUrl) {
        return client.updateWebhook(getId(), targetUrl, null, null);
    }

    /**
     * Update webhook.
     *
     * @param event An event you subscribe to. Only "file.uploaded" event supported.
     *
     * @return New webhook resource instance.
     */
    public Webhook update(String event) {
        return client.updateWebhook(getId(), null, event, null);
    }

    /**
     * Update webhook.
     *
     * @param isActive Marks a subscription as either active or not.
     *
     * @return New webhook resource instance.
     */
    public Webhook update(Boolean isActive) {
        return client.updateWebhook(getId(), null, null, isActive);
    }

    /**
     * Update webhook attributes.
     *
     * @param targetUrl A URL that is triggered by an event. If {@code null} then this field won't be updated.
     * @param event     An event you subscribe to. Only "file.uploaded" event supported. If {@code null} then this field
     *                  won't be updated.
     * @param isActive  Marks a subscription as either active or not. If {@code null} then this field won't be updated.
     *
     * @return New webhook resource instance.
     */
    public Webhook update(URI targetUrl, String event, Boolean isActive) {
        return client.updateWebhook(getId(), targetUrl, event, isActive);
    }

    /**
     * Unsubscribe and delete webhook.
     */
    public void delete() {
        client.deleteWebhook(getTargetUrl());
    }

    @Override
    public String toString() {
        return "Webhook{" +
                "webhookData=" + webhookData +
                '}';
    }
}
