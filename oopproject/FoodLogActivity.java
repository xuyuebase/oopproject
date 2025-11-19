package com.example.oopproject;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;
import android.widget.LinearLayout;

public class FoodLogActivity extends AppCompatActivity {

    EditText editFood, editCalories;
    Button btnAddFood;
    TextView txtFoodList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_log);
        BottomNavHelper.setup(this);

        editFood = findViewById(R.id.editFood);
        editCalories = findViewById(R.id.editCalories);
        btnAddFood = findViewById(R.id.btnAddFood);
        txtFoodList = findViewById(R.id.txtFoodList);

        btnAddFood.setOnClickListener(v -> {
            String name = editFood.getText().toString().trim();
            String cal = editCalories.getText().toString().trim();

            if (name.isEmpty() || cal.isEmpty()) {
                Toast.makeText(this, "Please enter food and calories", Toast.LENGTH_SHORT).show();
                return;
            }

            txtFoodList.append(name + " - " + cal + " kcal\n");

            editFood.setText("");
            editCalories.setText("");
        });

        setupBottomNav();
    }

    private void setupBottomNav() {

        LinearLayout navHome = findViewById(R.id.navDashboard);
        LinearLayout navFood = findViewById(R.id.navFoodLog);
        LinearLayout navFeed = findViewById(R.id.navFeed);
        LinearLayout navProfile = findViewById(R.id.navProfile);

        // Home
        navHome.setOnClickListener(v ->
                startActivity(new Intent(this, DashboardActivity.class)));

        // Food Log
        navFood.setOnClickListener(v ->
                startActivity(new Intent(this, FoodLogActivity.class)));

        // Feed
        navFeed.setOnClickListener(v -> {
            // Already here
        });

        // Profile
        navProfile.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileHomeActivity.class)));
    }
}
