package com.smiligenceTestenv.techUser.common;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.smiligenceTestenv.techUser.common.Constant.EMAIL_PATTERN;
import static com.smiligenceTestenv.techUser.common.Constant.INDIA_PINCODE;
import static com.smiligenceTestenv.techUser.common.Constant.PERCENTAGE_PATTERN;
import static com.smiligenceTestenv.techUser.common.Constant.TAMIL_NADU_PINCODE;
import static com.smiligenceTestenv.techUser.common.Constant.USER_NAME_PATTERN;

public class TextUtils {
    public static <T> List<T> removeDuplicatesList(List<T> list) {


        Set<T> set = new LinkedHashSet<>();

        // Add the elements to set
        set.addAll(list);


        list.clear();


        list.addAll(set);


        return list;
    }

    public static boolean validatePhoneNumber(String phoneNo) {
        if (phoneNo.matches("^[6789]\\d{9}$"))
            return true;
        else return false;
    }

    public static boolean validPinCode(final String firstName) {
        Pattern pattern = Pattern.compile(INDIA_PINCODE);
        Matcher matcher = pattern.matcher(firstName);

        return matcher.matches();

    }
    public static boolean validTamilNaduPinCode(final String pincode) {
        Pattern pattern = Pattern.compile(TAMIL_NADU_PINCODE);
        Matcher matcher = pattern.matcher(pincode);

        return matcher.matches();

    }

    public static boolean validPercentage(String percent) {
        Pattern pattern = Pattern.compile(PERCENTAGE_PATTERN);
        Matcher matcher = pattern.matcher(percent);
        return matcher.matches();
    }

    public static boolean isValidUserName(final String userName) {
        Pattern pattern = Pattern.compile(USER_NAME_PATTERN);
        Matcher matcher = pattern.matcher(userName);
        return matcher.matches();
    }

    public static boolean isValidEmail(final String email) {

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
