uploadcare-java
===============

This is a Java library for Uploadcare.

Supported features:

- Complete file and account API v2
- Paginated resources fetched as `List<T>`
- CDN path builder
- File uploads from disk, byte array, and URL

## Examples

### Basic API Usage

```java
Client client = new Client("publickey", "privatekey");
Account account = client.getAccount();

List<URI> published = new ArrayList<URI>();
List<File> files = client.getFiles();
for (File file : files) {
    if (file.isMadePublic()) {
        published.add(file.getOriginalFileUrl());
    }
}
```

### Building CDN URLs

```java
File file = client.getFile("85b5644f-e692-4855-9db0-8c5a83096e25");
CdnPathBuilder builder = file.cdnPath()
        .resizeWidth(200)
        .cropCenter(200, 200)
        .grayscale();
URI url = Urls.cdn(builder);
```

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
