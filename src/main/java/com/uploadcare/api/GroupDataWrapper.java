package com.uploadcare.api;

import com.uploadcare.data.DataWrapper;
import com.uploadcare.data.GroupData;

public class GroupDataWrapper implements DataWrapper<Group, GroupData> {

    private final Client client;

    public GroupDataWrapper(Client client) {
        this.client = client;
    }

    @Override
    public Group wrap(GroupData data) {
        return new Group(client, data);
    }
}
