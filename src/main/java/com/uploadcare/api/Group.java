package com.uploadcare.api;

import com.uploadcare.data.GroupData;

import java.net.URI;
import java.util.Date;

/**
 * The resource for group.
 */
public class Group {

    private final Client client;
    private final GroupData groupData;

    public Group(Client client, GroupData groupData) {
        this.client = client;
        this.groupData = groupData;
    }

    public String getId() {
        return groupData.id;
    }

    public URI getUrl() {
        return groupData.url;
    }

    public Date getCreateDate() {
        return groupData.datetimeCreated;
    }

    public Date getStoreDate() {
        return groupData.datetimeStored;
    }

    public int getFileCount() {
        return groupData.filesCount;
    }

    public URI getCdnUrl() {
        return groupData.cdnUrl;
    }

    /**
     * Mark all files in a group as stored.
     *
     * This does not mutate the current {@code Group} instance,
     * but creates a new one.
     *
     * @return New Group resource instance
     */
    public Group save() {
        client.saveGroup(getId());
        return client.getGroup(getId());
    }

    @Override
    public String toString() {
        return "Group{" +
                "groupData=" + groupData +
                '}';
    }
}
