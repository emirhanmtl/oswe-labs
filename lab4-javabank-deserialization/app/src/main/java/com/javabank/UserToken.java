package com.javabank;

import java.io.Serializable;

public class UserToken implements Serializable {
    private static final long serialVersionUID = 1L;

    public String username;
    public boolean admin;

    public UserToken(String username, boolean admin) {
        this.username = username;
        this.admin = admin;
    }
}
