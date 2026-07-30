package com.javabank;

import java.util.HashMap;
import java.util.Map;

public class Users {
    private static final Map<String, String> CREDENTIALS = new HashMap<>();
    private static final Map<String, Boolean> ADMINS = new HashMap<>();

    static {
        CREDENTIALS.put("jsmith", "Summer2024!");
        ADMINS.put("jsmith", false);
        // The real admin password is long and random - it isn't meant to be
        // guessed or brute-forced.
        CREDENTIALS.put("admin", "kX9!vQ2mZ7#pR4wL");
        ADMINS.put("admin", true);
    }

    public static boolean check(String username, String password) {
        return username != null && password != null
                && password.equals(CREDENTIALS.get(username));
    }

    public static boolean isAdmin(String username) {
        return Boolean.TRUE.equals(ADMINS.get(username));
    }
}
