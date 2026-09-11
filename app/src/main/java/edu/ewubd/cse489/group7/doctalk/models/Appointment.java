package edu.ewubd.cse489.group7.doctalk.models;
public class Appointment {
    private String id;
    private String doctorId;
    private String doctorName;
    private String patientName;
    private String patientPhone;
    private String userEmail;
    private String appointmentDate;
    private String appointmentTime;
    private String hospital;
    private double fee;
    private long timestamp;

    public Appointment() {}

    public Appointment(String id, String doctorId, String doctorName, String patientName,
                       String patientPhone, String userEmail, String appointmentDate,
                       String appointmentTime, String hospital, double fee) {
        this.id = id;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.patientName = patientName;
        this.patientPhone = patientPhone;
        this.userEmail = userEmail;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.hospital = hospital;
        this.fee = fee;
        this.timestamp = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getPatientPhone() { return patientPhone; }
    public void setPatientPhone(String patientPhone) { this.patientPhone = patientPhone; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(String appointmentDate) { this.appointmentDate = appointmentDate; }

    public String getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(String appointmentTime) { this.appointmentTime = appointmentTime; }

    public String getHospital() { return hospital; }
    public void setHospital(String hospital) { this.hospital = hospital; }

    public double getFee() { return fee; }
    public void setFee(double fee) { this.fee = fee; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}