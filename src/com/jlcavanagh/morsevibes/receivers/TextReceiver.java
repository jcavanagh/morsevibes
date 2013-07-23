package com.jlcavanagh.morsevibes.receivers;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;

import android.util.Log;

public class TextReceiver extends BroadcastReceiver {

    private static final String TAG = TextReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive called");
    }
}
