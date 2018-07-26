package com.dudu.users;

import com.dudu.database.DatabaseRow;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

public class User {
    private long userId;
    private String username;
    private String password;
    private Date createdOn;
    private Date lastLogin;

    @JsonIgnore
    private int loginAttempts;

    public static User from(DatabaseRow zetaMap) {
        User user = new User();
        user.setUserId(zetaMap.getLong("UserId"));
        user.setUsername(zetaMap.getString("Username"));
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
