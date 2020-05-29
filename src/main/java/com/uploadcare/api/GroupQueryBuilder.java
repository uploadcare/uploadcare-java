package com.uploadcare.api;

import com.uploadcare.data.GroupPageData;
import com.uploadcare.urls.FilesFromParameter;
import com.uploadcare.urls.FilesOrderParameter;
import com.uploadcare.urls.UrlParameter;
import com.uploadcare.urls.UrlParameter.Order;
import com.uploadcare.urls.Urls;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Group resource request builder.
 * <p>
 * Allows to specify some group filters and get results.
 *
 * @see com.uploadcare.api.Group
 */
public class GroupQueryBuilder implements PaginatedQueryBuilder<Group> {

    private final Client client;
    private final List<UrlParameter> parameters = new ArrayList<UrlParameter>();

    /**
     * Initializes a new builder for the given client.
     */
    public GroupQueryBuilder(Client client) {
        this.client = client;
    }

    /**
     * Adds a filter for datetime from objects will be returned.
     *
     * @param from A uploading datetime from which objects will be returned.
     */
    public GroupQueryBuilder from(Date from) {
        parameters.add(new FilesFromParameter(from));
        return this;
    }

    /**
     * Specifies the way groups are sorted.
     *
     * @param order [Order]
     * @see com.uploadcare.urls.UrlParameter.Order
     */
    public GroupQueryBuilder ordering(Order order) {
        parameters.add(new FilesOrderParameter(order));
        return this;
    }

    @Override
    public Iterable<Group> asIterable() {
        URI url = Urls.apiGroups();
        RequestHelper requestHelper = client.getRequestHelper();
        GroupDataWrapper dataWrapper = new GroupDataWrapper(client);
        return requestHelper.executePaginatedQuery(url, parameters, true, GroupPageData.class, dataWrapper);
    }

    @Override
    public List<Group> asList() {
        List<Group> groups = new ArrayList<Group>();
        for (Group group : asIterable()) {
            groups.add(group);
        }
        return groups;
    }
}
