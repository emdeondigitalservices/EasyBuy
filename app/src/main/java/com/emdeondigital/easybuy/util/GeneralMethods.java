package com.emdeondigital.easybuy.util;

import android.telephony.SmsManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GeneralMethods {

    // Generic function to convert set to list
    public static <String> List<String> convertSetToList(Set<String> set)
    {
        // create an empty list
        List<String> list = new ArrayList<>();

        // push each element in the set into the list
        if(set != null){
            for (String t : set)
                list.add(t);
        }
        // return the list
        return list;
    }

    public static void sendSMS(String mobNumber, String message, String scAddress,String sentIntent,String deliveryIntent){
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(mobNumber, null, message,
                null, null);
    }
}
