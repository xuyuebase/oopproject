package com.example.oopproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.widget.LinearLayout;

import java.io.InputStream;

public class ProfileHomeActivity extends AppCompatActivity {

    ImageView imgAvatar;
    Button btnEdit, btnChangeAvatar;

    TextView txtName, txtEmail, txtAge, txtHeight, txtWeight, txtGender, txtActivity, txtBio;

    private static final int PICK_IMAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_home);
        BottomNavHelper.setup(this);

        imgAvatar = findViewById(R.id.imgAvatar);
        btnChangeAvatar = findViewById(R.id.btnChangeAvatar);
        btnEdit = findViewById(R.id.btnEditProfile);

        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);
        txtAge = findViewById(R.id.txtAge);
        txtHeight = findViewById(R.id.txtHeight);
        txtWeight = findViewById(R.id.txtWeight);
        txtGender = findViewById(R.id.txtGender);
        txtActivity = findViewById(R.id.txtActivity);
        txtBio = findViewById(R.id.txtBio);

        loadProfile();

        btnEdit.setOnClickListener(v ->
                startActivity(new Intent(ProfileHomeActivity.this, EditProfileActivity.class))
        );

        btnChangeAvatar.setOnClickListener(v -> showAvatarOptions());

        Button btnLogout = findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(v -> {
            UserStorage.clearUser(this);   // 删除 email/password
            UserProfile.saveAvatar(this, "");  // 删除头像
            UserProfile.save(this, "", "", "", "", "", "", ""); // 清空所有资料

            Intent i = new Intent(ProfileHomeActivity.this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });
    }

    private void setupBottomNav() {

        LinearLayout navDashboard = findViewById(R.id.navDashboard);
        LinearLayout navFoodLog = findViewById(R.id.navFoodLog);
        LinearLayout navFeed = findViewById(R.id.navFeed);
        LinearLayout navProfile = findViewById(R.id.navProfile);

        // Home
        navDashboard.setOnClickListener(v ->
                startActivity(new Intent(this, DashboardActivity.class)));

        // Food Log
        navFoodLog.setOnClickListener(v ->
                startActivity(new Intent(this, FoodLogActivity.class)));

        // Feed
        navFeed.setOnClickListener(v ->
                startActivity(new Intent(this, FeedActivity.class)));

        // Profile (Already here)
        navProfile.setOnClickListener(v -> {
            // do nothing 或者刷新
        });
    }


    private void showAvatarOptions() {
        String[] options = {"Choose from Gallery", "Reset to Default"};

        new AlertDialog.Builder(this)
                .setTitle("Change Avatar")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) openGallery();
                    else resetAvatar();
                })
                .show();
    }

    private void resetAvatar() {
        imgAvatar.setImageResource(R.drawable.avatar_img);
        UserProfile.saveAvatar(this, "");
    }

    private void loadProfile() {
        txtName.setText(UserProfile.get(this, "name"));
        txtEmail.setText(UserStorage.getEmail(this));
        txtAge.setText(UserProfile.get(this, "age"));
        txtHeight.setText(UserProfile.get(this, "height"));
        txtWeight.setText(UserProfile.get(this, "weight"));
        txtGender.setText(UserProfile.get(this, "gender"));
        txtActivity.setText(UserProfile.get(this, "activity"));
        txtBio.setText(UserProfile.get(this, "bio"));

        String avatarBase64 = UserProfile.getAvatar(this);
        if (!avatarBase64.isEmpty()) {
            byte[] bytes = Base64.decode(avatarBase64, Base64.DEFAULT);
            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            imgAvatar.setImageBitmap(RoundImageHelper.getCircularBitmap(bmp));
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();

            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                Bitmap roundBmp = RoundImageHelper.getCircularBitmap(bitmap);

                imgAvatar.setImageBitmap(roundBmp);

                UserProfile.saveAvatar(this, bitmapToBase64(roundBmp));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String bitmapToBase64(Bitmap bitmap) {
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }


}
