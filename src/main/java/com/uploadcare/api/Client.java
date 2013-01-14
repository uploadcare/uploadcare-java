package com.uploadcare.api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.uploadcare.data.AccountData;
import com.uploadcare.data.FileData;
import com.uploadcare.data.FilePageData;
import com.uploadcare.urls.Urls;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;

import java.net.URI;
import java.util.List;

/**
 * Uploadcare API client.
 *
 * Provides simple access to {@code File} and {@code Account} resources.
 *
 * @see com.uploadcare.api.File
 * @see com.uploadcare.api.Account
 */
public class Client {

    private final String publicKey;
    private final String privateKey;
    private final boolean simpleAuth;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final RequestHelperProvider requestHelperProvider;

    /**
     * Initializes a client with custom access keys and simple authentication.
     *
     * @param publicKey Public key
     * @param privateKey Private key
     */
    public Client(String publicKey, String privateKey) {
        this(publicKey, privateKey, true, null);
    }

    /**
     * Initializes a client with custom access keys.
     * Can use simple or secure authentication.
     *
     * @param publicKey Public key
     * @param privateKey Private key
     * @param simpleAuth If {@code false}, HMAC-based authentication is used
     * @param requestHelperProvider Should be {@code null} to use {@link DefaultRequestHelperProvider}
     */
    public Client(
            String publicKey,
            String privateKey,
            boolean simpleAuth,
            RequestHelperProvider requestHelperProvider) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.simpleAuth = simpleAuth;

        if (requestHelperProvider != null) {
            this.requestHelperProvider = requestHelperProvider;
            httpClient = new DefaultHttpClient(new PoolingClientConnectionManager());
            objectMapper = new ObjectMapper();
            objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
            objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        } else {
            this.requestHelperProvider = new DefaultRequestHelperProvider();
            httpClient = null;
            objectMapper = null;
        }
    }

    /**
     * Creates a client with demo credentials.
     * Useful for tests and anonymous access.
     *
     * <b>Warning!</b> Do not use in production.
     * All demo account files are eventually purged.
     *
     * @return A demo client
     */
    public static Client demoClient() {
        return new Client("demopublickey", "demoprivatekey");
    }

    /**
     * Returns the public key.
     *
     * @return Public key
     */
    public String getPublicKey() {
        return publicKey;
    }

    /**
     * Returns the private key.
     *
     * @return Private key
     */
    public String getPrivateKey() {
        return privateKey;
    }

    /**
     * Returns {@code true}, if simple authentication is used.
     *
     * @return {@code true}, if simple authentication is used, {@code false} otherwise
     */
    public boolean isSimpleAuth() {
        return simpleAuth;
    }

    HttpClient getHttpClient() {
        return httpClient;
    }

    ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public RequestHelper getRequestHelper() {
        return requestHelperProvider.get(this);
    }

    /**
     * Requests account data from the API.
     *
     * @return Account resource
     */
    public Account getAccount() {
        URI url = Urls.apiAccount();
        RequestHelper requestHelper = getRequestHelper();
        AccountData accountData = requestHelper.executeQuery(new HttpGet(url), true, AccountData.class);
        return new Account(this, accountData);
    }

    /**
     * Requests file data.
     *
     * @param fileId Resource UUID
     * @return File resource
     */
    public File getFile(String fileId) {
        URI url = Urls.apiFile(fileId);
        RequestHelper requestHelper = getRequestHelper();
        FileData fileData = requestHelper.executeQuery(new HttpGet(url), true, FileData.class);
        return new File(this, fileData);
    }

    /**
     * Requests all uploaded files for the current account.
     *
     * @return File resource list
     */
    public List<File> getFiles() {
        URI url = Urls.apiFiles();
        RequestHelper requestHelper = getRequestHelper();
        FileDataWrapper dataWrapper = new FileDataWrapper(this);
        return requestHelper.executePaginatedQuery(url, true, FilePageData.class, dataWrapper);
    }

    /**
     * Marks a file as deleted.
     *
     * @param fileId Resource UUID
     */
    public void deleteFile(String fileId) {
        URI url = Urls.apiFile(fileId);
        RequestHelper requestHelper = getRequestHelper();
        requestHelper.executeCommand(new HttpDelete(url), true);
    }

    /**
     * Marks a file as saved.
     *
     * This has to be done for all files you want to keep.
     * Unsaved files are eventually purged.
     *
     * @param fileId Resource UUID
     */
    public void saveFile(String fileId) {
        URI url = Urls.apiFileStorage(fileId);
        RequestHelper requestHelper = getRequestHelper();
        requestHelper.executeCommand(new HttpPost(url), true);
    }
}
