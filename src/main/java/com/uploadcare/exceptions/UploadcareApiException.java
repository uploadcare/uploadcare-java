package com.uploadcare.exceptions;

/**
 * A generic error thrown by the
 *
 * Created by Yervand Aghababyan on 12/21/14.
 */
public class UploadcareApiException extends RuntimeException {

    public UploadcareApiException(String message) {
        super(message);
    }

    public UploadcareApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public UploadcareApiException(Throwable cause) {
        super(cause);
    }
}
