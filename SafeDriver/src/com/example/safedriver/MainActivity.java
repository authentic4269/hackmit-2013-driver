package com.example.safedriver;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

import com.androidplot.xy.*;

import java.util.Arrays;

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
	private Sensor mRotationVectorSensor = mSensorManager.getDefaultSensor(
			Sensor.TYPE_ROTATION_VECTOR);
	private Sensor mLinearAccelerationSensor = mSensorManager.getDefaultSensor(
			Sensor.TYPE_LINEAR_ACCELERATION);
	private XYPlot angularSpeedPlot = null;
	private XYPlot linearAccelerationPlot = null;
	private SimpleXYSeries angularSpeedSeries = null;
	private SimpleXYSeries linearAccelerationSeries = null;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orientation_sensor_example);
 
        // setup the APR Levels plot:
        angularSpeedPlot = (XYPlot) findViewById(R.id.angularSpeedPlot);
 
        angularSpeedSeries = new SimpleXYSeries("Angular Speed");
        angularSpeedSeries.useImplicitXVals();
        angularSpeedPlot.addSeries(angularSpeedSeries,
                new BarFormatter(Color.argb(100, 0, 200, 0), Color.rgb(0, 80, 0)));
        angularSpeedPlot.setDomainStepValue(3);
        angularSpeedPlot.setTicksPerRangeLabel(3);
 
        // per the android documentation, the minimum and maximum readings we can get from
        // any of the orientation sensors is -180 and 359 respectively so we will fix our plot's
        // boundaries to those values.  If we did not do this, the plot would auto-range which
        // can be visually confusing in the case of dynamic plots.
        angularSpeedPlot.setRangeBoundaries(-180, 359, BoundaryMode.FIXED);
 
        // use our custom domain value formatter:
        angularSpeedPlot.setDomainValueFormat(new APRIndexFormat());
 
        // update our domain and range axis labels:
        angularSpeedPlot.setDomainLabel("Axis");
        angularSpeedPlot.getDomainLabelWidget().pack();
        angularSpeedPlot.setRangeLabel("Angle (Degs)");
        angularSpeedPlot.getRangeLabelWidget().pack();
        angularSpeedPlot.setGridPadding(15, 0, 15, 0);
 
        // setup the APR History plot:
        linearAccelerationPlot = (XYPlot) findViewById(R.id.linearAccelerationPlot);
 
        linearAccelerationSeries = new SimpleXYSeries("Linear Acceleration");
        linearAccelerationSeries.useImplicitXVals();

        linearAccelerationPlot.setRangeBoundaries(-180, 359, BoundaryMode.FIXED);
        linearAccelerationPlot.setDomainBoundaries(0, 30, BoundaryMode.FIXED);
        linearAccelerationPlot.addSeries(linearAccelerationSeries, new LineAndPointFormatter(Color.rgb(100, 100, 200), Color.BLACK, null));
        linearAccelerationPlot.setDomainStepValue(5);
        linearAccelerationPlot.setTicksPerRangeLabel(3);
        linearAccelerationPlot.setDomainLabel("Sample Index");
        linearAccelerationPlot.getDomainLabelWidget().pack();
        linearAccelerationPlot.setRangeLabel("Angle (Degs)");
        linearAccelerationPlot.getRangeLabelWidget().pack();
 
        // setup checkboxes:
        hwAcceleratedCb = (CheckBox) findViewById(R.id.hwAccelerationCb);
        final PlotStatistics levelStats = new PlotStatistics(1000, false);
        final PlotStatistics histStats = new PlotStatistics(1000, false);
 
        aprLevelsPlot.addListener(levelStats);
        aprHistoryPlot.addListener(histStats);
        hwAcceleratedCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    aprLevelsPlot.setLayerType(View.LAYER_TYPE_NONE, null);
                    aprHistoryPlot.setLayerType(View.LAYER_TYPE_NONE, null);
                } else {
                    aprLevelsPlot.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                    aprHistoryPlot.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                }
            }
        });
 
        showFpsCb = (CheckBox) findViewById(R.id.showFpsCb);
        showFpsCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                levelStats.setAnnotatePlotEnabled(b);
                histStats.setAnnotatePlotEnabled(b);
            }
        });
 
        // get a ref to the BarRenderer so we can make some changes to it:
        BarRenderer barRenderer = (BarRenderer) aprLevelsPlot.getRenderer(BarRenderer.class);
        if(barRenderer != null) {
            // make our bars a little thicker than the default so they can be seen better:
            barRenderer.setBarWidth(25);
        }
 
        // register for orientation sensor events:
        sensorMgr = (SensorManager) getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        for (Sensor sensor : sensorMgr.getSensorList(Sensor.TYPE_ORIENTATION)) {
            if (sensor.getType() == Sensor.TYPE_ORIENTATION) {
                orSensor = sensor;
            }
        }
 
        // if we can't access the orientation sensor then exit:
        if (orSensor == null) {
            System.out.println("Failed to attach to orSensor.");
            cleanup();
        }
 
        sensorMgr.registerListener(this, orSensor, SensorManager.SENSOR_DELAY_UI);
 
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


	public void start() {
		// enable our sensor when the activity is resumed, ask for
		// 10 ms updates.
		mSensorManager.registerListener(this, mRotationVectorSensor, 10000);
		mSensorManager.registerListener(this, mLinearAccelerationSensor, 10000);
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
			event.values[0] = 0;
			event.values[1] = 0;
			//SensorManager.getRotationMatrixFromVector(
			// mRotationMatrix , event.values);
		}
		if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {

		}
	}


	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}
}
