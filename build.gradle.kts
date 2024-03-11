plugins {
    base
    `java-library`
    `maven-publish`
    signing
}

group = "com.uploadcare"


val isReleaseVersion = !version.toString().lowercase().endsWith("snapshot")

java {
    sourceCompatibility = JavaVersion.VERSION_1_7
    targetCompatibility = JavaVersion.VERSION_1_7
    withSourcesJar()
    withJavadocJar()
}

dependencies {
    implementation("org.apache.httpcomponents:httpclient:4.5.13")
    implementation("org.apache.httpcomponents:httpmime:4.5.13")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.2")
    implementation("commons-codec:commons-codec:1.10")
    implementation("commons-io:commons-io:2.7")
    implementation("com.sun.activation:javax.activation:1.2.0")

    testImplementation("junit:junit:4.13.1")
    testImplementation("org.mockito:mockito-all:1.10.19")
}

// Setup global publishing repository settings.
signing {
    useGpgCmd()
    sign(publishing.publications)
}

publishing {
    repositories {
        maven {
            // Dynamically select either Maven Central or na Internal repository depending on the value of uploadcare.publish.type / UPLOADCARE_PUBLISH_TYPE
            name = "selected"

            // Allow deploying to a custom repository (for testing purposes)
            val publishInternally = project.findProperty("uploadcare.publish.type")?.toString() == "internal";
            var repositoryUrl: String;

            if (isReleaseVersion) {
                val releaseMavenCentral = "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
                val releaseInternal = (project.findProperty("uploadcare.publish.internal.release") ?: System.getenv("UPLOADCARE_PUBLISH_INTERNAL_RELEASE") ?: "") as String
                repositoryUrl = if (releaseInternal != "" && publishInternally) releaseInternal else releaseMavenCentral
            } else {
                val snapshotMavenCentral = "https://oss.sonatype.org/content/repositories/snapshots/"
                val snapshotInternal = (project.findProperty("uploadcare.publish.internal.snapshot") ?: System.getenv("UPLOADCARE_PUBLISH_INTERNAL_SNAPSHOT") ?: "") as String
                repositoryUrl = if (snapshotInternal != "" && publishInternally) snapshotInternal else snapshotMavenCentral
            }

            url = uri(repositoryUrl)

            authentication {
                create<BasicAuthentication>("basic")
            }

            credentials {
                val mavenCentralUser = (project.findProperty("uploadcare.publish.sonatype.user") ?: System.getenv("UPLOADCARE_PUBLISH_SONATYPE_USER") ?: "") as String
                val mavenCentralPass = (project.findProperty("uploadcare.publish.sonatype.pass") ?: System.getenv("UPLOADCARE_PUBLISH_SONATYPE_PASS") ?: "") as String
                val internalUser = (project.findProperty("uploadcare.publish.internal.user") ?: System.getenv("UPLOADCARE_PUBLISH_INTERNAL_USER") ?: "") as String
                val internalPass = (project.findProperty("uploadcare.publish.internal.pass") ?: System.getenv("UPLOADCARE_PUBLISH_INTERNAL_PASS") ?: "") as String
                username = if (publishInternally) internalUser else mavenCentralUser
                password = if (publishInternally) internalPass else mavenCentralPass
            }
        }
    }
    publications {
        create<MavenPublication>("release") {
            from(components["java"])

            groupId = project.group as String?
            artifactId = "uploadcare"
            version = project.version as String?

            withBuildIdentifier()

            pom {
                name.set("uploadcare")
                url.set("https://github.com/uploadcare/uploadcare-java")
                description.set("Java client for the Uploadcare API. Uploadcare handles file uploads, storage and distribution for you, while you focus on other important things.")
                properties = mapOf(
                    "project.build.sourceEncoding" to "UTF-8",
                    "project.reporting.outputEncoding" to "UTF-8",
                    "maven.compiler.target" to "1.7",
                    "maven.compiler.source" to "1.7"
                )
                organization {
                    name.set("Uploadcare")
                    url.set("https://uploadcare.com")
                }
                issueManagement {
                    url.set("https://github.com/uploadcare/uploadcare-java/issues")
                }
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/uploadcare/uploadcare-java.git")
                    developerConnection.set("scm:git:ssh://github.com/uploadcare/uploadcare-java.git")
                    url.set("https://github.com/uploadcare/uploadcare-java")
                }
                developers {
                    developer {
                        name.set("Dimitry Solovyov")
                        id.set("dimituri")
                        email.set("dimituri@gmail.com")
                    }
                    developer {
                        name.set("Alexander Tchernin")
                        id.set("alchernin")
                        email.set("alchernin@gmail.com")
                    }
                    developer {
                        name.set("Yervand Aghababyan")
                        id.set("dnavre")
                        email.set("yervand.aghababyan@gmail.com")
                    }
                    developer {
                        name.set("Raphael Gilyazitdinov")
                        id.set("raphaelnew")
                        email.set("rafa@irafa.ru")
                    }
                    developer {
                        name.set("Artemis")
                        id.set("artemix")
                        email.set("artemix@artemix.org")
                    }
                }
            }
        }
    }
}

