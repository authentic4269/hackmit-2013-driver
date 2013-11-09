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
import com.app.drivesafe.R;


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
	private XYPlot sidewaysAccelerationPlot = null;
	private XYPlot forwardAccelerationPlot = null;
	private XYPlot upwardAccelerationPlot = null;
	private XYPlot xRotationPlot = null;
	private XYPlot yRotationPlot = null;
	private XYPlot zRotationPlot = null;
	
	private SimpleXYSeries xRotationSeries = null;
	private SimpleXYSeries yRotationSeries = null;
	private SimpleXYSeries zRotationSeries = null;
	private SimpleXYSeries sidewaysAccelerationSeries = null;
	private SimpleXYSeries forwardAccelerationSeries = null;
	private SimpleXYSeries upwardAccelerationSeries = null;
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
        
 
        // START HERE 
        xRotationPlot = (XYPlot) findViewById(R.id.xRotationPlot);

        xRotationSeries = new SimpleXYSeries("X Rotation");
        xRotationPlot.addSeries(xRotationSeries,
                new LineAndPointFormatter());
        xRotationPlot.setDomainStepValue(3);
        xRotationPlot.setTicksPerRangeLabel(3);
 
        // per the android documentation, the minimum and maximum readings we can get from
        // any of the orientation sensors is -180 and 359 respectively so we will fix our plot's
        // boundaries to those values.  If we did not do this, the plot would auto-range which
        // can be visually confusing in the case of dynamic plots.
        xRotationPlot.setRangeBoundaries(-180, 359, BoundaryMode.FIXED);
 
        // update our domain and range axis labels:
        xRotationPlot.setDomainLabel("Time");
        xRotationPlot.getDomainLabelWidget().pack();
        xRotationPlot.setRangeLabel("m/s^2");
        xRotationPlot.getRangeLabelWidget().pack();
        xRotationPlot.setGridPadding(15, 0, 15, 0);
        //END HERE 

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
			if (xRotationSeries.size() > HISTORY_SIZE) {
				xRotationSeries.removeFirst();
				yRotationSeries.removeFirst();
				zRotationSeries.removeFirst();
			}
			xRotationSeries.addLast(System.currentTimeMillis() - START, event.values[2]);
			yRotationSeries.addLast(System.currentTimeMillis() - START, event.values[2]);
			zRotationSeries.addLast(System.currentTimeMillis() - START, event.values[2]);
			xRotationPlot.redraw();
		}
		if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
			if (forwardAccelerationSeries.size() > HISTORY_SIZE) {
				forwardAccelerationSeries.removeFirst();
				upwardAccelerationSeries.removeFirst();
				sidewaysAccelerationSeries.removeFirst();
			}
			forwardAccelerationSeries.addLast(System.currentTimeMillis() - START, event.values[2]);
			forwardAccelerationPlot.redraw();
		}
	}


	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}
}
