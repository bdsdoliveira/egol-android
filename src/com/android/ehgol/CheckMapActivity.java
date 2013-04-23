package com.android.ehgol;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class CheckMapActivity extends Activity {
	private LocationManager l;
	GoogleMap map;
	double GAME_DETAILS_LAT, GAME_DETAILS_LNG;
	String team1, team2, city, stadium;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_checkmap);

		l = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.game_map)).getMap();
		
		checkGPSEnabled();
		
		map.setMyLocationEnabled(true);
		
		buildMapFromIntent();
		
		Button directions_map = (Button) findViewById(R.id.directions_button);
		directions_map.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_VIEW,
						Uri.parse("http://maps.google.com/maps?f=d&daddr=" + GAME_DETAILS_LAT + "," + GAME_DETAILS_LNG));
				i.setComponent(new ComponentName("com.google.android.apps.maps",
					    "com.google.android.maps.MapsActivity"));
				startActivity(i);

	            // Finish the activity so the user goes back to the game activity instead
	            finish();
			}
		});
	}

	private void buildMapFromIntent() {
		Intent i = getIntent();
		LatLng GAME_LATLNG = new LatLng(i.getDoubleExtra("GAME_DETAILS_LAT", 0), i.getDoubleExtra("GAME_DETAILS_LNG", 0));
		String team1 = i.getStringExtra("GAME_DETAILS_TEAM1");
		String team2 = i.getStringExtra("GAME_DETAILS_TEAM2");
		String city = i.getStringExtra("GAME_DETAILS_CITY");
		String stadium = i.getStringExtra("GAME_DETAILS_STADIUM");

		GAME_DETAILS_LAT = GAME_LATLNG.latitude; 
		GAME_DETAILS_LNG = GAME_LATLNG.longitude;
		
		if (map != null) {
			map.addMarker(new MarkerOptions()
				.position(GAME_LATLNG)
				.title(team1 + " × " + team2)
				.snippet(stadium + " – " + city));
		}
		
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(GAME_LATLNG, 12));
		map.animateCamera(CameraUpdateFactory.newLatLngZoom(GAME_LATLNG, 15), 1000, null);
	}
	

	public void checkGPSEnabled() {
		boolean enabled = l.isProviderEnabled(LocationManager.GPS_PROVIDER);

		if (!enabled) {
			AlertDialog.Builder a = new AlertDialog.Builder(this);
	 
			a.setTitle("Enable GPS?");
	 
			a.setMessage("This application requires GPS to be enable for location features.")
				.setCancelable(false)
				.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,int id) {
						// Open system settings for GPS
						Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						startActivity(intent);
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
					}
				});
	 
			AlertDialog d = a.create();
	 
			d.show();
		}
	}
	
}
