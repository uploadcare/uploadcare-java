package com.uploadcare.api;

class DefaultRequestHelperProvider implements RequestHelperProvider {

    public RequestHelper get(Client client) {
        return new RequestHelper(client);
    }

}
