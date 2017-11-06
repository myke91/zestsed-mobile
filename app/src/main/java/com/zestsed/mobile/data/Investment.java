package com.zestsed.mobile.data;

import java.util.List;

/**
 * Created by mdugah on 3/15/2017.
 */

public class Investment {
    String clientId;
    List<String> contributionKeys;
    Double totalAmount;
    Double rate;

    public Investment() {
    }

    public Investment(Double totalAmount, Double rate) {
        this.totalAmount = totalAmount;
        this.rate = rate;
    }

    public Investment(String clientId, List<String> contributionKeys, Double totalAmount, Double rate) {
        this.clientId = clientId;
        this.contributionKeys = contributionKeys;
        this.totalAmount = totalAmount;
        this.rate = rate;
    }

    public List<String> getContributionKeys() {
        return contributionKeys;
    }

    public void setContributionKeys(List<String> contributionKeys) {
        this.contributionKeys = contributionKeys;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }
}

