# uploadcare-java

[![Build Status](https://github.com/uploadcare/uploadcare-java/actions/workflows/build.yml/badge.svg?branch=master)](https://github.com/uploadcare/uploadcare-java/actions/workflows/build.yml)
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
[Maven Central](https://central.sonatype.com/artifact/com.uploadcare/uploadcare/).

Include following dependency into your project's `pom.xml`:

```xml
<dependency>
    <groupId>com.uploadcare</groupId>
    <artifactId>uploadcare</artifactId>
    <version>3.5.1</version>
</dependency>
```

## Gradle

Include following dependency into your project's `build.gradle`:

```
implementation 'com.uploadcare:uploadcare:3.5.1'
```

If you are using the kotlin style `build.gradle.kts`:

```
implementation("com.uploadcare:uploadcare:3.5.1")
```

## Examples

Get your [API keys](https://uploadcare.com/docs/start/settings/#keys) to proceed with
the examples below.

Read class documentation on [javadoc.io](https://www.javadoc.io/doc/com.uploadcare/uploadcare/latest/index.html).

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

The minimum requirements to build this project are:

1. Gradle 8.2 (you can use `./gradlew` or `.\gradlew.bat` which will download it for you)
2. JDK 1.8 (target is 1.7, so don't use much higher version)
3. Running `./gradlew build` should run successfully.

## Security issues

If you spotted or experienced any security implications while using Uploadcare
libraries, hit us up at [bugbounty@uploadcare.com](mailto:bugbounty@uploadcare.com)
or Hackerone. We'll contact you shortly to fix this security issue.
