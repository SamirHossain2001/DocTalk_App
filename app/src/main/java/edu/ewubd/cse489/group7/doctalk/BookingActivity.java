package edu.ewubd.cse489.group7.doctalk;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import edu.ewubd.cse489.group7.doctalk.models.Appointment;
import edu.ewubd.cse489.group7.doctalk.models.Doctor;
// NEW_NOTIFICATION: Removed WorkManager imports and added NotificationScheduler import
import edu.ewubd.cse489.group7.doctalk.services.NotificationScheduler;
import edu.ewubd.cse489.group7.doctalk.UserManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookingActivity extends AppCompatActivity {
    private TextView doctorNameText, hospitalText, feeText, specializationText;
    private EditText patientNameEdit, patientPhoneEdit, selectedDateEdit;
    private Spinner timeSpinner;
    private Button selectDateButton, bookAppointmentButton;

    private Doctor selectedDoctor;
    private DatabaseReference doctorsRef, appointmentsRef;
    private String selectedDate = "";
    private Calendar calendar;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        userManager = new UserManager(this);

        if (!userManager.isLoggedIn()) {
            Intent intent = new Intent(BookingActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        initViews();
        getSelectedDoctor();
        setupDatePicker();
        setupTimeSpinner();

        patientNameEdit.setText(userManager.getUserName());
        patientPhoneEdit.setText(userManager.getUserPhone());

        bookAppointmentButton.setOnClickListener(v -> bookAppointment());
    }

    private void initViews() {
        doctorNameText = findViewById(R.id.doctorNameText);
        hospitalText = findViewById(R.id.hospitalText);
        feeText = findViewById(R.id.feeText);
        specializationText = findViewById(R.id.specializationText);
        patientNameEdit = findViewById(R.id.patientNameEdit);
        patientPhoneEdit = findViewById(R.id.patientPhoneEdit);
        selectedDateEdit = findViewById(R.id.selectedDateEdit);
        timeSpinner = findViewById(R.id.timeSpinner);
        selectDateButton = findViewById(R.id.selectDateButton);
        bookAppointmentButton = findViewById(R.id.bookAppointmentButton);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://doctalk-1481c-default-rtdb.asia-southeast1.firebasedatabase.app");
        doctorsRef = database.getReference("doctors");
        appointmentsRef = database.getReference("appointments");
        calendar = Calendar.getInstance();
    }

    private void getSelectedDoctor() {
        String doctorId = getIntent().getStringExtra("doctorId");
        if (doctorId == null) {
            Toast.makeText(this, "Error: No doctor selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        doctorsRef.child(doctorId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                selectedDoctor = snapshot.getValue(Doctor.class);
                if (selectedDoctor != null) {
                    selectedDoctor.setId(snapshot.getKey());
                    displayDoctorInfo();
                } else {
                    Toast.makeText(BookingActivity.this, "Doctor not found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BookingActivity.this, "Error loading doctor info", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void displayDoctorInfo() {
        doctorNameText.setText("Dr. " + selectedDoctor.getName());
        hospitalText.setText("Hospital: " + selectedDoctor.getHospital());
        feeText.setText("Fee: "+ selectedDoctor.getFee()+" TK.");
        specializationText.setText("Specialization: " + selectedDoctor.getSpecialization());
        setupTimeSpinner();
    }

    private void setupDatePicker() {
        selectDateButton.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        calendar.set(year, month, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        selectedDate = sdf.format(calendar.getTime());
                        selectedDateEdit.setText(selectedDate);
                        checkDateAvailability();
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );

            // Set minimum date to today
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
            datePickerDialog.show();
        });
    }

    private void setupTimeSpinner() {
        if (selectedDoctor != null && selectedDoctor.getWorkingHours() != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, selectedDoctor.getWorkingHours());
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            timeSpinner.setAdapter(adapter);
        }
    }

    private void checkDateAvailability() {
        List<String> doctorBookedDates = selectedDoctor.getBookedDates();
        if (doctorBookedDates != null && doctorBookedDates.contains(selectedDate)) {
            Toast.makeText(this, "This date is already booked. Please select another date.",
                    Toast.LENGTH_LONG).show();
            selectedDateEdit.setText("");
            selectedDate = "";
        }
    }

    private void bookAppointment() {
        String patientName = patientNameEdit.getText().toString().trim();
        String patientPhone = patientPhoneEdit.getText().toString().trim();
        String selectedTime = timeSpinner.getSelectedItem() != null ?
                timeSpinner.getSelectedItem().toString() : "";

        if (patientName.isEmpty() || patientPhone.isEmpty() || selectedDate.isEmpty() || selectedTime.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check date availability again before booking
        List<String> doctorBookedDates = selectedDoctor.getBookedDates();
        if (doctorBookedDates != null && doctorBookedDates.contains(selectedDate)) {
            Toast.makeText(this, "This date is already booked!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get user email from SharedPreferences for unique identification
        String userEmail = userManager.getUserEmail();

        // Create appointment with user email
        String appointmentId = appointmentsRef.push().getKey();
        Appointment appointment = new Appointment(
                appointmentId,
                selectedDoctor.getId(),
                selectedDoctor.getName(),
                patientName,
                patientPhone,
                userEmail,
                selectedDate,
                selectedTime,
                selectedDoctor.getHospital(),
                selectedDoctor.getFee()
        );

        // NOTIFICATION: Save appointment first, then handle notification separately to avoid blocking booking
        appointmentsRef.child(appointmentId).setValue(appointment)
                .addOnSuccessListener(aVoid -> {
                    Log.d("BookingActivity", "Appointment saved successfully");

                    // Update doctor's booked dates
                    updateDoctorBookedDates();

                    // NOTIFICATION: Schedule notification safely without breaking booking flow
                    scheduleNotificationSafely(appointment);

                    Toast.makeText(BookingActivity.this, "Appointment booked successfully!",
                            Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e("BookingActivity", "Failed to book appointment: " + e.getMessage(), e);
                    Toast.makeText(BookingActivity.this, "Failed to book appointment: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void updateDoctorBookedDates() {
        List<String> bookedDates = selectedDoctor.getBookedDates();

        // Create a new mutable ArrayList to avoid UnsupportedOperationException
        List<String> mutableBookedDates = new ArrayList<>();
        if (bookedDates != null) {
            mutableBookedDates.addAll(bookedDates);
        }
        mutableBookedDates.add(selectedDate);

        doctorsRef.child(selectedDoctor.getId()).child("bookedDates").setValue(mutableBookedDates)
                .addOnSuccessListener(aVoid -> Log.d("BookingActivity", "Doctor booked dates updated"))
                .addOnFailureListener(e -> Log.e("BookingActivity", "Failed to update booked dates", e));
    }

    // NOTIFICATION: Safe notification scheduling that won't break the booking process
    private void scheduleNotificationSafely(Appointment appointment) {
        try {
            NotificationScheduler.scheduleNotification(
                    this,
                    appointment.getDoctorName(),
                    appointment.getAppointmentDate(),
                    appointment.getAppointmentTime(),
                    appointment.getHospital()
            );
            Log.d("BookingActivity", "Notification scheduled successfully for Dr. " + appointment.getDoctorName());
        } catch (Exception e) {
            Log.e("BookingActivity", "Error scheduling notification (booking still successful): " + e.getMessage(), e);
            // NOTIFICATION: Don't show error to user - booking was successful, just notification failed
        }
    }

    // NOTIFICATION: Test notification method for immediate testing
    public void testNotificationNow(View view) {
        if (selectedDoctor != null) {
            String selectedTime = timeSpinner.getSelectedItem() != null ?
                    timeSpinner.getSelectedItem().toString() : "10:00 AM";

            NotificationScheduler.scheduleImmediateNotification(
                    this,
                    selectedDoctor.getName(),
                    "2025-09-01",
                    selectedTime,
                    selectedDoctor.getHospital()
            );
            Toast.makeText(this, "Test notification will appear in 5 seconds!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Please wait for doctor info to load", Toast.LENGTH_SHORT).show();
        }
    }
}