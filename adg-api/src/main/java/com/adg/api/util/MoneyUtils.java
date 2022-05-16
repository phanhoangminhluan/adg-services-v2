package com.adg.api.util;

import com.ibm.icu.text.NumberFormat;
import com.ibm.icu.text.RuleBasedNumberFormat;

import java.util.Locale;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.05.11 23:00
 */
public class MoneyUtils {

    public static String convertMoneyToText(double input) {
        String output = "";
        try {
            NumberFormat ruleBasedNumberFormat = new RuleBasedNumberFormat(new Locale("vi", "VN"), RuleBasedNumberFormat.SPELLOUT);
            output = ruleBasedNumberFormat.format(input) + " Đồng";
        } catch (Exception e) {
            output = "không đồng";
        }
        return output.toUpperCase();
    }

    public static void main(String[] args) {
        String str = convertMoneyToText(3217500000f);
        System.out.println(str);
    }


}
