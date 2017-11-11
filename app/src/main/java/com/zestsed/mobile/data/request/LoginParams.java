package com.zestsed.mobile.data.request;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by michael.dugah on 11/9/2017.
 */

public class LoginParams extends JSONObject {

    String email;
    String password;

    public LoginParams(String json) throws JSONException {
        super(json);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
