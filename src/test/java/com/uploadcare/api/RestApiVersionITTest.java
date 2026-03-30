package com.uploadcare.api;

import com.uploadcare.upload.FileUploader;
import com.uploadcare.urls.Urls;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Integration tests for REST API v0.7 support.
 *
 * <p>These tests connect to the live Uploadcare API and require real credentials provided via
 * environment variables:
 * <ul>
 *   <li>{@code UPLOADCARE_PUBLIC_KEY} – The Uploadcare project public key</li>
 *   <li>{@code UPLOADCARE_SECRET_KEY} – The Uploadcare project secret key</li>
 * </ul>
 *
 * <p>Run with:
 * <pre>
 *   UPLOADCARE_PUBLIC_KEY=&lt;pub&gt; UPLOADCARE_SECRET_KEY=&lt;sec&gt; ./gradlew integrationTest
 * </pre>
 *
 * <p>When the environment variables are absent, the tests are automatically skipped.
 */
public class RestApiVersionITTest
{

    private static final String PUBLIC_KEY = System.getenv("UPLOADCARE_PUBLIC_KEY");

    private static final String SECRET_KEY = System.getenv("UPLOADCARE_SECRET_KEY");


    @Before
    public void requireCredentials() {
        Assume.assumeTrue("UPLOADCARE_PUBLIC_KEY environment variable must be set to run integration tests",
                          PUBLIC_KEY != null && !PUBLIC_KEY.isEmpty());
        Assume.assumeTrue("UPLOADCARE_SECRET_KEY environment variable must be set to run integration tests",
                          SECRET_KEY != null && !SECRET_KEY.isEmpty());
    }

    // -------------------------------------------------------------------------
    // Bug reproduction
    // -------------------------------------------------------------------------

    /**
     * Reproduces the bug: using {@code Accept: application/vnd.uploadcare-v0.6+json} causes the
     * Uploadcare API to include a {@code Warning} response header asking clients to migrate to
     * the latest version.
     *
     * <p>Before the fix the Java client sent the v0.6 Accept header on every request, which
     * silently received this deprecation warning without surfacing it to the caller.
     *
     * <p>Uses the {@code /files/} endpoint which is not itself deprecated, so the Warning header
     * is only present when the API version is outdated (not due to endpoint deprecation).
     */
    @Test
    public void test_v06AcceptHeader_triggersDeprecationWarning() throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            HttpGet request = buildRequest(Urls.apiFiles(), "application/vnd.uploadcare-v0.6+json");

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                Header warningHeader = response.getFirstHeader("Warning");
                assertNotNull(
                        "BUG REPRODUCTION: Expected a Warning response header when using the "
                                + "deprecated v0.6 Accept header, but none was found.\n"
                                + "All response headers received: "
                                + Arrays.toString(response.getAllHeaders()),
                        warningHeader);
                assertTrue(
                        "BUG REPRODUCTION: The Warning header should mention API version deprecation "
                                + "(e.g. 'not 0.6'), but was: " + warningHeader.getValue(),
                        warningHeader.getValue().contains("0.6"));
            }
        } finally {
            httpClient.close();
        }
    }

    // -------------------------------------------------------------------------
    // Fix verification – header level
    // -------------------------------------------------------------------------

    /**
     * Verifies the fix at the HTTP layer: using {@code Accept: application/vnd.uploadcare-v0.7+json}
     * produces no API-version deprecation {@code Warning} response header.
     *
     * <p>Uses the {@code /files/} endpoint (not the deprecated {@code /project/} endpoint) so
     * that any Warning header present is only about the API version, not endpoint deprecation.
     * With v0.7, the API returns at most a generic notice (e.g. demo-project), but never a
     * warning that says "not 0.6" or asks the client to upgrade.
     */
    @Test
    public void test_v07AcceptHeader_noDeprecationWarning() throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            HttpGet request = buildRequest(Urls.apiFiles(), "application/vnd.uploadcare-v0.7+json");

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                Header warningHeader = response.getFirstHeader("Warning");
                // A Warning header is acceptable as long as it is NOT an API-version deprecation
                // warning (e.g. demo-project notices are fine, but "not 0.6" / "API version"
                // upgrade messages must not appear when the client already uses v0.7).
                if (warningHeader != null) {
                    assertFalse("FIX VERIFICATION: No API-version deprecation Warning should be returned " +
                                "when using the v0.7 Accept header, but one was found: " +
                                warningHeader.getValue() + "\n" +
                                "All response headers: " + Arrays.toString(response.getAllHeaders()),
                                warningHeader.getValue().contains("0.6"));
                }
            }
        } finally {
            httpClient.close();
        }
    }

    // -------------------------------------------------------------------------
    // Fix verification – client + model level
    // -------------------------------------------------------------------------

    /**
     * Verifies the fix end-to-end using the Uploadcare Java client:
     * <ol>
     *   <li>Uploads a real image via the Upload API.</li>
     *   <li>Retrieves the file metadata via the REST API (which now sends a v0.7 Accept header).</li>
     *   <li>Asserts that the v0.7-specific {@code content_info} field is present and correctly
     *       populated in the response, proving the client both sends the right version header
     *       and correctly deserializes the new response shape.</li>
     * </ol>
     */
    @Test
    public void test_client_uploadsFile_receivesV07ContentInfo() throws Exception {
        Client client = new Client(PUBLIC_KEY, SECRET_KEY, true, null);

        InputStream imageStream = getClass().getClassLoader().getResourceAsStream("olympia.jpg");

        assertNotNull("Test resource olympia.jpg must be on the classpath", imageStream);

        byte[] imageBytes = IOUtils.toByteArray(imageStream);

        File uploadedFile = new FileUploader(client, imageBytes, "olympia.jpg").upload();
        String fileId = uploadedFile.getFileId();

        assertNotNull("Uploaded file must have a UUID", fileId);

        try {
            // getFile() uses the REST API with the v0.7 Accept header
            File file = client.getFile(fileId);

            // v0.7 response always includes content_info
            File.ContentInfo contentInfo = file.getContentInfo();

            assertNotNull("FIX VERIFICATION: content_info must be present in the v0.7 API response", contentInfo);
            assertNotNull("FIX VERIFICATION: content_info.mime must be present", contentInfo.mime);
            assertNotNull("FIX VERIFICATION: content_info.image must be present for an image file", contentInfo.image);

            assertEquals("FIX VERIFICATION: MIME type should be image/jpeg", "image/jpeg", contentInfo.mime.mime);
            assertEquals("FIX VERIFICATION: MIME type/type should be 'image'", "image", contentInfo.mime.type);
        } finally {
            // Best-effort clean up: ignore errors (e.g. demo projects may not allow DELETE)
            try {
                client.deleteFile(fileId);
            } catch (Exception ignored) {
            }
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * Builds a GET request to the given {@code url} with the given {@code Accept} header value,
     * using simple authentication (public_key:secret_key in plain text).  This helper exists so
     * the two header-level tests can forge any Accept header value without going through the
     * main {@link RequestHelper}, whose Accept header is now hard-coded to v0.7.
     */
    private HttpGet buildRequest(URI url, String acceptHeader) {
        HttpGet request = new HttpGet(url);
        request.setHeader("Accept", acceptHeader);
        request.setHeader("Date", RequestHelper.rfc2822(new Date()));
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Authorization", "Uploadcare.Simple " + PUBLIC_KEY + ":" + SECRET_KEY);

        return request;
    }

}
