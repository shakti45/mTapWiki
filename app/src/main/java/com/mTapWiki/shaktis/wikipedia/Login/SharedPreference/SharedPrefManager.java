package com.mTapWiki.shaktis.wikipedia.Login.SharedPreference;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.mTapWiki.shaktis.wikipedia.Login.UserLogin;


public class SharedPrefManager {

    //the constants
    private static final String SHARED_PREF_NAME = "LoggedInUser";
    private static final String KEY_USERNAME = "keyusername";
    private static final String KEY_READ = "keyAccountType";
    private static final String KEY_WRITE = "keyregion";


    private static SharedPrefManager mInstance;
    private static Context mCtx;

    private SharedPrefManager(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    public void userLogin(User user) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USERNAME, user.getUsername());
        editor.putBoolean(KEY_READ, user.getRead());
        editor.putBoolean(KEY_WRITE,user.getWrite());
        editor.apply();
    }

    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USERNAME, null) != null;
    }

    public User getUser() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new User(
            sharedPreferences.getString(KEY_USERNAME, null),
            sharedPreferences.getBoolean(KEY_READ, Boolean.parseBoolean(null)),
            sharedPreferences.getBoolean(KEY_WRITE, Boolean.parseBoolean(null))
        );
    }

    public void logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        mCtx.startActivity(new Intent(mCtx, UserLogin.class));
    }
}