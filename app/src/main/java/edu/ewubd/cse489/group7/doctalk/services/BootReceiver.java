package edu.ewubd.cse489.group7.doctalk.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import edu.ewubd.cse489.group7.doctalk.UserManager;
import edu.ewubd.cse489.group7.doctalk.models.Appointment;

// NEW_NOTIFICATION: Created BootReceiver to reschedule alarms after device reboot
public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "BootReceiver triggered with action: " + action);

        // NOTIFICATION: Handle boot completed and package replacement events
        if (Intent.ACTION_BOOT_COMPLETED.equals(action) ||
                Intent.ACTION_MY_PACKAGE_REPLACED.equals(action) ||
                Intent.ACTION_PACKAGE_REPLACED.equals(action)) {

            Log.d(TAG, "Rescheduling appointment notifications...");
            rescheduleAppointmentNotifications(context);
        }
    }

    // NOTIFICATION: Method to reschedule all future appointment notifications
    private void rescheduleAppointmentNotifications(Context context) {
        UserManager userManager = new UserManager(context);

        if (!userManager.isLoggedIn()) {
            Log.d(TAG, "User not logged in, skipping notification rescheduling");
            return;
        }

        String userEmail = userManager.getUserEmail();

        // NOTIFICATION: Access Firebase to get user's appointments
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://doctalk-1481c-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference appointmentsRef = database.getReference("appointments");

        appointmentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "Loading appointments for rescheduling...");

                for (DataSnapshot appointmentSnapshot : snapshot.getChildren()) {
                    Appointment appointment = appointmentSnapshot.getValue(Appointment.class);

                    // NOTIFICATION: Check if appointment belongs to current user and is in future
                    if (appointment != null &&
                            userEmail.equals(appointment.getUserEmail()) &&
                            isFutureAppointment(appointment.getAppointmentDate())) {

                        Log.d(TAG, "Rescheduling notification for Dr. " + appointment.getDoctorName());

                        // NOTIFICATION: Reschedule notification using NotificationScheduler
                        NotificationScheduler.scheduleNotification(
                                context,
                                appointment.getDoctorName(),
                                appointment.getAppointmentDate(),
                                appointment.getAppointmentTime(),
                                appointment.getHospital()
                        );
                    }
                }

                Log.d(TAG, "Notification rescheduling completed");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load appointments for rescheduling: " + error.getMessage());
            }
        });
    }

    // NOTIFICATION: Helper method to check if appointment is in the future
    private boolean isFutureAppointment(String appointmentDateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date appointmentDate = sdf.parse(appointmentDateStr);
            Date currentDate = new Date();

            return appointmentDate != null && appointmentDate.after(currentDate);
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing appointment date: " + appointmentDateStr, e);
            return false;
        }
    }
}