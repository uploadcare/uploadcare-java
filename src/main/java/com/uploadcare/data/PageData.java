package com.uploadcare.data;

import java.util.List;

public interface PageData<T> {

    List<T> getResults();
    boolean hasMore();

}
