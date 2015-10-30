package com.uploadcare.api;

import com.uploadcare.data.FilePageData;
import com.uploadcare.urls.FilesFromParameter;
import com.uploadcare.urls.FilesLimitParameter;
import com.uploadcare.urls.FilesRemovedParameter;
import com.uploadcare.urls.FilesStoredParameter;
import com.uploadcare.urls.FilesToParameter;
import com.uploadcare.urls.UrlParameter;
import com.uploadcare.urls.Urls;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * File resource request builder.
 *
 * Allows to specify some file filters and get results.
 *
 * @see com.uploadcare.api.File
 */
public class FilesQueryBuilder implements PaginatedQueryBuilder<File> {

    private final Client client;
    private final List<UrlParameter> parameters = new ArrayList<UrlParameter>();

    /**
     * Initializes a new builder for the given client.
     */
    public FilesQueryBuilder(Client client) {
        this.client = client;
    }

    /**
     * Adds a filter for removed files.
     *
     * @param removed If {@code true}, accepts removed files, otherwise declines them.
     */
    public FilesQueryBuilder removed(boolean removed) {
        parameters.add(new FilesRemovedParameter(removed));
        return this;
    }

    /**
     * Adds a filter for stored files.
     *
     * @param stored If {@code true}, accepts stored files, otherwise declines them.
     */
    public FilesQueryBuilder stored(boolean stored) {
        parameters.add(new FilesStoredParameter(stored));
        return this;
    }

    /**
     * Adds a filter for amount of files in one response.
     *
     * @param limit Amount of files in response. Default is 100, maximum is 1000.
     */
    public FilesQueryBuilder limit(int limit) {
        parameters.add(new FilesLimitParameter(limit));
        return this;
    }

    /**
     * Adds a filter for datetime from objects will be returned.
     *
     * @param from A uploading datetime from which objects will be returned.
     */
    public FilesQueryBuilder from(Date from) {
        parameters.add(new FilesFromParameter(from));
        return this;
    }

    /**
     * Adds a filter for datetime to which objects will be returned.
     *
     * @param to A uploading datetime to which objects will be returned.
     */
    public FilesQueryBuilder to(Date to) {
        parameters.add(new FilesToParameter(to));
        return this;
    }

    public Iterable<File> asIterable() {
        URI url = Urls.apiFiles();
        RequestHelper requestHelper = client.getRequestHelper();
        FileDataWrapper dataWrapper = new FileDataWrapper(client);
        return requestHelper.executePaginatedQuery(url, parameters, true, FilePageData.class, dataWrapper);
    }

    public List<File> asList() {
        List<File> files = new ArrayList<File>();
        for (File file : asIterable()) {
            files.add(file);
        }
        return files;
    }
}
