package com.uploadcare.api;

class DefaultRequestHelperProvider implements RequestHelperProvider {

    @Override
    public RequestHelper get(Client client) {
        return new RequestHelper(client);
    }

}
