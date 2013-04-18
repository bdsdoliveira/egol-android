package com.android.ehgol;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

public class GameActivity extends Activity implements LocationListener {
	private LocationManager l;
	String p;
	GoogleMap map;
	LatLng CURRENT_LOCATION;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		l = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	    
		checkGPSEnabled();

		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.game_map)).getMap();
		
		getCurrentLocation();
		
//		LatLng HAMBURG = new LatLng(53.558, 9.927);
//		if (map != null){
//			map.addMarker(new MarkerOptions()
//				.position(HAMBURG)
//				.title("Hamburg")
//				.snippet("Hamburg is so cool..."));
//		}
//		map.moveCamera(CameraUpdateFactory.newLatLngZoom(HAMBURG, 9));
//		map.animateCamera(CameraUpdateFactory.newLatLngZoom(HAMBURG, 10), 2000, null);
		
		Intent intent = getIntent();
		String extra = intent.getStringExtra("JSON");
		JSONArray array = null;
		try {
			array = new JSONArray("[" + extra + "]");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		JSONObject o;
		String team1 = null;
		String team2 = null;
		String city = null;
		String stadium = null;
		try {
			o = array.getJSONObject(0);
			team1 = o.getString("team1").toString();
			team2 = o.getString("team2").toString();
			city = o.getString("city").toString();
			stadium = o.getString("stadium").toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		TextView mTeams = (TextView) findViewById(R.id.list_item_teams);
		TextView mPlace = (TextView) findViewById(R.id.list_item_place);
		
		mTeams.setText(team1 + " × " + team2);
		mPlace.setText("\nLocal: " + city + ", " + stadium);
	}

	@Override
	protected void onResume() {
		super.onResume();
		l.requestLocationUpdates(p, 400, 1, this);
	}

	@Override
	protected void onPause() {
	    super.onPause();
	    l.removeUpdates(this);
	}

	
	public void getCurrentLocation() {
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
	}
	
	
	@Override
	public void onLocationChanged(Location l) {
	    int latitude = (int) (l.getLatitude());
	    int longitude = (int) (l.getLongitude());
	    CURRENT_LOCATION = new LatLng(latitude, longitude);
	    Toast.makeText(this, CURRENT_LOCATION.toString(), Toast.LENGTH_SHORT).show();
	    map.moveCamera(CameraUpdateFactory.newLatLngZoom(CURRENT_LOCATION, 9));
		map.animateCamera(CameraUpdateFactory.newLatLngZoom(CURRENT_LOCATION, 10), 2000, null);
	}

	@Override
	public void onStatusChanged(String p, int status, Bundle extras) {
		
	}
	
	@Override
	public void onProviderEnabled(String p) {
		Toast.makeText(this, "Enabled new provider " + p, Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onProviderDisabled(String pr) {
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
		getMenuInflater().inflate(R.menu.menu_game, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button.
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
