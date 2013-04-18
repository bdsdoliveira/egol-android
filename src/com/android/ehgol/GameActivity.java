package com.android.ehgol;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class GameActivity extends Activity {
	GoogleMap map;
	double GAME_LOCATION_LAT, GAME_LOCATION_LNG;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
	    
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.game_map)).getMap();
		
		buildGameFromIntent();
		
		Button check_map = (Button) findViewById(R.id.map_button);
		check_map.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(GameActivity.this, CheckMapActivity.class);
				GameActivity.this.startActivity(i);
			}
		});
		
		Button directions_map = (Button) findViewById(R.id.directions_button);
		directions_map.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(GameActivity.this, DirectionsMapActivity.class);
				i.putExtra("LOCATION_LAT", GAME_LOCATION_LAT);
				i.putExtra("LOCATION_LNG", GAME_LOCATION_LNG);
				GameActivity.this.startActivity(i);
			}
		});
	
	}
	
	
	
	private void buildGameFromIntent() {
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
		

		/* Temporary code for testing */
		LatLng GAME_LATLNG = new LatLng(53.558, 9.927);
		if (map != null){
			map.addMarker(new MarkerOptions()
				.position(GAME_LATLNG)
				.title("Game")
				.snippet(""));
		}
		/* ************************** */
		
		GAME_LOCATION_LAT = GAME_LATLNG.latitude; 
		GAME_LOCATION_LNG = GAME_LATLNG.longitude;
		
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(GAME_LATLNG, 9));
		map.animateCamera(CameraUpdateFactory.newLatLngZoom(GAME_LATLNG, 10), 2000, null);
		
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
