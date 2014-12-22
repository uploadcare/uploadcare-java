package com.uploadcare.exceptions;

/**
 * An exception thrown in cases of network failure.
 */
public class UploadcareNetworkException extends UploadcareApiException {
    public UploadcareNetworkException(Throwable cause) {
        super("Network failure!", cause);
    }
}
