package edu.ewubd.cse489.group7.doctalk.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import edu.ewubd.cse489.group7.doctalk.R;
import edu.ewubd.cse489.group7.doctalk.models.Appointment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AppointmentAdapter extends BaseAdapter {
    private Context context;
    private List<Appointment> appointmentList;
    private LayoutInflater inflater;

    public AppointmentAdapter(Context context, List<Appointment> appointmentList) {
        this.context = context;
        this.appointmentList = appointmentList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return appointmentList.size();
    }

    @Override
    public Object getItem(int position) {
        return appointmentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.appointment_item, parent, false);
            holder = new ViewHolder();
            holder.doctorName = convertView.findViewById(R.id.appointmentDoctorName);
            holder.patientName = convertView.findViewById(R.id.appointmentPatientName);
            holder.date = convertView.findViewById(R.id.appointmentDate);
            holder.time = convertView.findViewById(R.id.appointmentTime);
            holder.hospital = convertView.findViewById(R.id.appointmentHospital);
            holder.fee = convertView.findViewById(R.id.appointmentFee);
            holder.bookingDate = convertView.findViewById(R.id.bookingDate);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Appointment appointment = appointmentList.get(position);

        holder.doctorName.setText("Dr. "+appointment.getDoctorName());
        holder.patientName.setText("Patient: " + appointment.getPatientName());
        holder.date.setText("Date: " +appointment.getAppointmentDate());
        holder.time.setText("Time: " + appointment.getAppointmentTime());
        holder.hospital.setText("Hospital: " + appointment.getHospital());
        holder.fee.setText("Fee: "+appointment.getFee()+" TK.");

        // Format booking date
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        String formattedDate = sdf.format(new Date(appointment.getTimestamp()));
        holder.bookingDate.setText("Booked on: " + formattedDate);

        return convertView;
    }

    private static class ViewHolder {
        TextView doctorName;
        TextView patientName;
        TextView date;
        TextView time;
        TextView hospital;
        TextView fee;
        TextView bookingDate;
    }
}