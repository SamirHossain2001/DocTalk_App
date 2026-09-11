package edu.ewubd.cse489.group7.doctalk;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import edu.ewubd.cse489.group7.doctalk.services.NotificationScheduler;

public class MainActivity extends AppCompatActivity {

    private LinearLayout llHome, llList, llHistory, llProfile;
    private FrameLayout flBell;
    private TextView tvName;
    private UserManager userManager;
    private ImageView ivAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userManager = new UserManager(this);
        // userManager.checkSessionOnAppStart();

        initViews();

        // Update welcome
        String userName = userManager.getUserName();
        if(!userName.isEmpty() && userManager.isLoggedIn()){
            tvName.setText(userName);
        }

        if(userManager.isLoggedIn()) {
            ivAvatar.setOnClickListener(v -> showProfile());
        }
        // NOTIFICATION: Updated bell click to show notification options including test
        flBell.setOnClickListener(v -> showNotificationOptions());

        llHome.setOnClickListener(v -> {
            System.out.println("Home button clicked");
        });

        llList.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, DoctorList.class);
            startActivity(i);
        });

        llHistory.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, History.class);
            startActivity(i);
        });

        llProfile.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(i);
        });
    }

    private void showProfile() {
        String userInfo = "Name: "+userManager.getUserName()+"\n"+
                "Email: "+userManager.getUserEmail()+"\n"+
                "Phone: "+userManager.getUserPhone()+"\n";
        new AlertDialog.Builder(this).setTitle("User Profile").setMessage(userInfo).setPositiveButton("OK",null).show();
    }

    // NOTIFICATION: Updated notification dialog to include test option
    private void showNotificationOptions(){
        new AlertDialog.Builder(this)
                .setTitle("Notifications")
                .setMessage("Choose an option:")
                .setPositiveButton("Test Notification", (dialog, which) -> {
                    // NEW_NOTIFICATION: Test notification functionality
                    testNotificationNow();
                })
                .setNegativeButton("View Settings", (dialog, which) -> {
                    Toast.makeText(this, "Notification settings not implemented yet", Toast.LENGTH_SHORT).show();
                })
                .setNeutralButton("Cancel", null)
                .show();
    }

    // NOTIFICATION: Method to test notification immediately
    private void testNotificationNow() {
        NotificationScheduler.scheduleImmediateNotification(
                this,
                "Dr. Strange",
                "2025-09-01",
                "2:30 PM",
                "DocTalk General Hospital"
        );
        Toast.makeText(this, "Test notification will appear in 5 seconds! 🔔", Toast.LENGTH_LONG).show();
    }

    private void initViews(){
        llHome = findViewById(R.id.llHome);
        llList = findViewById(R.id.llList);
        llHistory = findViewById(R.id.llHistory);
        llProfile = findViewById(R.id.llProfile);
        tvName = findViewById(R.id.tvName);
        ivAvatar = findViewById(R.id.ivAvatar);
        flBell = findViewById(R.id.flBell);
    }
}