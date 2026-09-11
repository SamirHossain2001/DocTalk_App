package edu.ewubd.cse489.group7.doctalk.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

// NOTIFICATION: Created NotificationScheduler utility class to handle alarm scheduling
public class NotificationScheduler {
    private static final String TAG = "NotificationScheduler";

    // NOTIFICATION: Schedule notification 1 day before appointment
    public static void scheduleNotification(Context context, String doctorName, String appointmentDate,
                                            String appointmentTime, String hospital) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date appointmentDateObj = sdf.parse(appointmentDate);

            if (appointmentDateObj != null) {
                Calendar appointmentCal = Calendar.getInstance();
                appointmentCal.setTime(appointmentDateObj);
                appointmentCal.add(Calendar.DAY_OF_MONTH, -1); // NEW_NOTIFICATION: 1 day before
                appointmentCal.set(Calendar.HOUR_OF_DAY, 9); // NEW_NOTIFICATION: Set reminder at 9 AM
                appointmentCal.set(Calendar.MINUTE, 0);
                appointmentCal.set(Calendar.SECOND, 0);

                long notificationTime = appointmentCal.getTimeInMillis();
                long currentTime = System.currentTimeMillis();

                // NOTIFICATION: Only schedule if notification time is in the future
                if (notificationTime > currentTime) {
                    scheduleAlarm(context, doctorName, appointmentDate, appointmentTime, hospital, notificationTime);
                    Log.d(TAG, "Notification scheduled for " + appointmentCal.getTime());
                } else {
                    Log.d(TAG, "Notification time has passed, not scheduling");
                }
            }
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing appointment date: " + appointmentDate, e);
        }
    }

    // NOTIFICATION: Schedule immediate notification for testing (5 seconds delay)
    public static void scheduleImmediateNotification(Context context, String doctorName,
                                                     String appointmentDate, String appointmentTime, String hospital) {
        long notificationTime = System.currentTimeMillis() + 5000; // NEW_NOTIFICATION: 5 seconds from now
        scheduleAlarm(context, doctorName, appointmentDate, appointmentTime, hospital, notificationTime);
        Log.d(TAG, "Immediate test notification scheduled for 5 seconds from now");
    }

    // NOTIFICATION: Private method to create and schedule the actual alarm
    private static void scheduleAlarm(Context context, String doctorName, String appointmentDate,
                                      String appointmentTime, String hospital, long triggerTime) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // NOTIFICATION: Create intent for BroadcastReceiver
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.setAction("edu.ewubd.cse489.group7.doctalk.APPOINTMENT_REMINDER");
        intent.putExtra("doctorName", doctorName);
        intent.putExtra("appointmentDate", appointmentDate);
        intent.putExtra("appointmentTime", appointmentTime);
        intent.putExtra("hospital", hospital);

        // NOTIFICATION: Create unique request code using doctor name and date hash
        int requestCode = (doctorName + appointmentDate).hashCode();

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // NOTIFICATION: Use exact alarm for precise timing
        if (alarmManager != null) {
            try {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                Log.d(TAG, "Alarm scheduled successfully for request code: " + requestCode);
            } catch (SecurityException e) {
                Log.e(TAG, "Permission denied for setting exact alarm", e);
                // NEW_NOTIFICATION: Fallback to regular alarm if exact alarm permission denied
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            }
        }
    }

    // NOTIFICATION: Method to cancel a scheduled notification
    public static void cancelNotification(Context context, String doctorName, String appointmentDate) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.setAction("edu.ewubd.cse489.group7.doctalk.APPOINTMENT_REMINDER");

        int requestCode = (doctorName + appointmentDate).hashCode();

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
            Log.d(TAG, "Notification cancelled for request code: " + requestCode);
        }
    }
}