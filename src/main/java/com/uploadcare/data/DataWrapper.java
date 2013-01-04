package com.uploadcare.data;

public interface DataWrapper<T, U> {

    T wrap(U data);

}
