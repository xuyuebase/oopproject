package com.example.oopproject;

import android.content.Context;
import android.content.SharedPreferences;

public class UserStorage {

    private static final String PREF_NAME = "UserData";

    // Save user
    public static void saveUser(Context context, String name, String email, String password) {
        SharedPreferences sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.putString("name", name);
        editor.putString("email", email);
        editor.putString("password", password);

        editor.apply();
    }

    // Load email
    public static String getEmail(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getString("email", "");
    }

    // Load password
    public static String getPassword(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getString("password", "");
    }

    // Load name
    public static String getName(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getString("name", "");
    }

    public static void clearUser(Context c) {
        SharedPreferences sp = c.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sp.edit();
        e.clear();
        e.apply();
    }

}
