package org.apereo.portlet.soffit.model.v1_0;

/**
 * Created by andrew on 6/10/16.
 */
public class UserDetails {
    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "UserDetails [userName = " + userName + "]";
    }
}
