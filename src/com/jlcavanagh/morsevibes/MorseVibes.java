package com.jlcavanagh.morsevibes;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Menu;

public class MorseVibes extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_morse_vibes);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.morse_vibes, menu);
		return true;
	}

}
