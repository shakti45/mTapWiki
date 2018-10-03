package com.mTapWiki.shaktis.wikipedia.Login.SharedPreference;

/**
 * Created by shaktis on 08/01/18.
 */

public class User {

    private int id;
    private String username;
    private Boolean read;
    private Boolean write;
    public User(String username, Boolean read, Boolean write) {
        this.username = username;
        this.read = read;
        this.write = write;
    }

    public String getUsername() {

        return username;
    }

    public Boolean getRead() {
        return read;
    }

    public Boolean getWrite() {
        return write;
    }
}
