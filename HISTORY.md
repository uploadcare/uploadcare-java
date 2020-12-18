# Changelog
All notable changes to this project will be documented in this file.

The format is based now on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## 3.3.1
### Updated
- Apache httpclient and httpmime dependencies to v4.5.13, fixes network related issues.
### Fixed
- Signature for requests with query parameters was incorrect.
- Possible issue with using default timeout values from machine that running UploadcareClient. Set custom timeouts for connections, sockets.

## 3.3.0
### Added
- Support Uploadcare REST API v0.6
- File: Add new fields
- Group: get list of groups with filter params. Create/Store group.
- Add support for batch store/delete calls for Files
- Add ability to use Client without "secret key" for: uploading file/get uploaded file info/creating group/get created group info.
- Add ability to do "Signed Uploads" when using FileUploader/UrlUploader/Create Group.
- Add ability to copy files in local/remote storage. Remove old copy file method.
- Add multipart upload support for FileUploader.
- Add Webhooks support. Get Webhooks, Create/Update/Delete Webhook is supported.
- Update UrlUploader, add ability to specify additional params.

### Changed
- Renamed "privateKey" variable to "secretKey".
- Default Client auth method now is HMAC-based instead of Simple Auth.

### Fixed
- Uploaded files now has proper MimeType.
- "InputStream" content uploading.
- UrlUploader correctly handle "progress", "waiting", "unknown" statuses of a file uploaded from URL. Do exponential backoff and throw error instead of polling Upload API server indefinitely.

## 3.2.0
### Added
- Support for uploading files using `InputStream`

### Changed
- Build matrix in Travis
- Switched to Semantic Versioning

## 3.1
This is a technical version.
For some reason version 3.0 uploaded to Sonatype was broken and used old code.


## 3.0
### Added
- Image Operations to `CdnPathBuilder`: blur, sharp, preview, format, quality.

### Fixed
- Threading problem with HttpClient.

### Changed
- Support Uploadcare REST API v0.4
- Updated some of deprecated classes.


## 2.0
### Changed
- Support Uploadcare REST API v0.3
- Improve error handling

## 1.0
- Initial release
- Supports Uploadcare REST API v0.2
