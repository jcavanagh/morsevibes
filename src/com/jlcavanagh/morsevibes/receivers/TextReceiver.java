package com.jlcavanagh.morsevibes.receivers;

import java.util.HashMap;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.SmsMessage;
import android.util.Log;

public class TextReceiver extends BroadcastReceiver {

    private static final String TAG = TextReceiver.class.getSimpleName();

    //FIXME: Make these configurable
    private static final int SHORT = 250;
    private static final int LONG = 500;
    private static final int PAUSE = 20;

    private static final HashMap<Character, String> morse;

    static {
        //Short = 0, long = 1
        morse = new HashMap<Character, String>();
        morse.put('A', "01");
        morse.put('B', "1000");
        morse.put('C', "1010");
        morse.put('D', "100");
        morse.put('E', "0");
        morse.put('F', "0010");
        morse.put('G', "110");
        morse.put('H', "0000");
        morse.put('I', "00");
        morse.put('J', "0111");
        morse.put('K', "101");
        morse.put('L', "0100");
        morse.put('M', "11");
        morse.put('N', "10");
        morse.put('O', "111");
        morse.put('P', "0110");
        morse.put('Q', "1101");
        morse.put('R', "010");
        morse.put('S', "000");
        morse.put('T', "1");
        morse.put('U', "001");
        morse.put('V', "0001");
        morse.put('W', "011");
        morse.put('X', "1001");
        morse.put('Y', "1011");
        morse.put('Z', "1100");

        morse.put('1', "01111");
        morse.put('2', "00111");
        morse.put('3', "00011");
        morse.put('4', "00001");
        morse.put('5', "00000");
        morse.put('6', "10000");
        morse.put('7', "11000");
        morse.put('8', "11100");
        morse.put('9', "11110");
        morse.put('0', "11111");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive called");

        Vibrator v = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        Bundle extra = intent.getExtras();
        SmsMessage[] msgs;
        String msgFrom = "", msgBody = "";

        if (extra != null) {
            try {
                //Assemble message from this insanity
                Object[] pdus = (Object[]) extra.get("pdus");
                msgs = new SmsMessage[pdus.length];

                //Assemble each chunk into a single message
                for(int i=0; i<msgs.length; i++){
                    msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                    msgFrom = msgs[i].getOriginatingAddress();
                    msgBody += msgs[i].getMessageBody();
                }

                Log.d(TAG, "msgFrom: " + msgFrom);
                Log.d(TAG, "msgBody: " + msgBody);
            } catch(Exception e){
                Log.d(TAG, e.getMessage());
            }
        }
    }
}
