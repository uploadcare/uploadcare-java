package com.uploadcare.data;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.uploadcare.api.CopyFile;
import com.uploadcare.api.JsonToStringDeserializer;

/**
 * Result of the file copy operation
 */
public class CopyFileData {

    public CopyFile.CopyFileType type;
    @JsonDeserialize(using = JsonToStringDeserializer.class)
    public String result;

    @Override
    public String toString() {
        return " type: " + (type == null ? "" : type) + ", result: " + (result == null ? "" : result);
    }

}
