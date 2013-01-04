package com.uploadcare.api;

import com.uploadcare.data.DataWrapper;
import com.uploadcare.data.FileData;

public class FileDataWrapper implements DataWrapper<File, FileData> {

    private Client client;

    public FileDataWrapper(Client client) {
        this.client = client;
    }

    @Override
    public File wrap(FileData data) {
        return new File(client, data);
    }

}
