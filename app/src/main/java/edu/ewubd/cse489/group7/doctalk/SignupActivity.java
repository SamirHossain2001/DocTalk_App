package edu.ewubd.cse489.group7.doctalk;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SignupActivity extends AppCompatActivity {

    private EditText etSignupName, etSignupEmail, etSignupPassword, etSignupPhone;
    private Button btnSignup;
    private TextView tvSignupToLogin;
    private CheckBox cbLoginRemember;
    private UserManager userManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        userManager = new UserManager(this);
        initViews();

        btnSignup.setOnClickListener(v -> performSignup());
        tvSignupToLogin.setOnClickListener(v -> toggleToLogin());
    }

    private void initViews(){
        etSignupName = findViewById(R.id.etSignupName);
        etSignupEmail = findViewById(R.id.etSignupEmail);
        etSignupPassword = findViewById(R.id.etSignupPassword);
        etSignupPhone = findViewById(R.id.etSignupPhone);
        btnSignup = findViewById(R.id.btnSignup);
        cbLoginRemember = findViewById(R.id.cbSignupRemember);
        tvSignupToLogin = findViewById(R.id.tvSignupToLogin);
    }
    private void performSignup(){
        String name = etSignupName.getText().toString().trim();
        String email = etSignupEmail.getText().toString().trim();
        String pass = etSignupPassword.getText().toString().trim();
        String phone = etSignupPhone.getText().toString().trim();
        boolean isRemembered = cbLoginRemember.isChecked();

        if(name.isEmpty()){
            Toast.makeText(this, "Name Field Required", Toast.LENGTH_SHORT).show();
            return;
        }

        if(email.isEmpty()){
            Toast.makeText(this, "Email Field Required", Toast.LENGTH_SHORT).show();
            return;
        }

        if(pass.isEmpty()){
            Toast.makeText(this, "Password Field Required", Toast.LENGTH_SHORT).show();
            return;
        }
        if(phone.isEmpty()){
            Toast.makeText(this, "Phone Field Required", Toast.LENGTH_SHORT).show();
            return;
        }

        userManager.saveUserSession(name, email, pass, phone, isRemembered);
        Toast.makeText(this, "User account created", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(SignupActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }
    private void toggleToLogin(){
        Intent i = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }
}