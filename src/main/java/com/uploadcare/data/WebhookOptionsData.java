package com.uploadcare.data;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.net.URI;

public class WebhookOptionsData {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String event;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public URI targetUrl;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Boolean isActive;

}