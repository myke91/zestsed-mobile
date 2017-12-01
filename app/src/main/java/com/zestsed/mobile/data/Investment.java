package com.zestsed.mobile.data;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mdugah on 3/15/2017.
 */

public class Investment extends JSONObject {
    private Double openingBalance;
    private Double totalContributions;

    public Investment() {
    }

    public Investment(String json) throws JSONException {
        super(json);
    }


    public static Investment load(Double openingBalance, Double totalContributions) {
        Map map = new LinkedHashMap<>();
        ObjectMapper mapper = new ObjectMapper();

        map.put("openingBalance", openingBalance);
        map.put("totalContributions", totalContributions);

        try {
            String json = mapper.writeValueAsString(map);
            return new Investment(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Double getOpeningBalance() {
        return openingBalance;
    }

    public void setOpeningBalance(Double openingBalance) {
        this.openingBalance = openingBalance;
    }

    public Double getTotalContributions() {
        return totalContributions;
    }

    public void setTotalContributions(Double totalContributions) {
        this.totalContributions = totalContributions;
    }
}

