package com.mastersoft.steb.bymeapp.utils;

import android.text.TextUtils;
import android.util.Patterns;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ValidationFunc {

    public static boolean isValidDate(String input, String format) {
        boolean valid = false;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.ITALY);
            //dateFormat.setLenient(false) ;
            String output = dateFormat.format(dateFormat.parse(input));
            valid = input.equals(output);
        } catch (Exception ignore) {}

        return valid;
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public static boolean isValidMoney(String text) {
        boolean valid=false;
        int dotPos = text.indexOf(".");
        if ((dotPos==0) || (dotPos>0 && text.lastIndexOf(".")==text.length()-2)) valid=true;
        return valid;
   }


}
