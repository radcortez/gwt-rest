package com.radcortez.gwt.rest.client;

public interface GwtRest {
    User getUser();

    User createUser(User user);

    User updateUser(String name);
}
