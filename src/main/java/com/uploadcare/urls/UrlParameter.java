package com.uploadcare.urls;

public interface UrlParameter {
    String getParam();

    String getValue();

    public enum Order {
        UPLOAD_TIME_ASC {
            public String toString() {
                return "datetime_uploaded";
            }
        },
        UPLOAD_TIME_DESC {
            public String toString() {
                return "-datetime_uploaded";
            }
        },
        SIZE_ASC {
            public String toString() {
                return "size";
            }
        },
        SIZE_DESC {
            public String toString() {
                return "-size";
            }
        }
    }
}
