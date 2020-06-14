package com.uploadcare.urls;

public class AddFieldsParameter implements UrlParameter {

    private final String fields;

    public AddFieldsParameter(String fields) {
        this.fields = fields;
    }

    public String getParam() {
        return "add_fields";
    }

    public String getValue() {
        return fields;
    }
}
