package com.uploadcare.upload;

public class UploadFailureException extends Exception {

    public UploadFailureException() {
        super("UploadFailureException");
    }

    public UploadFailureException(String message) {
        super(message);
    }

    public UploadFailureException(String message, Throwable cause) {
        super(message, cause);
    }

    public UploadFailureException(Throwable cause) {
        super(cause);
    }
}
