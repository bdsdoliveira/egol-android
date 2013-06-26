package com.android.egol;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

public class DirectionsMapActivity extends Activity implements LocationListener {
	private LocationManager l;
	String p;
	GoogleMap map;
	LatLng CURRENT_LOCATION;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_directionsmap);

		l = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.game_map)).getMap();
		
		checkGPSEnabled();
		
		map.setMyLocationEnabled(true);
		
		map.setOnMyLocationChangeListener(new OnMyLocationChangeListener() {
			@Override
			public void onMyLocationChange(Location l) {
			    CURRENT_LOCATION = new LatLng(l.getLatitude(), l.getLongitude());
			    Toast.makeText(DirectionsMapActivity.this, CURRENT_LOCATION.toString(), Toast.LENGTH_SHORT).show();
			    map.moveCamera(CameraUpdateFactory.newLatLngZoom(CURRENT_LOCATION, 14));
			}
		});

//	    CURRENT_LOCATION = new LatLng(map.getMyLocation().getLatitude(), map.getMyLocation().getLongitude());
//	    Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
//	    map.moveCamera(CameraUpdateFactory.newLatLngZoom(CURRENT_LOCATION, 11));
		
//		Intent intent = getIntent();
//		LatLng GAME_LATLNG = new LatLng(intent.getDoubleExtra("LOCATION_LAT", 0), intent.getDoubleExtra("LOCATION_LNG", 0));
		
//		getCurrentLocation();
		
//		requestDirections(GAME_LATLNG, CURRENT_LOCATION);
		
	}
	

//	private void requestDirections(final LatLng start, final LatLng dest) {
//	    //https://developers.google.com/maps/documentation/directions/#JSON <- get api
//	    String jsonURL = "http://maps.googleapis.com/maps/api/directions/json?";
//	    final StringBuffer sBuf = new StringBuffer(jsonURL);
//	    sBuf.append("origin=");
//	    sBuf.append(start.latitude);
//	    sBuf.append(',');
//	    sBuf.append(start.longitude);
//	    sBuf.append("&destination=");
//	    sBuf.append(dest.latitude);
//	    sBuf.append(',');
//	    sBuf.append(dest.longitude);
//	    sBuf.append("&sensor=true&mode=driving");
//	}
	
	
	
	@Override
	protected void onResume() {
		super.onResume();
//		l.requestLocationUpdates(p, 400, 1, this);
	}

	@Override
	protected void onPause() {
	    super.onPause();
//	    l.removeUpdates(this);
	}

	
	public void getCurrentLocation() {
//    	ProgressDialog d = new ProgressDialog(this);
//		d.setCancelable(false);
//		d.setCanceledOnTouchOutside(false);
//		d.setMessage("Getting current location...");
//		d.show();
		
	    // Define the criteria how to select the location provider -> use default
	    Criteria c = new Criteria();
	    p = l.getBestProvider(c, false);
	    Location location = l.getLastKnownLocation(p);

	    l.requestLocationUpdates(p, 400, 1, this);
	    
	    // Initialize the location fields
	    if (location != null) {
	    	Toast.makeText(getApplicationContext(), "Provider " + p + " has been selected.", Toast.LENGTH_SHORT).show();
	    	onLocationChanged(location);
	    } else {
	    	Toast.makeText(getApplicationContext(), "Location not available.", Toast.LENGTH_SHORT).show();
	    }
	    
//	    d.dismiss();
	}
	
	
	@Override
	public void onLocationChanged(Location location) {
//	    CURRENT_LOCATION = new LatLng(location.getLatitude(), location.getLongitude());
//	    Toast.makeText(this, CURRENT_LOCATION.toString(), Toast.LENGTH_SHORT).show();
//	    map.moveCamera(CameraUpdateFactory.newLatLngZoom(CURRENT_LOCATION, 11));
//		map.animateCamera(CameraUpdateFactory.newLatLngZoom(CURRENT_LOCATION, 14), 2000, null);
//	    l.removeUpdates(this);
	}

	@Override
	public void onStatusChanged(String p, int status, Bundle extras) {	}
	
	@Override
	public void onProviderEnabled(String p) {
		Toast.makeText(this, "Enabled new provider " + p, Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onProviderDisabled(String p) {
		Toast.makeText(this, "Disabled provider " + p, Toast.LENGTH_SHORT).show();
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
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_directionsmap, menu);
		return true;
	}
	
}
