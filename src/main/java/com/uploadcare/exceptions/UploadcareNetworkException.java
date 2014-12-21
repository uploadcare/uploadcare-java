package com.uploadcare.exceptions;

/**
 * An exception thrown in cases of network failures.
 *
 * Created by Yervand Aghababyan on 12/21/14.
 */
public class UploadcareNetworkException extends UploadcareApiException {
    public UploadcareNetworkException(Throwable cause) {
        super("Network failure!", cause);
    }
}
