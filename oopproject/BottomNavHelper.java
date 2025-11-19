package com.example.oopproject;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

public class BottomNavHelper {

    public static void setup(Activity activity) {

        LinearLayout navHome = activity.findViewById(R.id.navDashboard);
        LinearLayout navFood = activity.findViewById(R.id.navFoodLog);
        LinearLayout navFeed = activity.findViewById(R.id.navFeed);
        LinearLayout navProfile = activity.findViewById(R.id.navProfile);

        // Dashboard
        navHome.setOnClickListener(v -> navigate(activity, DashboardActivity.class));

        // Food Log
        navFood.setOnClickListener(v -> navigate(activity, FoodLogActivity.class));

        // Feed
        navFeed.setOnClickListener(v -> navigate(activity, FeedActivity.class));

        // Profile
        navProfile.setOnClickListener(v -> navigate(activity, ProfileHomeActivity.class));
    }

    private static void navigate(Activity current, Class<?> target) {

        // 避免从 A 页面跳去 A 页面无限叠加
        if (current.getClass().equals(target)) return;

        Intent i = new Intent(current, target);
        current.startActivity(i);

        // 淡入淡出过渡（更顺滑）
        current.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
