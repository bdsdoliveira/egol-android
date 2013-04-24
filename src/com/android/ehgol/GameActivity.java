package com.android.ehgol;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.plus.PlusShare;

public class GameActivity extends Activity {
	GoogleMap map;
	String team1, team2, city, stadium;
	float latitude, longitude;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
	    
		checkGoogleMapsApp();
		
		buildGameFromIntent();
		
		Button check_map = (Button) findViewById(R.id.map_button);
		check_map.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(GameActivity.this, CheckMapActivity.class);
				i.putExtra("GAME_DETAILS_LAT", latitude);
				i.putExtra("GAME_DETAILS_LNG", longitude);
				i.putExtra("GAME_DETAILS_TEAM1", team1);
				i.putExtra("GAME_DETAILS_TEAM2", team2);
				i.putExtra("GAME_DETAILS_CITY", city);
				i.putExtra("GAME_DETAILS_STADIUM", stadium);
				GameActivity.this.startActivity(i);
			}
		});
		
		Button directions_map = (Button) findViewById(R.id.directions_button);
		directions_map.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_VIEW,
						Uri.parse("http://maps.google.com/maps?f=d&daddr=" + latitude + "," + longitude))
					.setComponent(new ComponentName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity"));
				startActivity(i);
			}
		});
		
		ImageButton facebook = (ImageButton) findViewById(R.id.facebook_button);
		facebook.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_SEND)
					.setType("text/plain")
					.putExtra(Intent.EXTRA_TEXT, "Test sharing")
					.setPackage("com.facebook.katana");
		        startActivity(i);
		        // https://m.facebook.com/sharer.php?u=website_url&t=titleOfThePost
			}
		});
		
		ImageButton twitter = (ImageButton) findViewById(R.id.twitter_button);
		twitter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_SEND)
					.setType("text/plain")
		        	.putExtra(Intent.EXTRA_TEXT, "Hey, I'll be at " + stadium + " in " + city + " to watch the " + team1 + " × " + team2 + " match!")
		        	.setComponent(new ComponentName("com.twitter.android", "com.twitter.android.PostActivity"));
		        startActivity(i);
			}
		});
	
		ImageButton gplus = (ImageButton) findViewById(R.id.gplus_button);
		gplus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				 Intent i = new PlusShare.Builder(GameActivity.this)
		         	.setType("text/plain")
		         	.setText("Hey, I'll be at " + stadium + " in " + city + " to watch the " + team1 + " × " + team2 + " match!")
		         	.getIntent();
				 startActivity(i);
			}
		});
	}
	
	
	
	private void buildGameFromIntent() {
		Intent intent = getIntent();
		String extra = intent.getStringExtra("GAME_DETAILS");
		
		JSONObject o;
		JSONArray a;
		try {
			a = new JSONArray(extra);
			o = a.getJSONObject(0);
			team1 = o.getString("team_1");
			team2 = o.getString("team_2");
			city = o.getString("city_");
			stadium = o.getString("stadium");
			latitude = Float.parseFloat(o.getString("latitude"));
			longitude = Float.parseFloat(o.getString("longitude"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		TextView mTeams = (TextView) findViewById(R.id.list_item_teams);
		TextView mCity = (TextView) findViewById(R.id.list_item_city);
		TextView mStadium = (TextView) findViewById(R.id.list_item_stadium);
		
		mTeams.setText(team1 + " × " + team2);
		mCity.setText("Cidade: " + city);
		mStadium.setText("Local: " + stadium);
		
		LatLng GAME_LATLNG = new LatLng(latitude, longitude);
		
		if (map != null) {
			map.addMarker(new MarkerOptions()
				.position(GAME_LATLNG)
				.title(team1 + " × " + team2)
				.snippet(stadium + " – " + city));
		}
		
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(GAME_LATLNG, 13));
		map.animateCamera(CameraUpdateFactory.newLatLngZoom(GAME_LATLNG, 14), 1000, null);
		
	}
	
	
	private void checkGoogleMapsApp() {
		boolean maps_installed;
		
	    try {
	        getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0 );
	        maps_installed = true;
	    } catch (PackageManager.NameNotFoundException e) {
	    	maps_installed = false;
	    }
	    
	    // Do a null check to confirm that the map is not instantiated.
	    if (map == null) {
	        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.game_map)).getMap();

	        if (!maps_installed) {
	            AlertDialog.Builder d = new AlertDialog.Builder(this);
	            d.setMessage("Install Google Maps to view location and get directions to stadiums!\n\nPlease, click below to download and install from Google Play.");
	            d.setCancelable(false);
	            d.setPositiveButton("Install", new DialogInterface.OnClickListener() {
	    	        @Override
	    	        public void onClick(DialogInterface dialog, int which) {
	    	            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.apps.maps"));
	    	            startActivity(i);
	    	            // Finish the activity to force re-check
	    	            finish();
	    	        }
	            });
	            d.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent i = new Intent(GameActivity.this, MainActivity.class);
						startActivity(i);
					}
				});
	            AlertDialog dialog = d.create();
	            dialog.show();
	        }
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
