package edu.ewubd.cse489.group7.doctalk.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Doctor {
    private String id;
    private String name;
    private String image;
    private String education;
    private double fee;
    private String hospital;
    private String specialization;
    private Object workingHours;
    private Object bookedDates;

    public Doctor() {}

    public Doctor(String id, String name, String image, String education, double fee,
                  String hospital, String specialization, List<String> workingHours) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.education = education;
        this.fee = fee;
        this.hospital = hospital;
        this.specialization = specialization;
        this.workingHours = workingHours != null ? workingHours : new ArrayList<>();
        this.bookedDates = new ArrayList<>();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getEducation() { return education; }
    public void setEducation(String education) { this.education = education; }

    public double getFee() { return fee; }
    public void setFee(double fee) { this.fee = fee; }

    public String getHospital() { return hospital; }
    public void setHospital(String hospital) { this.hospital = hospital; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    // Handle String and List for workingHours
    public List<String> getWorkingHours() {
        if (workingHours == null) {
            return new ArrayList<>();
        }
        if (workingHours instanceof List) {
            return new ArrayList<>((List<String>) workingHours);
        } else if (workingHours instanceof String) {
            // for str, split by comma or return as single item
            String hours = (String) workingHours;
            if (hours.contains(",")) {
                return new ArrayList<>(Arrays.asList(hours.split(",")));
            } else {
                List<String> hoursList = new ArrayList<>();
                hoursList.add(hours);
                return hoursList;
            }
        }
        return new ArrayList<>();
    }

    public void setWorkingHours(Object workingHours) {
        this.workingHours = workingHours;
    }

    public List<String> getBookedDates() {
        if (bookedDates == null) {
            return new ArrayList<>();
        }
        if (bookedDates instanceof List) {
            return new ArrayList<>((List<String>) bookedDates);
        } else if (bookedDates instanceof String) {
            String dates = (String) bookedDates;
            if (dates.contains(",")) {
                return new ArrayList<>(Arrays.asList(dates.split(",")));
            } else {
                List<String> datesList = new ArrayList<>();
                datesList.add(dates);
                return datesList;
            }
        }
        return new ArrayList<>();
    }

    public void setBookedDates(Object bookedDates) {
        this.bookedDates = bookedDates;
    }
}
