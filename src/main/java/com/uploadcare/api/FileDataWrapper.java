package com.uploadcare.api;

import com.uploadcare.data.DataWrapper;
import com.uploadcare.data.FileData;

public class FileDataWrapper implements DataWrapper<File, FileData> {

    private final Client client;

    public FileDataWrapper(Client client) {
        this.client = client;
    }

    public File wrap(FileData data) {
        return new File(client, data);
    }

}
