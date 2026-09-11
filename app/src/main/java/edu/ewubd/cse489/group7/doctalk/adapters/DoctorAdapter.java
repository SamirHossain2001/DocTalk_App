package edu.ewubd.cse489.group7.doctalk.adapters;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import edu.ewubd.cse489.group7.doctalk.BookingActivity;
import edu.ewubd.cse489.group7.doctalk.LoginActivity;
import edu.ewubd.cse489.group7.doctalk.R;
import edu.ewubd.cse489.group7.doctalk.models.Doctor;
import edu.ewubd.cse489.group7.doctalk.UserManager;

import java.util.List;

public class DoctorAdapter extends BaseAdapter {
    private Context context;
    private List<Doctor> doctorList;
    private LayoutInflater inflater;
    private UserManager userManager;

    public DoctorAdapter(Context context, List<Doctor> doctorList) {
        this.context = context;
        this.doctorList = doctorList;
        this.inflater = LayoutInflater.from(context);
        this.userManager = new UserManager(context);
    }

    @Override
    public int getCount() {
        return doctorList.size();
    }

    @Override
    public Object getItem(int position) {
        return doctorList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.doctor_item, parent, false);
            holder = new ViewHolder();
            holder.doctorImage = convertView.findViewById(R.id.ivDoctorImage);
            holder.doctorName = convertView.findViewById(R.id.tvDoctorName);
            holder.hospitalName = convertView.findViewById(R.id.tvHospitalName);
            holder.specialization = convertView.findViewById(R.id.tvSpecialization);
            holder.fee = convertView.findViewById(R.id.tvFee);
            holder.bookButton = convertView.findViewById(R.id.btnBook);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Doctor doctor = doctorList.get(position);

        holder.doctorName.setText("Dr. " + doctor.getName());
        holder.hospitalName.setText(doctor.getHospital());
        holder.specialization.setText(doctor.getSpecialization());
        holder.fee.setText("Fee: " + doctor.getFee()+" TK.");

        // Load image with Glide
        Glide.with(context)
                .load(doctor.getImage())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_gallery)
                .into(holder.doctorImage);

        holder.bookButton.setOnClickListener(v -> {
            // Check user is logged in
            if (userManager.isLoggedIn()) {
                Intent intent = new Intent(context, BookingActivity.class);
                intent.putExtra("doctorId", doctor.getId());
                context.startActivity(intent);
            } else {
                // User not logged in, redirect to login with doctor ID
                Intent intent = new Intent(context, LoginActivity.class);
                intent.putExtra("doctorId", doctor.getId());
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        ImageView doctorImage;
        TextView doctorName;
        TextView hospitalName;
        TextView specialization;
        TextView fee;
        Button bookButton;
    }
}