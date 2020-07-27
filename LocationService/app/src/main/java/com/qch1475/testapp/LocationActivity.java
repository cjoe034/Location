package com.qch1475.testapp;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.Manifest;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class LocationActivity extends Activity {

	private static final int PERMISSIONS_REQUEST = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Check if GPS is enabled
		LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Toast.makeText(this, "Please enable location services", Toast.LENGTH_SHORT).show();
			finish();
		}

		// for start tracking button
		final Button button = (Button) findViewById(R.id.button1);
		button.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// Check location permission is granted - if it is, start
				// the service, otherwise request the permission
				int permission = ContextCompat.checkSelfPermission(getApplicationContext(),
					Manifest.permission.ACCESS_FINE_LOCATION);
				if (permission == PackageManager.PERMISSION_GRANTED) {
					Intent intent = new Intent(getApplicationContext(),
						LocationService.class);
					startService(intent);
				} else {
					ActivityCompat.requestPermissions(LocationActivity.this,
						new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
						PERMISSIONS_REQUEST);
				}

				updateServiceStatus();
			}
		});

		// for stop tracking button
		final Button button1 = (Button) findViewById(R.id.button2);
		button1.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						LocationService.class);
				stopService(intent);
				updateServiceStatus();
			}
		});

		this.updateServiceStatus();
	}

	private void updateServiceStatus() {
		final TextView serviceText = (TextView) findViewById(R.id.textServiceStatus);
		String text = LocationService.IsRunning ? "Running..." : "Stopped...";
		serviceText.setText(text);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
		grantResults) {
		if (requestCode == PERMISSIONS_REQUEST && grantResults.length == 1
			&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
			// Start the service when the permission is granted
			Intent intent = new Intent(getApplicationContext(),
				LocationService.class);
			startService(intent);
		} else {
			finish();
		}
	}
}