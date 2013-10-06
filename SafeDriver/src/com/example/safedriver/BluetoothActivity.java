package com.example.safedriver;

import java.io.InputStream;
import java.io.OutputStream;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BluetoothActivity extends Activity {

	/** Bluetooth Variables **/
	
	// Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;

	private static BluetoothSocket btSocket;
	private static InputStream btInStream;
	private static OutputStream btOutStream;

	private static final boolean D = true;          // Debug

	OutputStream tmpOut = null;
	OutputStream mmOutStream = null;    
	InputStream tmpIn = null;
	InputStream mmInStream = null;
	byte[] buffer = new byte[1024];;  // buffer store for the stream
	int bytes; // bytes returned from read()
	int numberofbytes = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
	}

}
