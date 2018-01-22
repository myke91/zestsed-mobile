package com.zestsed.mobile.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by michael.dugah on 12/1/2017.
 */

public class InvestmentDetails extends JSONObject {
    @JsonProperty("member_id")
    private int memberId;
    @JsonProperty("quota_month")
    private String quotaMonth;
    @JsonProperty("quota_year")
    private String quotaYear;
    @JsonProperty("quota_amount")
    private Double quotaAmount;
    @JsonProperty("quota_rollover")
    private Double quotaRollover;
    @JsonProperty("quota_with_interest")
    private Double quotaWithInterest;
    @JsonProperty("interest_amount")
    private Double interestAmount;
    @JsonProperty("cummulative_interest")
    private Double cummulativeInterest;

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

    public Double getCummulativeInterest() {
        return cummulativeInterest;
    }

    public void setCummulativeInterest(Double cummulativeInterest) {
        this.cummulativeInterest = cummulativeInterest;
    }
}
