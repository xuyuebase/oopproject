package com.example.oopproject;

import android.content.Context;
import android.content.SharedPreferences;

public class UserProfile {

    private static final String PREF = "UserProfile";

    public static void save(Context c, String name, String age, String height, String weight,
                            String gender, String activity, String bio) {

        SharedPreferences sp = c.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sp.edit();

        e.putString("name", name);
        e.putString("age", age);
        e.putString("height", height);
        e.putString("weight", weight);
        e.putString("gender", gender);
        e.putString("activity", activity);
        e.putString("bio", bio);

        e.apply();
    }

    public static String get(Context c, String key) {
        return c.getSharedPreferences(PREF, Context.MODE_PRIVATE).getString(key, "");
    }

    public static void saveAvatar(Context c, String base64) {
        SharedPreferences sp = c.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sp.edit();
        e.putString("avatar", base64);
        e.apply();
    }

    public static String getAvatar(Context c) {
        return c.getSharedPreferences(PREF, Context.MODE_PRIVATE)
                .getString("avatar", "");
    }

}
