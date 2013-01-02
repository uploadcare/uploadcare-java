package com.uploadcare.data;

import com.uploadcare.api.Client;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class DataUtils {

    public static <T> T executeQuery(Client client, HttpUriRequest request, Class<T> dataClass) {
        try {
            HttpResponse response = client.getHttpClient().execute(request);
            HttpEntity entity = response.getEntity();
            String data = EntityUtils.toString(entity);
            return client.getObjectMapper().readValue(data, dataClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void executeCommand(Client client, HttpUriRequest request) {
        try {
            client.getHttpClient().execute(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
