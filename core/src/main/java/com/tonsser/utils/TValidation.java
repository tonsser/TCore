package com.tonsser.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TValidation {

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    /**
     * @param email
     * @return boolean
     * @author Martin - 2012
     * Use this method to valid email
     */
    public static boolean isValidEmailString(String email) {
        Pattern p = Pattern.compile(EMAIL_PATTERN);
        Matcher m = p.matcher(email);
        boolean matchFound = m.matches();
        return matchFound;
    }
}
