package com.uploadcare.urls;

import com.uploadcare.api.RequestHelper;

import java.util.Date;

public class FilesFromParameter implements UrlParameter {

    private final Date fromDate;
    private final Long fromSize;

    public FilesFromParameter(Date fromDate) {
        this.fromDate = fromDate;
        this.fromSize = null;
    }

    public FilesFromParameter(Long fromSize) {
        this.fromSize = fromSize;
        this.fromDate = null;
    }

    public String getParam() {
        return "from";
    }

    public String getValue() {
        if (fromDate != null) {
            return RequestHelper.iso8601(fromDate);
        } else {
            return String.valueOf(fromSize);
        }
    }
}
