package com.tonsser.utils;

public class TInteger {

    public static int parseInt(String input, int defaultValue) {
        try {
            return Integer.parseInt(input);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
