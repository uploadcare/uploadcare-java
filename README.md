uploadcare-java
===============

[![Build Status](https://travis-ci.org/uploadcare/uploadcare-java.png?branch=master)](https://travis-ci.org/uploadcare/uploadcare-java)

This is a Java library for Uploadcare.

Supported features:

- Complete file and project API v2
- Paginated resources fetched as `List<T>`
- CDN path builder
- File uploads from disk, byte array, and URL

## Examples

### Basic API Usage

```java
Client client = new Client("publickey", "privatekey");
Project project = client.getProject();
Project.Collaborator owner = project.getOwner();

List<URI> published = new ArrayList<URI>();
Iterable<File> files = client.getFiles().asIterable();
for (File file : files) {
    if (file.isMadePublic()) {
        published.add(file.getOriginalFileUrl());
    }
}
```

See documentation for details:

* [Client](http://uploadcare.github.io/uploadcare-java/apidocs/com/uploadcare/api/Client.html)
* [File](http://uploadcare.github.io/uploadcare-java/apidocs/com/uploadcare/api/File.html)
* [Project](http://uploadcare.github.io/uploadcare-java/apidocs/com/uploadcare/api/Project.html)

### Building CDN URLs

```java
File file = client.getFile("85b5644f-e692-4855-9db0-8c5a83096e25");
CdnPathBuilder builder = file.cdnPath()
        .resizeWidth(200)
        .cropCenter(200, 200)
        .grayscale();
URI url = Urls.cdn(builder);
```

See documentation for details:

* [CdnPathBuilder](http://uploadcare.github.io/uploadcare-java/apidocs/com/uploadcare/urls/CdnPathBuilder.html)
* [Urls](http://uploadcare.github.io/uploadcare-java/apidocs/com/uploadcare/urls/Urls.html)

### File uploads

```java
Client client = Client.demoClient();
java.io.File file = new java.io.File("olympia.jpg");
Uploader uploader = new FileUploader(client, sourceFile);
try {
    File file = uploader.upload().save();
    System.out.println(file.getOriginalFileUrl());
} catch (UploadFailureException e) {
    System.out.println("Upload failed :(");
}
```

See documentation for details:

* [FileUploader](http://uploadcare.github.io/uploadcare-java/apidocs/com/uploadcare/upload/FileUploader.html)
* [UrlUploader](http://uploadcare.github.io/uploadcare-java/apidocs/com/uploadcare/upload/UrlUploader.html)
