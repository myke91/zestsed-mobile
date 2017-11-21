package com.zestsed.mobile.data;

import java.util.regex.Pattern;

/**
 * Created by michael.dugah on 11/9/2017.
 */

public class Constants {
    public static String BACKEND_BASE_URL = "http://10.0.2.2:8000";
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
}
