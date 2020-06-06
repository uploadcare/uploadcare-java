package com.uploadcare.data;

import java.net.URI;
import java.util.Date;

public class WebhookData {

    public int id;
    public String event;
    public URI targetUrl;
    public int project;
    public boolean isActive;
    public Date created;
    public Date updated;

    @Override
    public String toString() {
        return "WebhookData{" +
                "id=" + id +
                ", event='" + event + '\'' +
                ", targetUrl=" + targetUrl +
                ", project=" + project +
                ", isActive=" + isActive +
                ", created=" + created +
                ", updated=" + updated +
                '}';
    }
}
