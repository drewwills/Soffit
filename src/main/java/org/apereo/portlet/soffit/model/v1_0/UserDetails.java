package org.apereo.portlet.soffit.model.v1_0;

/**
 * Created by andrew on 6/10/16.
 */
public class UserDetails {
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "UserDetails [username = " + username + "]";
    }
}
