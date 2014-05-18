package com.saint_peter.services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

public class GenerateSoundService extends Service {

	public static final String BUNDLE_KEY_MEDIA_INT = "media_int";
	
	private final IBinder binder = new LocalBinder();
		
	public void playSound(int resourceId) {
		MediaPlayer.create(this.getApplicationContext(), resourceId).start();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}
	
	public class LocalBinder extends Binder {
		public GenerateSoundService getService() {
			return GenerateSoundService.this;
		}
	}

}
