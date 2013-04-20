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
	double GAME_DETAILS_LAT, GAME_DETAILS_LNG;
	String team1, team2, city, stadium;
	
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
				i.putExtra("GAME_DETAILS_LAT", GAME_DETAILS_LAT);
				i.putExtra("GAME_DETAILS_LNG", GAME_DETAILS_LNG);
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
				Intent i = new Intent(GameActivity.this, DirectionsMapActivity.class);
				i.putExtra("GAME_DETAILS_LAT", GAME_DETAILS_LAT);
				i.putExtra("GAME_DETAILS_LNG", GAME_DETAILS_LNG);
				GameActivity.this.startActivity(i);
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
			team1 = o.getString("team1").toString();
			team2 = o.getString("team2").toString();
			city = o.getString("city").toString();
			stadium = o.getString("stadium").toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		TextView mTeams = (TextView) findViewById(R.id.list_item_teams);
		TextView mCity = (TextView) findViewById(R.id.list_item_city);
		TextView mStadium = (TextView) findViewById(R.id.list_item_stadium);
		
		mTeams.setText(team1 + " × " + team2);
		mCity.setText("Cidade: " + city);
		mStadium.setText("Local: " + stadium);
		
		/* Temporary code for testing */
		LatLng GAME_LATLNG = new LatLng(-23.545531, -46.473373);
		/* ************************** */
		
		GAME_DETAILS_LAT = GAME_LATLNG.latitude; 
		GAME_DETAILS_LNG = GAME_LATLNG.longitude;

		if (map != null) {
			map.addMarker(new MarkerOptions()
				.position(GAME_LATLNG)
				.title(team1 + " × " + team2)
				.snippet(stadium + " – " + city));
		}
		
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(GAME_LATLNG, 13));
		map.animateCamera(CameraUpdateFactory.newLatLngZoom(GAME_LATLNG, 14), 1000, null);
		
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
