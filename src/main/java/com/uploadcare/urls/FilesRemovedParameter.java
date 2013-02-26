package com.uploadcare.urls;

public class FilesRemovedParameter implements UrlParameter {

    private final boolean removed;

    public FilesRemovedParameter(boolean removed) {
        this.removed = removed;
    }

    @Override
    public String getParam() {
        return "removed";
    }

    @Override
    public String getValue() {
        return removed ? "true" : "false";
    }
}
