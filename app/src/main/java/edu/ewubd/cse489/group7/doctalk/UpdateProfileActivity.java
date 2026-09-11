package edu.ewubd.cse489.group7.doctalk;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class UpdateProfileActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword, etPhone;
    private Button btnSave;
    private String oldName, oldEmail, oldPassword, oldPhone;
    private String newName, newEmail, newPassword, newPhone;
    private String finalName, finalEmail, finalPassword, finalPhone;

    private UserManager userManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        userManager = new UserManager(this);
        if(!userManager.isLoggedIn()){
            Intent i = new Intent(UpdateProfileActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
            return;
        }
        initViews();

        btnSave.setOnClickListener(v -> updateInfo());
    }

    private void updateInfo() {
        oldName = userManager.getUserName();
        oldEmail = userManager.getUserEmail();
        oldPassword = userManager.getUserPassword();
        oldPhone = userManager.getUserPhone();

        newName = etName.getText().toString().trim();
        newEmail = etEmail.getText().toString().trim();
        newPassword = etPassword.getText().toString().trim();
        newPhone = etPhone.getText().toString().trim();

        // System.out.println(oldName +' '+ oldEmail+' '+oldPassword+' '+oldPhone);
        // System.out.println(newName+' '+newEmail+' '+newPassword+' '+newPhone);
        finalName = (!newName.isEmpty()) ? newName : oldName;
        finalEmail = (!newEmail.isEmpty()) ? newEmail : oldEmail;
        finalPassword = (!newPassword.isEmpty()) ? newPassword : oldPassword;
        finalPhone = (!newPhone.isEmpty()) ? newPhone : oldPhone;

        userManager.updateUserDetails(finalName, finalEmail, finalPassword, finalPhone);
        Toast.makeText(this, "Information Updated Successfully.", Toast.LENGTH_SHORT).show();

        Intent i = new Intent(UpdateProfileActivity.this, ProfileActivity.class);
        startActivity(i);
        finish();
     }

    private void initViews() {
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etPhone = findViewById(R.id.etPhone);
        btnSave = findViewById(R.id.btnSave);
    }
}