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
    Double amount;
    Double rate;
    String dateOfInvestment;

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

    public InvestmentDetails(Double totalAmount, Double rate) {
        this.amount = totalAmount;
        this.rate = rate;
    }

    public Double getAmount() {
        return amount;
    }

    public String getDateOfInvestment() {
        return dateOfInvestment;
    }

    public void setDateOfInvestment(String dateOfInvestment) {
        this.dateOfInvestment = dateOfInvestment;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }
}
