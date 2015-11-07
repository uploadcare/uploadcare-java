package com.uploadcare.upload;

import com.uploadcare.api.File;

public interface Uploader {

    File upload() throws UploadFailureException;

    Uploader store(boolean store);
}
