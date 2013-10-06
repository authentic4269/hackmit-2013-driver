package com.app.drivesafe;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class BluetoothActivity extends Activity {
	TBlue tBlue = null;
	TextView outputTv;

	// Debugging
	private static final String TAG = "BluetoothActivity";
	private static final boolean D = true;

	// Intent request codes
	private final static int REQUEST_ENABLE_BT = 1;
	private static final int REQUEST_CONNECT_DEVICE = 2;
	
	// Presets!
	String address = null;

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
		if (address == null) {
			Intent serverIntent = new Intent(this, DeviceListActivity.class);
			startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
		} else {
			connectDevice();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (tBlue != null) {
			tBlue.close();
		}
	}

	public void initGUI() {
		LinearLayout container = new LinearLayout(this);
		outputTv = new TextView(this);
		container.addView(outputTv);
		setContentView(container);
	}
	
	public void connectDevice() {
		Log.d("yolo", "hi5");
		tBlue = new TBlue(address);
		if (tBlue.streaming()) {
			outputTv.append("Connected succesfully! \n");
		} else {
			outputTv.append("Error: Failed to connect. \n");
		}
		
		int i = 0;
		while (tBlue.streaming() && (i < 50)) {
			outputTv.append(tBlue.read() + "\n");
			i ++;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(D) Log.d(TAG, "onActivityResult " + resultCode);
		switch (requestCode) {
			case REQUEST_CONNECT_DEVICE:
				// When DeviceListActivity returns with a device to connect
				if (resultCode == Activity.RESULT_OK) {
					address = data.getExtras()
				            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
					connectDevice();
				}
				break;
			case REQUEST_ENABLE_BT:
				// When the request to enable Bluetooth returns
				if (resultCode == Activity.RESULT_OK) {
					// Bluetooth is now enabled, so let's start over
					onResume();
				} else {
					// User did not enable Bluetooth or an error occurred
					Log.d(TAG, "BT not enabled");
					Toast.makeText(this, "Bluetooth isn't enabled. kthxbai.", Toast.LENGTH_SHORT).show();
					finish();
				}
        }
    }
}