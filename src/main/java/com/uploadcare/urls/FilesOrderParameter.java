package com.uploadcare.urls;

public class FilesOrderParameter implements UrlParameter {

    private final Order order;

    public FilesOrderParameter(Order order) {
        this.order = order;
    }

    @Override
    public String getParam() {
        return "ordering";
    }

    @Override
    public String getValue() {
        return order.toString();
    }
}
