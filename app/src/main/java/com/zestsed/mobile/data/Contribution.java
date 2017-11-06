package com.zestsed.mobile.data;

/**
 * Created by mdugah on 3/14/2017.
 */

public class Contribution {
    Client client;
    String date;
    Double amount;

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
