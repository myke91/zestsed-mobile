package com.zestsed.mobile.data;


import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by mdugah on 3/13/2017.
 */

public class Client extends JSONObject {


    String firstname;
    String lastname;
    String othernames;
    String email;
    String gender;
    String phoneNumber;
    String dateOfBirth;
    String nextOfKin;
    String nextOfKinPhoneNumber;
    String occupation;
    String residentialAddress;
    String purposeOfInvesting;

    public Client() {
    }

    public Client(Map copyFrom) {
        super(copyFrom);
    }

    public Client(JSONTokener readFrom) throws JSONException {
        super(readFrom);
    }

    public Client(String json) throws JSONException {
        super(json);
    }

    public Client(JSONObject copyFrom, String[] names) throws JSONException {
        super(copyFrom, names);
    }

    public Client(String firstname, String email) {
        this.firstname = firstname;
        this.email = email;
    }

    public Client(String firstname, String lastname, String othernames, String email, String gender, String phoneNumber, String dateOfBirth,
                  String nextOfKin, String nextOfKinPhoneNumber, String occupation, String residentialAddress, String purposeOfInvesting) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.othernames = othernames;
        this.email = email;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.nextOfKin = nextOfKin;
        this.nextOfKinPhoneNumber = nextOfKinPhoneNumber;
        this.occupation = occupation;
        this.residentialAddress = residentialAddress;
        this.purposeOfInvesting = purposeOfInvesting;
    }

    public static Client load(String firstName, String lastName, String otherNames, String email, String gender, String phoneNumber, String dateOfBirth,
                              String nextOfKin, String nextOfKinPhoneNumber, String occupation, String residentialAddress, String purposeOfInvesting) {
        Map map = new LinkedHashMap<>();
        ObjectMapper mapper = new ObjectMapper();

        map.put("firstName", firstName);
        map.put("lastName", lastName);
        map.put("otherNames", otherNames);
        map.put("email", email);
        map.put("gender", gender);
        map.put("phoneNumber", phoneNumber);
        map.put("dateOfBirth", dateOfBirth);
        map.put("nextOfKin", nextOfKin);
        map.put("nextOfKinTelephone", nextOfKinPhoneNumber);
        map.put("occupation", occupation);
        map.put("residentialAddress", residentialAddress);
        map.put("purposeOfInvesting", purposeOfInvesting);

        try {
            String json = mapper.writeValueAsString(map);
            return new Client(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

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


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getOthernames() {
        return othernames;
    }

    public void setOthernames(String othernames) {
        this.othernames = othernames;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPurposeOfInvesting() {
        return purposeOfInvesting;
    }

    public void setPurposeOfInvesting(String purposeOfInvesting) {
        this.purposeOfInvesting = purposeOfInvesting;
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
