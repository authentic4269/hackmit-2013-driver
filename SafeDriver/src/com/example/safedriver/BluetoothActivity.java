package com.example.safedriver;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;


public class BluetoothActivity extends Activity {
	TBlue tBlue;
	TextView messagesTv;

	// Debugging
	private static final String TAG = "BluetoothActivity";
	private static final boolean D = true;

	// Intent request code
	private final static int REQUEST_ENABLE_BT = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initGUI();
	}

	@Override
	public void onResume() {
		super.onResume();
		// If BT is not on, request that it be enabled.
		if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		}
		tBlue = new TBlue();
		if (tBlue.streaming()) {
			messagesTv.append("Connected succesfully! ");
		} else {
			messagesTv.append("Error: Failed to connect. ");
		}
		String s="";
		while (tBlue.streaming() && (s.length()<10) ) {
			s+=tBlue.read();
		}
		messagesTv.append("Read from Bluetooth: \n"+s);
	}

	@Override
	public void onPause() {
		super.onPause();
		tBlue.close();
	}

	public void initGUI() {
		LinearLayout container=new LinearLayout(this);
		messagesTv = new TextView(this);
		container.addView(messagesTv);
		setContentView(container);
	}
}