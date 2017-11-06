package com.zestsed.mobile.data;

/**
 * Created by mdugah on 3/13/2017.
 */

public class Client {


    String fullname;
    String email;
    String genderSpinner;
    String phoneNumber;
    String dateOfBirth;
    String nextOfKin;
    String nextOfKinPhoneNumber;
    String occupation;
    String residentialAddress;
    String hometown;
    String highSchool;
    String college;

    public Client() {
    }

    public Client(String fullname, String email) {
        this.fullname = fullname;
        this.email = email;
    }

    public Client(String fullname, String email, String genderSpinner, String phoneNumber, String dateOfBirth, String nextOfKin, String nextOfKinPhoneNumber) {
        this.fullname = fullname;
        this.email = email;
        this.genderSpinner = genderSpinner;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.nextOfKin = nextOfKin;
        this.nextOfKinPhoneNumber = nextOfKinPhoneNumber;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getResidentialAddress() {
        return residentialAddress;
    }

    public void setResidentialAddress(String residentialAddress) {
        this.residentialAddress = residentialAddress;
    }

    public String getHometown() {
        return hometown;
    }

    public void setHometown(String hometown) {
        this.hometown = hometown;
    }

    public String getHighSchool() {
        return highSchool;
    }

    public void setHighSchool(String highSchool) {
        this.highSchool = highSchool;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGenderSpinner() {
        return genderSpinner;
    }

    public void setGenderSpinner(String genderSpinner) {
        this.genderSpinner = genderSpinner;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getNextOfKin() {
        return nextOfKin;
    }

    public void setNextOfKin(String nextOfKin) {
        this.nextOfKin = nextOfKin;
    }

    public String getNextOfKinPhoneNumber() {
        return nextOfKinPhoneNumber;
    }

    public void setNextOfKinPhoneNumber(String nextOfKinPhoneNumber) {
        this.nextOfKinPhoneNumber = nextOfKinPhoneNumber;
    }
}
