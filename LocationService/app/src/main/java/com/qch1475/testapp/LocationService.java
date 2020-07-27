package com.qch1475.testapp;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.Manifest;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class LocationService extends Service {

	private static final String TAG = LocationService.class.getSimpleName();
	public static boolean IsRunning = false;
	MyDbAdapter dbAdapt = null;
	long INTERVAL = 10000;
	long FASTESTINTERVAL = 5000;

	@Override
	public void onCreate() {
		dbAdapt = new MyDbAdapter(getApplicationContext());
		dbAdapt.open();
		requestLocationUpdates();
		IsRunning = true;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Toast.makeText(this, "Tracker service starting", Toast.LENGTH_SHORT)
				.show();

		// If get killed, after returning from here, restart
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private void requestLocationUpdates() {
		LocationRequest request = new LocationRequest();
		// set 10s as update interval and 5s as the fastest update interval if update available
		// these values can be adjusted for the battery saving and the the location request frequency
		request.setInterval(INTERVAL);
		request.setFastestInterval(FASTESTINTERVAL);
		request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);

		int permission = ContextCompat.checkSelfPermission(this,
			Manifest.permission.ACCESS_FINE_LOCATION);
		if (permission == PackageManager.PERMISSION_GRANTED) {
			// Request location updates and when an update is
			// received, toast it and store the location in sdcard
			client.requestLocationUpdates(request, new LocationCallback() {
				@Override
				public void onLocationResult(LocationResult locationResult) {
					Location location = locationResult.getLastLocation();
					if (location != null) {
						Log.d(TAG, "location update " + location);
						// toast with the new location data
						Toast.makeText(
							getApplicationContext(),
							"Latitude " + location.getLatitude() + " longtitude: "
								+ location.getLongitude(), Toast.LENGTH_SHORT)
							.show();

						// and store this information
						SimpleDateFormat dateFormat = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
						Date date = new Date();
						dbAdapt.insertLocation(Double.toString(location.getLongitude()),
							Double.toString(location.getLatitude()),
							dateFormat.format(date));
					}

				}
			}, null);
		}
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "Location Service Closing", Toast.LENGTH_SHORT)
				.show();

		// save the database contents to the txt file on the external SD card
		File sdCard = Environment.getExternalStorageDirectory();
		File dbPath = new File(sdCard.getAbsolutePath() + "/locationtrack/");
		if (!dbPath.mkdirs()) {
			Log.w(TAG, "failed to create directory: " + dbPath.toString());
		}

		File file = new File(dbPath, "locationdata.txt");
		try {
			file.createNewFile();
		} catch (IOException e1) {
			Log.w(TAG, e1.toString());
		}

		try {
			FileWriter fw = new FileWriter(file);
			Cursor cursor = dbAdapt.getAllLocations();
			while (cursor.moveToNext()) {
				String var1 = "";

				for (int i = 1; i < cursor.getColumnCount(); i++) {
					var1 += "\"" + cursor.getString(i) + "\"";
					var1 += " ";
				}

				fw.write(var1);
				fw.write("\n");
			}
			fw.close();

		} catch (FileNotFoundException e) {
			Log.e("IO", e.toString());
		} catch (IOException e) {
			Log.e("IO", e.toString());
		}

		// and kill the service
		IsRunning = false;
		stopSelf();
	}
}