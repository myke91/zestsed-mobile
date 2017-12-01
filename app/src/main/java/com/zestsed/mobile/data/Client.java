package com.zestsed.mobile.data;


import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by mdugah on 3/13/2017.
 */

public class Client extends JSONObject {

    int registrationId;
    String firstName;
    String lastName;
    String otherNames;
    String email;
    String gender;
    String phoneNumber;
    String dateOfBirth;
    String nextOfKin;
    String nextOfKinTelephone;
    String occupation;
    String residentialAddress;
    String purposeOfInvesting;
    int isApproved;
    String dateOfApproval;
    String created_at;
    String updated_at;

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

    public Client(String firstName, String email) {
        this.firstName = firstName;
        this.email = email;
    }

    public Client(String firstName, String lastName, String otherNames, String email, String gender, String phoneNumber, String dateOfBirth,
                  String nextOfKin, String nextOfKinTelephone, String occupation, String residentialAddress, String purposeOfInvesting) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.otherNames = otherNames;
        this.email = email;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.nextOfKin = nextOfKin;
        this.nextOfKinTelephone = nextOfKinTelephone;
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

    public int getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(int registrationId) {
        this.registrationId = registrationId;
    }

    public String getFullName(){
        if(otherNames != null){
            return firstName+" "+otherNames+" "+lastName;
        }
        return firstName+" "+lastName;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getOtherNames() {
        return otherNames;
    }

    public void setOtherNames(String otherNames) {
        this.otherNames = otherNames;
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

    public String getNextOfKinTelephone() {
        return nextOfKinTelephone;
    }

    public void setNextOfKinTelephone(String nextOfKinTelephone) {
        this.nextOfKinTelephone = nextOfKinTelephone;
    }

    public int getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(int isApproved) {
        this.isApproved = isApproved;
    }

    public String getDateOfApproval() {
        return dateOfApproval;
    }

    public void setDateOfApproval(String dateOfApproval) {
        this.dateOfApproval = dateOfApproval;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
}
