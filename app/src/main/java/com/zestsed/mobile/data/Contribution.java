package com.zestsed.mobile.data;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by mdugah on 3/14/2017.
 */

public class Contribution extends JSONObject {

    int contributionId;
    String modeOfPayment;
    String sourceOfPayment;
    String vendorName;
    String dateOfContribution;
    String contributionAmount;
    String userEmail;
    int memberId;
    int isApproved;
    int isInvested;
    String dateOfApproval;
    String created_at;
    String updated_at;


    public Contribution() {
    }


    public Contribution(String json) throws JSONException {
        super(json);
    }


    public static Contribution load(String modeOfPayment, String sourceOfPayment, String vendorName, String dateOfContribution, String contributionAmount, String userEmail) {
        Map map = new LinkedHashMap<>();
        ObjectMapper mapper = new ObjectMapper();

        map.put("modeOfPayment", modeOfPayment);
        map.put("sourceOfPayment", sourceOfPayment);
        map.put("vendorName", vendorName);
        map.put("dateOfContribution", dateOfContribution);
        map.put("contributionAmount", contributionAmount);
        map.put("userEmail", userEmail);

        try {
            String json = mapper.writeValueAsString(map);
            return new Contribution(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getContributionId() {
        return contributionId;
    }

    public void setContributionId(int contributionId) {
        this.contributionId = contributionId;
    }

    public String getModeOfPayment() {
        return modeOfPayment;
    }

    public void setModeOfPayment(String modeOfPayment) {
        this.modeOfPayment = modeOfPayment;
    }

    public String getSourceOfPayment() {
        return sourceOfPayment;
    }

    public void setSourceOfPayment(String sourceOfPayment) {
        this.sourceOfPayment = sourceOfPayment;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getDateOfContribution() {
        return dateOfContribution;
    }

    public void setDateOfContribution(String dateOfContribution) {
        this.dateOfContribution = dateOfContribution;
    }

    public String getContributionAmount() {
        return contributionAmount;
    }

    public void setContributionAmount(String contributionAmount) {
        this.contributionAmount = contributionAmount;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
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

    public int getIsInvested() {
        return isInvested;
    }

    public void setIsInvested(int isInvested) {
        this.isInvested = isInvested;
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
