package edu.ewubd.cse489.group7.doctalk;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private LinearLayout llHome, llList, llHistory, llProfile, llProfilePersonalDetails, llLogout;
    private UserManager userManager;
    private TextView tvProfileEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userManager = new UserManager(this);
        if(!userManager.isLoggedIn()){
            Intent i = new Intent(ProfileActivity.this, LoginActivity.class);
            i.putExtra("source", "ProfileActivity");
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
            return;
        }
        initViews();
        String email = userManager.getUserEmail();
        if(!email.isEmpty()){
            tvProfileEmail.setText(email);
        }


        // logout
        llLogout.setOnClickListener(v -> logout());

        llProfilePersonalDetails.setOnClickListener(v -> {
            Intent i = new Intent(ProfileActivity.this, UpdateProfileActivity.class);
            startActivity(i);
        });

        llHome.setOnClickListener(v -> {
            Intent i = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(i);
        });

        llList.setOnClickListener(v -> {
            Intent i = new Intent(ProfileActivity.this, DoctorList.class);
            startActivity(i);
        });

        llHistory.setOnClickListener(v -> {
            Intent i = new Intent(ProfileActivity.this, History.class);
            startActivity(i);
        });

        llProfile.setOnClickListener(v -> {
            System.out.println("Profile Btn clicked");
        });
    }
    private void initViews(){
        llHome = findViewById(R.id.llHome);
        llList = findViewById(R.id.llList);
        llHistory = findViewById(R.id.llHistory);
        llProfile = findViewById(R.id.llProfile);
        llProfilePersonalDetails = findViewById(R.id.llProfilePersonalDetails);
        llLogout = findViewById(R.id.llLogout);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);
    }
    private void logout(){
        userManager.logout();
        Intent i = new Intent(ProfileActivity.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }

}