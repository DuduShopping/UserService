package com.dudu.users;

import com.dudu.common.CryptoUtil;

public class PasswordHasher {
    private static final String SALT = "p@om^bcad3&yjena[jd!~si42*)[jdjk";
    private static final PasswordHasher instance = new PasswordHasher();

    public static PasswordHasher getInstance() {
        return instance;
    }

    private PasswordHasher() {}

    public String hashPassword(String password) {
        return CryptoUtil.sha256base64(SALT + password);
    }
}
