package com.example.oopproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;

public class DashboardActivity extends AppCompatActivity {

    ImageView dashAvatar;
    TextView dashName;

    LinearLayout navDashboard, navFoodLog, navFeed, navProfile;

    TextView txtConsumed, txtBurned, txtRemaining;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        BottomNavHelper.setup(this);

        // 顶部：头像 & 名字
        dashAvatar = findViewById(R.id.dashAvatar);
        dashName = findViewById(R.id.dashName);

        // Summary TextViews
        txtConsumed = findViewById(R.id.txtConsumed);
        txtBurned = findViewById(R.id.txtBurned);
        txtRemaining = findViewById(R.id.txtRemaining);

        // 底部导航栏
        navDashboard = findViewById(R.id.navDashboard);
        navFoodLog = findViewById(R.id.navFoodLog);
        navFeed = findViewById(R.id.navFeed);
        navProfile = findViewById(R.id.navProfile);

        // 显示名字
        String name = UserProfile.get(this, "name");
        if (name.isEmpty()) name = "User";
        dashName.setText("Welcome, " + name + "!");

        // 显示头像
        String avatarBase64 = UserProfile.getAvatar(this);
        if (!avatarBase64.isEmpty()) {
            byte[] bytes = Base64.decode(avatarBase64, Base64.DEFAULT);
            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            dashAvatar.setImageBitmap(RoundImageHelper.getCircularBitmap(bmp));
        } else {
            dashAvatar.setImageResource(R.drawable.avatar_img);
        }

        // 底部导航监听
        setupBottomNav();
    }

    private void setupBottomNav() {

        navDashboard.setOnClickListener(v -> {
            // Already here
        });

        navFoodLog.setOnClickListener(v ->
                startActivity(new Intent(this, FoodLogActivity.class)));

        navFeed.setOnClickListener(v ->
                startActivity(new Intent(this, FeedActivity.class)));

        navProfile.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileHomeActivity.class)));
    }
}
