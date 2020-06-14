package com.uploadcare.api;

import com.uploadcare.data.FilePageData;
import com.uploadcare.urls.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
    private final HashMap<String, UrlParameter> parameters = new HashMap<String, UrlParameter>();

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
        parameters.put("removed", new FilesRemovedParameter(removed));
        return this;
    }

    /**
     * Adds a filter for stored files.
     *
     * @param stored If {@code true}, accepts stored files, otherwise declines them.
     */
    public FilesQueryBuilder stored(boolean stored) {
        parameters.put("stored", new FilesStoredParameter(stored));
        return this;
    }

    /**
     * Adds a filter for datetime from objects will be returned.
     * Order {@link com.uploadcare.urls.UrlParameter.Order#UPLOAD_TIME_ASC} will be used.
     *
     * @param fromDate A uploading datetime from which objects will be returned.
     */
    public FilesQueryBuilder from(Date fromDate) {
        parameters.put("ordering", new FilesOrderParameter(UrlParameter.Order.UPLOAD_TIME_ASC));
        parameters.put("from", new FilesFromParameter(fromDate));
        return this;
    }

    /**
     * Adds a filter for datetime from objects will be returned.
     * Order {@link com.uploadcare.urls.UrlParameter.Order#SIZE_ASC} will be used.
     *
     * @param fromSize File size in bytes.
     */
    public FilesQueryBuilder from(Long fromSize) {
        parameters.put("ordering", new FilesOrderParameter(UrlParameter.Order.SIZE_ASC));
        parameters.put("from", new FilesFromParameter(fromSize));
        return this;
    }

    /**
     * Adds a filter for datetime to which objects will be returned.
     * Order {@link com.uploadcare.urls.UrlParameter.Order#UPLOAD_TIME_DESC} will be used.
     *
     * @param toDate A uploading datetime to which objects will be returned.
     */
    public FilesQueryBuilder to(Date toDate) {
        parameters.put("ordering", new FilesOrderParameter(UrlParameter.Order.UPLOAD_TIME_DESC));
        parameters.put("from", new FilesFromParameter(toDate));
        return this;
    }

    /**
     * Adds a filter for datetime to which objects will be returned.
     * Order {@link com.uploadcare.urls.UrlParameter.Order#SIZE_DESC} will be used.
     *
     * @param toSize File size in bytes.
     */
    public FilesQueryBuilder to(Long toSize) {
        parameters.put("ordering", new FilesOrderParameter(UrlParameter.Order.SIZE_DESC));
        parameters.put("from", new FilesFromParameter(toSize));
        return this;
    }

    /**
     * Specifies the way files are sorted. This filter clears any from/to filters set before.
     *
     * @param order Order in which files are sorted in a returned list {@link com.uploadcare.urls.UrlParameter.Order}
     *
     * @see com.uploadcare.urls.UrlParameter.Order
     */
    public FilesQueryBuilder ordering(UrlParameter.Order order) {
        parameters.put("ordering", new FilesOrderParameter(order));
        parameters.remove("from");
        return this;
    }

    /**
     * Add special fields to the file object in the result.
     *
     * @param fields Example: "rekognition_info"
     */
    public FilesQueryBuilder addFields(String fields) {
        parameters.put("add_fields", new AddFieldsParameter(fields));
        return this;
    }

    public Iterable<File> asIterable() {
        URI url = Urls.apiFiles();
        RequestHelper requestHelper = client.getRequestHelper();
        FileDataWrapper dataWrapper = new FileDataWrapper(client);

        return requestHelper.executePaginatedQuery(
                url,
                parameters.values(),
                true,
                FilePageData.class,
                dataWrapper);
    }

    public List<File> asList() {
        List<File> files = new ArrayList<File>();
        for (File file : asIterable()) {
            files.add(file);
        }
        return files;
    }
}
