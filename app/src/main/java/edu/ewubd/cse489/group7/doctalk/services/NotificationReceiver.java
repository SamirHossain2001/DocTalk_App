package edu.ewubd.cse489.group7.doctalk.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import edu.ewubd.cse489.group7.doctalk.MainActivity;
import edu.ewubd.cse489.group7.doctalk.R;

// NOTIFICATION: Created BroadcastReceiver to replace Worker for notifications
public class NotificationReceiver extends BroadcastReceiver {
    private static final String TAG = "NotificationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "NotificationReceiver triggered");

        // NOTIFICATION: Extract appointment data from intent extras
        String doctorName = intent.getStringExtra("doctorName");
        String appointmentDate = intent.getStringExtra("appointmentDate");
        String appointmentTime = intent.getStringExtra("appointmentTime");
        String hospital = intent.getStringExtra("hospital");

        Log.d(TAG, "Showing notification for Dr. " + doctorName + " on " + appointmentDate);

        // NOTIFICATION: Show notification with appointment details
        showNotification(context, doctorName, appointmentDate, appointmentTime, hospital);
    }

    // NOTIFICATION: Method to display the appointment reminder notification
    private void showNotification(Context context, String doctorName, String appointmentDate,
                                  String appointmentTime, String hospital) {
        // NOTIFICATION: Create intent to open MainActivity when notification is tapped
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // NOTIFICATION: Build notification with enhanced styling and content
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "APPOINTMENT_CHANNEL")
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Appointment Reminder")
                .setContentText("Tomorrow: Dr. " + doctorName + " at " + appointmentTime)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Reminder: You have an appointment with Dr. " + doctorName +
                                " scheduled for tomorrow (" + appointmentDate + ") at " + appointmentTime +
                                (hospital != null ? "\nHospital: " + hospital : "")))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVibrate(new long[]{1000, 1000, 1000}) // NEW_NOTIFICATION: Added vibration pattern
                .setCategory(NotificationCompat.CATEGORY_REMINDER); // NEW_NOTIFICATION: Set notification category

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        // NOTIFICATION: Use unique notification ID based on doctor name and date
        int notificationId = (doctorName + appointmentDate).hashCode();
        notificationManager.notify(notificationId, builder.build());

        Log.d(TAG, "Notification displayed successfully");
    }
}