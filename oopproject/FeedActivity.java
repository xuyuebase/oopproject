package com.example.oopproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.view.View;   // ⭐⭐ 解决 View 红色报错
import android.content.Intent;

public class FeedActivity extends AppCompatActivity {

    EditText editPost;
    Button btnPost;
    TextView txtFeedList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        BottomNavHelper.setup(this);

        editPost = findViewById(R.id.editPost);
        btnPost = findViewById(R.id.btnPost);
        txtFeedList = findViewById(R.id.txtFeedList);

        btnPost.setOnClickListener(v -> {
            String post = editPost.getText().toString().trim();
            if (post.isEmpty()) return;

            txtFeedList.append("• " + post + "\n\n");
            editPost.setText("");
        });

        setupBottomNav();
    }

    private void setupBottomNav() {

        LinearLayout navHome = findViewById(R.id.navDashboard);
        LinearLayout navFood = findViewById(R.id.navFoodLog);
        LinearLayout navFeed = findViewById(R.id.navFeed);
        LinearLayout navProfile = findViewById(R.id.navProfile);

        navHome.setOnClickListener(v ->
                startActivity(new Intent(this, DashboardActivity.class)));

        navFood.setOnClickListener(v ->
                startActivity(new Intent(this, FoodLogActivity.class)));

        navFeed.setOnClickListener(v -> {
            // Already here
        });

        navProfile.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileHomeActivity.class)));
    }
}
