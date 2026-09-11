package edu.ewubd.cse489.group7.doctalk;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    private EditText etloginEmail, etLoginPassword;
    private Button btnLogin;
    private TextView tvLoginToSignup;
    private CheckBox cbLoginRemember;
    private UserManager userManager;
    private String sourcePage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userManager = new UserManager(this);
        sourcePage = getIntent().getStringExtra("source");

        // Debug logging
        //System.out.println("LoginActivity - Source page: " + sourcePage);
        // System.out.println("LoginActivity - Current login status: " + userManager.isLoggedIn());

        initViews();

        btnLogin.setOnClickListener(v -> performLogin());
        tvLoginToSignup.setOnClickListener(v -> toggleToSignup());
    }
    @Override
    public void onBackPressed() {
        // If user came from another activity, go to MainActivity
        if (sourcePage != null && !sourcePage.isEmpty()) {
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    private void initViews(){
        etloginEmail = findViewById(R.id.etloginEmail);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvLoginToSignup = findViewById(R.id.tvLoginToSignup);
        cbLoginRemember = findViewById(R.id.cbLoginRemember);
    }

    private void performLogin(){
        String email = etloginEmail.getText().toString().trim();
        String pass = etLoginPassword.getText().toString().trim();

        if(email.isEmpty()){
            Toast.makeText(this, "Email Field Required", Toast.LENGTH_SHORT).show();
            return;
        }

        if(pass.isEmpty()){
            Toast.makeText(this, "Password Field Required", Toast.LENGTH_SHORT).show();
            return;
        }

        String userEmail = userManager.getUserEmail();
        String userPass = userManager.getUserPassword();

        // Debug logging
        System.out.println("Attempting login - Email: " + email + ", Stored email: " + userEmail);

        if(email.equals(userEmail) && pass.equals(userPass)){
            // Set login status and remember preference
            userManager.setLoggedIn(true);
            userManager.setIsUserRemembered(cbLoginRemember.isChecked());

            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();

            // Navigate to the correct activity
            navigateToDestination();
        }
        else{
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToDestination(){
        Intent i;
        System.out.println("Navigating from source: " + sourcePage);

        if("ProfileActivity".equals(sourcePage)){
            i = new Intent(LoginActivity.this, ProfileActivity.class);
            System.out.println("Going to ProfileActivity");
        }
        else if("HistoryActivity".equals(sourcePage) || "History".equals(sourcePage)){
            i = new Intent(LoginActivity.this, History.class);
            System.out.println("Going to History");
        }
        else if("DoctorList".equals(sourcePage)){
            i = new Intent(LoginActivity.this, DoctorList.class);
            System.out.println("Going to DoctorList");
        }
        else {
            i = new Intent(LoginActivity.this, MainActivity.class);
            System.out.println("Going to MainActivity (default)");
        }

        startActivity(i);
        finish();
    }

    private void toggleToSignup(){
        Intent i = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(i);
        finish();
    }
}