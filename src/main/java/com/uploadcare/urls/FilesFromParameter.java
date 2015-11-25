package com.uploadcare.urls;

import com.uploadcare.api.RequestHelper;

import java.util.Date;

public class FilesFromParameter implements UrlParameter {

    private final Date from;

    public FilesFromParameter(Date from) {
        this.from = from;
    }

    public String getParam() {
        return "from";
    }

    public String getValue() {
        return RequestHelper.iso8601(from);
    }
}
