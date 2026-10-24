plugins {
    base
    `java-library`
    `maven-publish`
    signing
}

group = "com.uploadcare"


val isReleaseVersion = !version.toString().lowercase().endsWith("snapshot")

// The library targets Java 7. JDK 17 is the newest LTS JDK that can still compile for Java 7,
// and JUnit 6 needs Java 17 to run the tests.
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
    withSourcesJar()
    withJavadocJar()
}

// Run the tests on another JDK with -PtestJavaVersion=<version> (for example 21).
val testJavaVersion = (project.findProperty("testJavaVersion") ?: "17").toString()

// Mockito 5 attaches an agent to the test JVM. Load it at JVM start, as the Mockito
// documentation advises, so newer JDKs do not warn about dynamic agent loading.
val mockitoAgent = configurations.create("mockitoAgent")

dependencies {
    implementation("org.apache.httpcomponents:httpclient:4.5.14")
    implementation("org.apache.httpcomponents:httpmime:4.5.14")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.21.8")
    implementation("commons-codec:commons-codec:1.23.0")
    implementation("commons-io:commons-io:2.22.0")

    testImplementation(platform("org.junit:junit-bom:6.1.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.mockito:mockito-core:5.24.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.24.0")
    mockitoAgent("org.mockito:mockito-core:5.24.0") { isTransitive = false }
}

tasks {
    compileJava {
        options.release.set(7)
        // JDK 17 warns that release 7 is obsolete.
        options.compilerArgs.add("-Xlint:-options")
    }
    compileTestJava {
        options.release.set(17)
    }
    test {
        useJUnitPlatform()
        jvmArgs("-javaagent:${mockitoAgent.asPath}")
        javaLauncher.set(project.javaToolchains.launcherFor {
            languageVersion.set(JavaLanguageVersion.of(testJavaVersion))
        })
    }
}

// Setup global publishing repository settings.
signing {
    setRequired({ isReleaseVersion })
    useGpgCmd()
    sign(publishing.publications)
}

// Skip signing entirely for non-release (snapshot) builds.
tasks {
    withType<Sign>().configureEach {
        onlyIf { isReleaseVersion }
    }
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/uploadcare/uploadcare-java")
            credentials {
                username = System.getenv("GITHUB_ACTOR") ?: ""
                password = System.getenv("GITHUB_TOKEN") ?: ""
            }
        }
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

