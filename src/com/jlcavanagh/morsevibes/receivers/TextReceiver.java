package com.jlcavanagh.morsevibes.receivers;

import java.util.HashMap;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
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

                //Pull the contact by phone number
                if(msgFrom != null) {
                    //Assemble the filter URI
                    Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(msgFrom));
                    String name = "";

                    //Query the contacts DB and grab a cursor
                    ContentResolver contentResolver = context.getContentResolver();
                    Cursor contactLookup = contentResolver.query(uri, new String[] {BaseColumns._ID,
                            ContactsContract.PhoneLookup.DISPLAY_NAME }, null, null, null);

                    try {
                        //If we found anything, pull the name
                        if (contactLookup != null && contactLookup.getCount() > 0) {
                            contactLookup.moveToNext();
                            name = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                            //String contactId = contactLookup.getString(contactLookup.getColumnIndex(BaseColumns._ID));
                        } else {
                            //FIXME: Fallback of some sort?  Configurable?
                            Log.d(TAG, "Could not find contact for phone: " + msgFrom);
                        }
                    } finally {
                        if (contactLookup != null) {
                            contactLookup.close();
                        }
                    }

                    //Vibe first letter of name
                    if(name != null) {
                        //Get the morse code for the first character
                        Character firstChar = name.toUpperCase().charAt(0);
                        String code = morse.get(firstChar);

                        if(code != null) {
                            //If we got a code, assemble the vibration pattern
                            long[] vibePattern = new long[code.length() * 2];

                            //Vibration pattern is [time off, time on, time off, ...]
                            for(int x = 0; x < code.length(); x++) {
                                vibePattern[x * 2] = PAUSE;

                                //If zero, vibe short, else vibe long
                                if(code.charAt(x) == '0') {
                                    vibePattern[(x * 2) + 1] = SHORT;
                                } else {
                                    vibePattern[(x * 2) + 1] = LONG;
                                }
                            }

                            //Dat vibe
                            v.vibrate(vibePattern, -1);
                        } else {
                            Log.d(TAG, "No morse mapping for char: " + firstChar);
                        }
                    } else {
                        Log.d(TAG, "Could not find name for contact with phone: " + msgFrom);
                    }
                }
            } catch(Exception e){
                Log.d(TAG, e.getMessage());
            }
        }
    }
}
