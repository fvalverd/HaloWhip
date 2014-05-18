package com.saint_peter.halowhip;

import java.util.Map;

import com.saint_peter.services.GenerateSoundService;
import com.saint_peter.tasks.RequestTask;
import com.saint_peter.tasks.RequestTaskListener;
import com.saint_peter.halowhip.R;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends Activity implements SensorEventListener, RequestTaskListener {
	
	private SensorManager sensorManager;
	private double acceleration;
	private double currentAcceleration;
	private double lastAcceleration;
	private double minAcceleration;
	private boolean working;
	Map<String, ?> settings;
	
	GenerateSoundService soundService;
	boolean mBound = false;
	
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			GenerateSoundService.LocalBinder binder = (GenerateSoundService.LocalBinder) service;
			soundService = binder.getService();
			mBound = true;
		}
		
		public void onServiceDisconnected(ComponentName arg0) {
			mBound = false;
		}
	};

	public void whipSensorInitialize() {
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		acceleration = 0.00f;
		currentAcceleration = SensorManager.GRAVITY_EARTH;
		lastAcceleration = currentAcceleration;
		minAcceleration = 14.0;
		PreferenceManager.setDefaultValues(this, R.xml.settings, false);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		whipSensorInitialize();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.action_bar, menu);
		return true;
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		Intent intent = new Intent(this, GenerateSoundService.class);
		bindService(intent, mConnection, BIND_AUTO_CREATE);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		settings = PreferenceManager.getDefaultSharedPreferences(this).getAll();
		minAcceleration = Integer.valueOf(settings.get("whip_sensibility").toString());
		sensorManager.registerListener(
			this,
			sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
			SensorManager.SENSOR_DELAY_UI
		);
	}

	@Override
	protected void onPause() {
		super.onPause();
		sensorManager.unregisterListener(this);
	}
		
	@Override
	protected void onStop() {
		super.onStop();
		if (mBound) {
			unbindService(mConnection);
			mBound = false;
		}
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO: custom sensibility
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			float[] mGravity = event.values.clone();
			float x = mGravity[0], y = mGravity[1], z = mGravity[2];
			lastAcceleration = currentAcceleration;
			currentAcceleration = Math.sqrt(x*x + y*y + z*z);
			acceleration = acceleration * 0.1f + (currentAcceleration - lastAcceleration);
			if (acceleration > minAcceleration) {
				soundService.playSound(R.raw.halo_whip);
				if (!working) {
					synchronized (this) {
		    			if (!working) {
		    				working = true;
		    				sendRequest(null);
		    			}
					}
				}
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void requestTaskCallback (String data, int status) {
		working = false;
		soundService.playSound((status == 200) ? R.raw.open : R.raw.no);
	}

	public void settings(MenuItem item) {
		Intent settingsIntent = new Intent(this, SettingsActivity.class);
		startActivityForResult(settingsIntent, 1500); // Ask to Aldo about 1500
	}

	public void sendRequest(View view) {
		new RequestTask(this, settings).execute();
	}
	
}
