# uploadcare-java

[![Build Status](https://travis-ci.org/uploadcare/uploadcare-java.png?branch=master)](https://travis-ci.org/uploadcare/uploadcare-java)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.uploadcare/uploadcare/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.uploadcare/uploadcare)
[![Javadoc](https://javadoc-emblem.rhcloud.com/doc/com.uploadcare/uploadcare/badge.svg)](http://www.javadoc.io/doc/com.uploadcare/uploadcare)

This is a Java library for Uploadcare.

Supported features:

- Complete file and project APIs v0.4
- Paginated resources are fetched as `List<T>`
- CDN path builder
- File uploads from local storages, byte arrays or URLs

## Maven

The latest stable version of the lib is available from
[Maven Central](https://search.maven.org/#search%7Cga%7C1%7Cuploadcare).

Including it in your build is done through adding the
following to the `<dependencies />` section of your
project's `pom.xml` file.

```xml
<dependency>
    <groupId>com.uploadcare</groupId>
    <artifactId>uploadcare</artifactId>
    <version>3.1</version>
</dependency>
```

## Examples

Don't forget to [grab](https://uploadcare.com/documentation/keys/)
your API keys prior to trying out the examples.

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

Check out the docs for details:

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

Check out the docs for details:

* [CdnPathBuilder](http://uploadcare.github.io/uploadcare-java/apidocs/com/uploadcare/urls/CdnPathBuilder.html)
* [Urls](http://uploadcare.github.io/uploadcare-java/apidocs/com/uploadcare/urls/Urls.html)

### File uploads

```java
Client client = Client.demoClient();
java.io.File file = new java.io.File("olympia.jpg");
Uploader uploader = new FileUploader(client, file);
try {
    File file = uploader.upload().save();
    System.out.println(file.getOriginalFileUrl());
} catch (UploadFailureException e) {
    System.out.println("Upload failed :(");
}
```

Check out the docs for details:

* [FileUploader](http://uploadcare.github.io/uploadcare-java/apidocs/com/uploadcare/upload/FileUploader.html)
* [UrlUploader](http://uploadcare.github.io/uploadcare-java/apidocs/com/uploadcare/upload/UrlUploader.html)

## Contributors

- [@disolovyov](https://github.com/disolovyov)
- [@raphaelnew](https://github.com/raphaelnew)
- [@dmitry-mukhin](https://github.com/dmitry-mukhin)
- [@dnavre](https://github.com/dnavre)
- [@alchernin](https://github.com/alchernin)
- [@joeyvmason](https://github.com/joeyvmason)

## Security issues

If you think you ran into something in Uploadcare libraries
which might have security implications, please hit us up at
[bugbounty@uploadcare.com](mailto:bugbounty@uploadcare.com)
or Hackerone.

We'll contact you personally in a short time to fix an issue
through co-op and prior to any public disclosure.
