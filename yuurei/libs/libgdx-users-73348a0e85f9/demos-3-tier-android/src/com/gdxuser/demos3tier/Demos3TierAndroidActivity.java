package com.gdxuser.demos3tier;

import android.os.Bundle;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.tests.TestCollection;

import com.pikkle.isomap.IsoMap;

public class Demos3TierAndroidActivity extends AndroidApplication {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// initialize(new TestCollection(), false);
		IsoMap iso = new IsoMap();
		initialize(iso, false);
		// setContentView(R.layout.main);
	}
}