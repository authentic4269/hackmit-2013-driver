package com.app.drivesafe;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;

import com.androidplot.xy.*;
import com.example.safedriver.R;


/**
 * Wrapper activity demonstrating the use of the new
 * {@link SensorEvent#values rotation vector sensor}
 * ({@link Sensor#TYPE_ROTATION_VECTOR TYPE_ROTATION_VECTOR}).
 * 
 * @see Sensor
 * @see SensorEvent
 * @see SensorManager
 * 
 */
public class MainActivity extends Activity implements SensorEventListener {
	private SensorManager mSensorManager;
	private Sensor mRotationVectorSensor;
	private Sensor mLinearAccelerationSensor;
	private XYPlot angularSpeedPlot = null;
	private XYPlot linearAccelerationPlot = null;
	private SimpleXYSeries angularSpeedSeries = null;
	private SimpleXYSeries linearAccelerationSeries = null;
	private int HISTORY_SIZE = 30;
	private long START = System.currentTimeMillis();



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int i = R.layout.orientation_sensor_example;
        setContentView(i);
        mSensorManager = (SensorManager) getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        mLinearAccelerationSensor = mSensorManager.getDefaultSensor(
    			Sensor.TYPE_LINEAR_ACCELERATION);
        mLinearAccelerationSensor = mSensorManager.getDefaultSensor(
    			Sensor.TYPE_LINEAR_ACCELERATION);
        
 
        // setup the APR Levels plot:
        angularSpeedPlot = (XYPlot) findViewById(R.id.angularSpeedPlot);
 
        angularSpeedSeries = new SimpleXYSeries("Angular Speed");
        angularSpeedPlot.addSeries(angularSpeedSeries,
                new LineAndPointFormatter());
        angularSpeedPlot.setDomainStepValue(3);
        angularSpeedPlot.setTicksPerRangeLabel(3);
 
        // per the android documentation, the minimum and maximum readings we can get from
        // any of the orientation sensors is -180 and 359 respectively so we will fix our plot's
        // boundaries to those values.  If we did not do this, the plot would auto-range which
        // can be visually confusing in the case of dynamic plots.
        angularSpeedPlot.setRangeBoundaries(-180, 359, BoundaryMode.FIXED);
 
        // update our domain and range axis labels:
        angularSpeedPlot.setDomainLabel("Time");
        angularSpeedPlot.getDomainLabelWidget().pack();
        angularSpeedPlot.setRangeLabel("Angle (Degs)");
        angularSpeedPlot.getRangeLabelWidget().pack();
        angularSpeedPlot.setGridPadding(15, 0, 15, 0);
 
        // setup the APR History plot:
        linearAccelerationPlot = (XYPlot) findViewById(R.id.linearAccelerationPlot);
 
        linearAccelerationSeries = new SimpleXYSeries("Linear Acceleration");

        linearAccelerationPlot.setRangeBoundaries(-180, 359, BoundaryMode.FIXED);
        linearAccelerationPlot.setDomainBoundaries(0, 30, BoundaryMode.FIXED);
        linearAccelerationPlot.addSeries(linearAccelerationSeries, new LineAndPointFormatter());
        linearAccelerationPlot.setDomainStepValue(5);
        linearAccelerationPlot.setTicksPerRangeLabel(3);
        linearAccelerationPlot.setDomainLabel("Time");
        linearAccelerationPlot.getDomainLabelWidget().pack();
        linearAccelerationPlot.setRangeLabel("Acceleration (m/s^2)");
        linearAccelerationPlot.getRangeLabelWidget().pack();
		mSensorManager.registerListener(this, mRotationVectorSensor, 100000);
		mSensorManager.registerListener(this, mLinearAccelerationSensor, 100000);
    }


	@Override
	protected void onResume() {
		// Ideally a game should implement onResume() and onPause()
		// to take appropriate action when the activity looses focus
		super.onResume();
	}

	@Override
	protected void onPause() {
		// Ideally a game should implement onResume() and onPause()
		// to take appropriate action when the activity looses focus
		super.onPause();

	}

	public void stop() {
		// make sure to turn our sensor off when the activity is paused
		mSensorManager.unregisterListener(this);
	}

	public void onSensorChanged(SensorEvent event) {
		// we received a sensor event. it is a good practice to check
		// that we received the proper event
		if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
			// convert the rotation-vector to a 4x4 matrix. the matrix
			// is interpreted by Open GL as the inverse of the
			// rotation-vector, which is what we want.
			if (angularSpeedSeries.size() > HISTORY_SIZE) {
				angularSpeedSeries.removeFirst();
			}
			angularSpeedSeries.addLast(System.currentTimeMillis() - START, event.values[2]);
			angularSpeedPlot.redraw();
		}
		if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
			if (linearAccelerationSeries.size() > HISTORY_SIZE) {
				linearAccelerationSeries.removeFirst();
			}
			linearAccelerationSeries.addLast(System.currentTimeMillis() - START, event.values[2]);
			linearAccelerationPlot.redraw();
		}
	}


	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}
}
