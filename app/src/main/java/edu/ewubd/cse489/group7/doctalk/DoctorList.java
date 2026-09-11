package edu.ewubd.cse489.group7.doctalk;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.ewubd.cse489.group7.doctalk.adapters.DoctorAdapter;
import edu.ewubd.cse489.group7.doctalk.models.Doctor;
import edu.ewubd.cse489.group7.doctalk.services.NotificationScheduler;

public class DoctorList extends AppCompatActivity {

    private LinearLayout llHome, llList, llHistory, llProfile;
//    private EditText searchDoctor;
    private ListView listViewDoctors;
    private DoctorAdapter doctorAdapter;
    private List<Doctor> doctorList;
    private DatabaseReference databaseReference;
    private ProgressBar progressBar;
    private TextView loadingText;
    private LoadDoctorsTask currentTask;
    private UserManager userManager;
    // NOTIFICATION: Added test notification button
    private Button testNotificationBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_list);
        userManager = new UserManager(this);

        // FIXED: Only check login status, not remember preference
        if(!userManager.isLoggedIn()){
            Intent i = new Intent(DoctorList.this, LoginActivity.class);
            i.putExtra("source", "DoctorList");
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
            return;
        }

        initViews();
        createNotificationChannel();
        requestNotificationPermission();
        setupNavigationListeners();
        // Start AsyncTask to load doctors
        currentTask = new LoadDoctorsTask();
        currentTask.execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cancel the AsyncTask if activity is destroyed
        if (currentTask != null && !currentTask.isCancelled()) {
            currentTask.cancel(true);
        }
    }

    private void requestNotificationPermission() {
        // Request notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        // NOTIFICATION: Request exact alarm permission for Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SCHEDULE_EXACT_ALARM)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SCHEDULE_EXACT_ALARM}, 102);
            }
        }
    }

    // NOTIFICATION: Updated notification channel for better organization
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Appointment Reminders";
            String description = "Channel for appointment reminder notifications - 1 day before appointments";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("APPOINTMENT_CHANNEL", name, importance);
            channel.setDescription(description);
            // NOTIFICATION: Enable vibration and lights for the channel
            channel.enableVibration(true);
            channel.enableLights(true);
            channel.setVibrationPattern(new long[]{1000, 1000, 1000});

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            Log.d("DoctorList", "Notification channel created successfully");
        }
    }

    private void initViews(){
        llHome = findViewById(R.id.llHome);
        llList = findViewById(R.id.llList);
        llHistory = findViewById(R.id.llHistory);
        llProfile = findViewById(R.id.llProfile);
//        searchDoctor = findViewById(R.id.searchDoctor);
        listViewDoctors = findViewById(R.id.listViewDoctors);
        progressBar = findViewById(R.id.progressBar);
        loadingText = findViewById(R.id.loadingText);

        doctorList = new ArrayList<>();
        doctorAdapter = new DoctorAdapter(this, doctorList);
        listViewDoctors.setAdapter(doctorAdapter);
        // Initialize Firebase reference
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://doctalk-1481c-default-rtdb.asia-southeast1.firebasedatabase.app");
        databaseReference = database.getReference("doctors");
    }

    private void setupNavigationListeners(){
        llHome.setOnClickListener(v -> {
            Intent i = new Intent(DoctorList.this, MainActivity.class);
            startActivity(i);
        });

        llList.setOnClickListener(v -> {
            System.out.println("List Btn clicked - Already on DoctorList");
            // Already on this page, do nothing or refresh content
        });

        llHistory.setOnClickListener(v -> {
            Intent i = new Intent(DoctorList.this, History.class);
            startActivity(i);
        });

        llProfile.setOnClickListener(v -> {
            Intent i = new Intent(DoctorList.this, ProfileActivity.class);
            startActivity(i);
        });
    }

    // AsyncTask for loading doctors from Firebase
    private class LoadDoctorsTask extends AsyncTask<Void, String, List<Doctor>> {
        private boolean isError = false;
        private String errorMessage = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Show loading UI
            progressBar.setVisibility(View.VISIBLE);
            loadingText.setVisibility(View.VISIBLE);
            loadingText.setText("Loading doctors...");
            listViewDoctors.setVisibility(View.GONE);
        }

        @Override
        protected List<Doctor> doInBackground(Void... voids) {
            final List<Doctor> tempDoctorList = new ArrayList<>();
            final Object lock = new Object();
            final boolean[] isComplete = {false};

            try {
                // Publish progress update
                publishProgress("Connecting to database...");

                // Add ValueEventListener
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        publishProgress("Processing doctor data...");

                        int totalDoctors = (int) snapshot.getChildrenCount();
                        int currentDoctor = 0;

                        for (DataSnapshot doctorSnapshot : snapshot.getChildren()) {
                            if (isCancelled()) {
                                break;
                            }

                            currentDoctor++;
                            publishProgress("Loading doctor " + currentDoctor + " of " + totalDoctors);

                            Doctor doctor = doctorSnapshot.getValue(Doctor.class);
                            if (doctor != null) {
                                doctor.setId(doctorSnapshot.getKey());

                                // Fix for image URLs - provide default images if URL is invalid
                                if (doctor.getImage() == null || doctor.getImage().isEmpty() ||
                                        !isValidImageUrl(doctor.getImage())) {
                                    // Use a default avatar image based on doctor ID
                                    doctor.setImage(getDefaultDoctorImage(doctor.getId()));
                                }

                                tempDoctorList.add(doctor);
                            }

                            // Simulate some processing time for demonstration
                            try {
                                Thread.sleep(100); // Remove this in production
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        synchronized (lock) {
                            isComplete[0] = true;
                            lock.notify();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        isError = true;
                        errorMessage = error.getMessage();
                        Log.e("DoctorList", "Database error: " + error.getMessage());

                        synchronized (lock) {
                            isComplete[0] = true;
                            lock.notify();
                        }
                    }
                });

                // Wait for Firebase callback to complete
                synchronized (lock) {
                    while (!isComplete[0]) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

            } catch (Exception e) {
                isError = true;
                errorMessage = e.getMessage();
                e.printStackTrace();
            }

            return tempDoctorList;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            // Update loading text with current progress
            if (values.length > 0) {
                loadingText.setText(values[0]);
            }
        }

        @Override
        protected void onPostExecute(List<Doctor> doctors) {
            super.onPostExecute(doctors);

            // Hide loading UI
            progressBar.setVisibility(View.GONE);
            loadingText.setVisibility(View.GONE);
            listViewDoctors.setVisibility(View.VISIBLE);

            if (isError) {
                Toast.makeText(DoctorList.this,
                        "Failed to load doctors: " + errorMessage,
                        Toast.LENGTH_LONG).show();
            } else {
                // Update the adapter with loaded doctors
                doctorList.clear();
                doctorList.addAll(doctors);
                doctorAdapter.notifyDataSetChanged();

                if (doctors.isEmpty()) {
                    loadingText.setVisibility(View.VISIBLE);
                    loadingText.setText("No doctors available");
                } else {
                    Toast.makeText(DoctorList.this,
                            "Loaded " + doctors.size() + " doctors",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            // Handle task cancellation
            progressBar.setVisibility(View.GONE);
            loadingText.setVisibility(View.VISIBLE);
            loadingText.setText("Loading cancelled");
            Toast.makeText(DoctorList.this, "Loading cancelled", Toast.LENGTH_SHORT).show();
        }

        // Helper method to validate image URLs
        private boolean isValidImageUrl(String url) {
            return url != null && !url.isEmpty() &&
                    (url.startsWith("http://") || url.startsWith("https://")) &&
                    (url.contains(".jpg") || url.contains(".jpeg") ||
                            url.contains(".png") || url.contains(".gif") ||
                            url.contains("images.unsplash.com") ||
                            url.contains("i.pravatar.cc") ||
                            url.contains("randomuser.me"));
        }

        // Helper method to get default doctor image
        private String getDefaultDoctorImage(String doctorId) {
            // Use UI Avatars service for generating avatar based on doctor ID
            // This creates a consistent avatar for each doctor
            int hash = doctorId.hashCode();
            String[] colors = {"3498db", "2ecc71", "e74c3c", "f39c12", "9b59b6", "1abc9c"};
            String bgColor = colors[Math.abs(hash) % colors.length];

            // Generate initials from doctor ID (first 2 characters)
            String initials = doctorId.length() >= 2 ?
                    doctorId.substring(0, 2).toUpperCase() : "DR";

            // Return URL for UI Avatars service
            return "https://ui-avatars.com/api/?name=" + initials +
                    "&background=" + bgColor + "&color=fff&size=200";
        }
    }
}
