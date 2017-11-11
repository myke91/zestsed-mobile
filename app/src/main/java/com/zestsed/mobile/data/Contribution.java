package com.zestsed.mobile.data;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Map;

/**
 * Created by mdugah on 3/14/2017.
 */

public class Contribution extends JSONObject {
    Client client;
    String date;
    Double amount;

    public Contribution(Map copyFrom) {
        super(copyFrom);
    }

    public Contribution(JSONTokener readFrom) throws JSONException {
        super(readFrom);
    }

    public Contribution(String json) throws JSONException {
        super(json);
    }

    public Contribution(JSONObject copyFrom, String[] names) throws JSONException {
        super(copyFrom, names);
    }

    public Contribution() {
    }

    public Contribution(String date, Double amount) {
        this.date = date;
        this.amount = amount;
    }

    public Contribution(Client client, String date, Double amount) {
        this.client = client;
        this.date = date;
        this.amount = amount;
    }

    public static Contribution load(){
        String json = "";
        try {
            return new Contribution(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
