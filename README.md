# uploadcare-java

[![Build Status](https://github.com/uploadcare/uploadcare-java/actions/workflows/maven.yml/badge.svg?branch=master)](https://github.com/uploadcare/uploadcare-java/actions/workflows/maven.yml)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.uploadcare/uploadcare/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.uploadcare/uploadcare)
[![Javadocs](https://www.javadoc.io/badge/com.uploadcare/uploadcare.svg)](https://www.javadoc.io/doc/com.uploadcare/uploadcare)
[![Uploadcare stack on StackShare][stack-img]][stack]

[stack-img]: http://img.shields.io/badge/tech-stack-0690fa.svg?style=flat
[stack]: https://stackshare.io/uploadcare/stacks/

This is a Java library for Uploadcare.

Supported features:

- Complete file and project APIs v0.6.
- Paginated resources are fetched as `List<T>`.
- CDN path builder.
- File uploading from a local storage, byte arrays, URLs, and signed uploads.

## Maven

The latest stable library version is available at
[Maven Central](https://search.maven.org/#search%7Cga%7C1%7Cuploadcare).

Include the following code into your build by adding the following dependencies
into `pom.xml` for your project.

```xml
<dependency>
    <groupId>com.uploadcare</groupId>
    <artifactId>uploadcare</artifactId>
    <version>3.4.0</version>
</dependency>
```

## Examples

Get your [API keys](https://uploadcare.com/docs/start/settings/#keys) to proceed with
the examples below.

Read full documentation on [javadoc.io](https://www.javadoc.io/doc/com.uploadcare/uploadcare/latest/index.html).

### Basic API Usage

```java
Client client = new Client("publickey", "secretkey");
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

## Contributors

See `AUTHORS.txt` and our [contributors](https://github.com/uploadcare/uploadcare-java/graphs/contributors).

## Security issues

If you spotted or experienced any security implications while using Uploadcare
libraries, hit us up at [bugbounty@uploadcare.com](mailto:bugbounty@uploadcare.com)
or Hackerone. We'll contact you shortly to fix this security issue.
