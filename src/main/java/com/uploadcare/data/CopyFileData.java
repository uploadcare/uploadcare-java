package com.uploadcare.data;

public class CopyFileData {
	public String detail;
	public String type;
	public FileData result;
	
	@Override
	public String toString() {
		return "detail: " + (detail == null ? "" : detail) + ", type: " + (type == null ? "" : type) + ", result: " + (result == null ? "" : result.uuid);
	}
	
}
