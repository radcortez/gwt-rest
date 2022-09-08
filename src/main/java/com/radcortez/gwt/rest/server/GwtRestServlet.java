package com.radcortez.gwt.rest.server;

import com.radcortez.gwt.rest.client.GwtRest;
import com.radcortez.gwt.rest.client.User;

public class GwtRestServlet extends RestSupportServlet implements GwtRest {
    @Override
    public User getUser() {
        return new User("Naruto", "Uzumaki");
    }

    @Override
    public User createUser(final User user) {
        return user;
    }

    @Override
    @Payload(User.class)
    public User updateUser(@PayloadPath("firstName") final String name) {
        return new User(name, "Uzumaki");
    }
}
