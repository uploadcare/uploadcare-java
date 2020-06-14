package com.uploadcare.urls;

import com.uploadcare.api.RequestHelper;

import java.util.Date;

@Deprecated
public class FilesToParameter implements UrlParameter {

    private final Date to;

    public FilesToParameter(Date to) {
        this.to = to;
    }

    public String getParam() {
        return "to";
    }

    public String getValue() {
        return RequestHelper.iso8601(to);
    }
}