package edu.ewubd.cse489.group7.doctalk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.ewubd.cse489.group7.doctalk.adapters.AppointmentAdapter;
import edu.ewubd.cse489.group7.doctalk.models.Appointment;

public class History extends AppCompatActivity {

    private LinearLayout llHome, llList, llHistory, llProfile;
    private UserManager userManager;
    private ListView lvAppointments;
    private TextView tvEmptyAppointment;
    private AppointmentAdapter appointmentAdapter;
    private List<Appointment> appointmentList;
    private DatabaseReference appointmentsRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        userManager = new UserManager(this);

        if(!userManager.isLoggedIn()){
            Intent i = new Intent(History.this, LoginActivity.class);
            i.putExtra("source", "History");
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
            return;
        }

        initViews();
        loadUserAppointmentsFromFirebase();

        llHome.setOnClickListener(v -> {
            Intent i = new Intent(History.this, MainActivity.class);
            startActivity(i);
        });

        llList.setOnClickListener(v -> {
            Intent i = new Intent(History.this, DoctorList.class);
            startActivity(i);
        });

        llHistory.setOnClickListener(v -> {
            System.out.println("History Btn clicked");
        });

        llProfile.setOnClickListener(v -> {
            Intent i = new Intent(History.this, ProfileActivity.class);
            startActivity(i);
        });
    }
    private void initViews(){
        llHome = findViewById(R.id.llHome);
        llList = findViewById(R.id.llList);
        llHistory = findViewById(R.id.llHistory);
        llProfile = findViewById(R.id.llProfile);
        lvAppointments = findViewById(R.id.lvAppointments);
        tvEmptyAppointment = findViewById(R.id.tvEmptyAppointment);

        appointmentList = new ArrayList<>();
        appointmentAdapter = new AppointmentAdapter(this, appointmentList);
        lvAppointments.setAdapter(appointmentAdapter);
    }
    private void loadUserAppointmentsFromFirebase() {
        // Get current user's email for filtering
        String userEmail = userManager.getUserEmail();

        if (userEmail.isEmpty()) {
            Toast.makeText(this, "User session error. Please login again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Use your specific database URL for Asia Southeast region
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://doctalk-1481c-default-rtdb.asia-southeast1.firebasedatabase.app");
        appointmentsRef = database.getReference("appointments");

        appointmentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                appointmentList.clear();

                for (DataSnapshot appointmentSnapshot : snapshot.getChildren()) {
                    Appointment appointment = appointmentSnapshot.getValue(Appointment.class);
                    if (appointment != null) {
                        appointment.setId(appointmentSnapshot.getKey());

                        // Filter appointments by user email
                        if (userEmail.equals(appointment.getUserEmail())) {
                            appointmentList.add(appointment);
                        }
                    }
                }

                // Sort by timestamp (newest first)
                Collections.sort(appointmentList, new Comparator<Appointment>() {
                    @Override
                    public int compare(Appointment a1, Appointment a2) {
                        return Long.compare(a2.getTimestamp(), a1.getTimestamp());
                    }
                });

                // Show/hide empty message
                if (appointmentList.isEmpty()) {
                    tvEmptyAppointment.setVisibility(View.VISIBLE);
                    lvAppointments.setVisibility(View.GONE);
                } else {
                    tvEmptyAppointment.setVisibility(View.GONE);
                    lvAppointments.setVisibility(View.VISIBLE);
                }

                appointmentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(History.this, "Failed to load appointments: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}