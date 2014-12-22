package com.uploadcare.data;

/**
 * Result of the file copy call
 *
 */
public class CopyFileData {
	public String detail;
	public String type;
	public FileData result;

	@Override
	public String toString() {
		return "detail: " + (detail == null ? "" : detail) + ", type: " + (type == null ? "" : type) + ", result: " + (result == null ? "" : result.uuid);
	}
	
}
