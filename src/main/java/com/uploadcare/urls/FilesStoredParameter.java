package com.uploadcare.urls;

public class FilesStoredParameter implements UrlParameter {

    private final boolean stored;

    public FilesStoredParameter(boolean stored) {
        this.stored = stored;
    }

    @Override
    public String getParam() {
        return "stored";
    }

    @Override
    public String getValue() {
        return stored ? "true" : "false";
    }
}
