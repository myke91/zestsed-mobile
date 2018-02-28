package com.zestsed.mobile.data;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by michael.dugah on 12/1/2017.
 */

public class InvestmentDetails extends JSONObject {
    private int investmentId;
    private int memberId;
    private String quotaMonth;
    private String quotaYear;
    private Double cycleMonth;
    private Double cycleYear;
    private Double quotaAmount;
    private Double quotaRollover;
    private Double quotaWithInterest;
    private Double interestAmount;
    private Double cumulativeInterest;

    public InvestmentDetails() {
    }


    public InvestmentDetails(String json) throws JSONException {
        super(json);
    }

    public InvestmentDetails load(Double amount, Double rate, String dateOfInvestment) {
        Map map = new LinkedHashMap<>();
        ObjectMapper mapper = new ObjectMapper();

        map.put("amount", amount);
        map.put("rate", rate);
        map.put("dateOfInvestment", dateOfInvestment);

        try {
            String json = mapper.writeValueAsString(map);
            return new InvestmentDetails(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getInvestmentId() {
        return investmentId;
    }

    public void setInvestmentId(int investmentId) {
        this.investmentId = investmentId;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public String getQuotaMonth() {
        return quotaMonth;
    }

    public void setQuotaMonth(String quotaMonth) {
        this.quotaMonth = quotaMonth;
    }

    public String getQuotaYear() {
        return quotaYear;
    }

    public void setQuotaYear(String quotaYear) {
        this.quotaYear = quotaYear;
    }

    public Double getCycleMonth() {
        return cycleMonth;
    }

    public void setCycleMonth(Double cycleMonth) {
        this.cycleMonth = cycleMonth;
    }

    public Double getCycleYear() {
        return cycleYear;
    }

    public void setCycleYear(Double cycleYear) {
        this.cycleYear = cycleYear;
    }

    public Double getQuotaAmount() {
        return quotaAmount;
    }

    public void setQuotaAmount(Double quotaAmount) {
        this.quotaAmount = quotaAmount;
    }

    public Double getQuotaRollover() {
        return quotaRollover;
    }

    public void setQuotaRollover(Double quotaRollover) {
        this.quotaRollover = quotaRollover;
    }

    public Double getQuotaWithInterest() {
        return quotaWithInterest;
    }

    public void setQuotaWithInterest(Double quotaWithInterest) {
        this.quotaWithInterest = quotaWithInterest;
    }

    public Double getInterestAmount() {
        return interestAmount;
    }

    public void setInterestAmount(Double interestAmount) {
        this.interestAmount = interestAmount;
    }

    public Double getCumulativeInterest() {
        return cumulativeInterest;
    }

    public void setCumulativeInterest(Double cumulativeInterest) {
        this.cumulativeInterest = cumulativeInterest;
    }
}
