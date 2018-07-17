package com.dudu.users;

import com.dudu.database.ZetaMap;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

public class User {
    private long userId;
    private String login;
    private String password;
    private Date createdOn;
    private Date lastLogin;

    @JsonIgnore
    private int loginAttempts;

    public static User from(ZetaMap zetaMap) {
        User user = new User();
        user.setUserId(zetaMap.getLong("UserId"));
        user.setLogin(zetaMap.getString("Login"));
        user.setPassword(zetaMap.getString("Password"));
        user.setCreatedOn(zetaMap.getDate("CreatedOn"));
        user.setLastLogin(zetaMap.getDate("LastLogin"));
        return user;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public int getLoginAttempts() {
        return loginAttempts;
    }

    public void setLoginAttempts(int loginAttempts) {
        this.loginAttempts = loginAttempts;
    }
}
