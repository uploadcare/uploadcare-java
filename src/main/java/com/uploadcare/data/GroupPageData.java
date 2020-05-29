package com.uploadcare.data;

import java.net.URI;
import java.util.List;

public class GroupPageData implements PageData<GroupData> {

    public URI next;

    public URI previous;

    public int total;

    public int perPage;

    public List<GroupData> results;

    @Override
    public List<GroupData> getResults() {
        return results;
    }

    @Override
    public boolean hasMore() {
        return next != null;
    }

    @Override
    public URI getNext() {
        return next;
    }
}
