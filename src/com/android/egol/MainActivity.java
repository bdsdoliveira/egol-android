package com.android.egol;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements OnQueryTextListener {
	private final String GET_URL = "http://egol.herokuapp.com";
    //private final String GET_URL = "http://192.168.0.12:3000";
	SearchView s;
	ListView matches_list;
	MatchesListAdapter matchesListAdapter;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        matches_list = (ListView) findViewById(R.id.matches_list);
        matches_list.setTextFilterEnabled(true);

		GetMatchesTask getMatches = new GetMatchesTask();
		getMatches.execute();
    }
    
    
    public class GetMatchesTask extends AsyncTask<String, Void, Void> {
    	private ProgressDialog p = new ProgressDialog(MainActivity.this);
		StringBuilder sb = new StringBuilder();
		JSONArray array;
		int status;
		
    	@Override
    	protected void onPreExecute() {
			p.setCancelable(false);
			p.setCanceledOnTouchOutside(false);
			p.setMessage("Getting matches...");
			p.show();
		}
		
		@Override
		protected Void doInBackground(String... s) {
			try {
				// Set up object for HTTP request
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(GET_URL);
				
				// Set options for the request 
				get.setHeader("Accept", "application/json");
				HttpConnectionParams.setConnectionTimeout(client.getParams(), 15000);
				HttpConnectionParams.setSoTimeout(client.getParams(), 20000);
				
				// Execute and save response and status 
				HttpResponse response = client.execute(get);
				status = response.getStatusLine().getStatusCode();
				
				// Only set the array if status is OK (200)
				if (status == 200) {
					BufferedReader b = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
					String aux = null;
					while ((aux = b.readLine()) != null) sb.append(aux);
					array = new JSONArray(sb.toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		protected void onPostExecute(Void v) {
			p.dismiss();
			
			if (array != null) {
		        matchesListAdapter = new MatchesListAdapter(array);
				matches_list.setAdapter(matchesListAdapter);
				Toast.makeText(getApplicationContext(), "Matches loaded.", Toast.LENGTH_SHORT).show();
			} else Toast.makeText(getApplicationContext(), "Failed: error " + status, Toast.LENGTH_SHORT).show();
		}
    }
    
    
    
    private class MatchesListAdapter extends BaseAdapter {
    	private List<String> items = new ArrayList<String>();
    	private List<String> items_showing = new ArrayList<String>();
    	
    	MatchesListAdapter(JSONArray array) {
    		for (int i = 0; i < array.length(); i++) {
    			try {
    				items.add("[" + array.getJSONObject(i).toString() + "]");
    				items_showing.add("[" + array.getJSONObject(i).toString() + "]");
    			} catch (JSONException e) {
    				e.printStackTrace();
    			}
    		}
    	}
    	
		@Override
		public int getCount() {
			return items_showing.size();
		}
		
		@Override
		public String getItem(int i) {
			return items_showing.get(i);
		}
		
		@Override
		public long getItemId(int i) {
			return i;
		}
		
		@Override
		public View getView(final int i, View view, ViewGroup parent) {
			View v = view;
			JSONObject o;
			JSONArray a;
			String team1 = null;
			String team2 = null;
            String date = null;
			String city = null;
			String stadium = null;
            String stage = null;
			
			try {
				a = new JSONArray(getItem(i));
				o = a.getJSONObject(0);
				team1 = (o.getJSONObject("team1").getString("name") == "null") ? o.getJSONObject("team1").getString("code") : o.getJSONObject("team1").getString("name");
				team2 = (o.getJSONObject("team2").getString("name") == "null") ? o.getJSONObject("team2").getString("code") : o.getJSONObject("team2").getString("name");
                date = o.getString("date_and_time");
				city = o.getJSONObject("city").getString("name");
				stadium = o.getJSONObject("city").getString("stadium");
                stage = o.getJSONObject("stage").getString("name");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			if (v == null) {
				LayoutInflater l = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = l.inflate(R.layout.list_item, null);
			}

			TextView mTeams = (TextView) v.findViewById(R.id.list_item_teams);
            TextView mDate = (TextView) v.findViewById(R.id.list_item_date);
			TextView mCity = (TextView) v.findViewById(R.id.list_item_city);
			TextView mStadium = (TextView) v.findViewById(R.id.list_item_stadium);
            TextView mStage = (TextView) v.findViewById(R.id.list_item_stage);
			
			mTeams.setText(team1 + " × " + team2);
            mDate.setText("Date: " + date);
			mCity.setText("City: " + city);
			mStadium.setText("Place: " + stadium);
            mStage.setText("Stage: " + stage);
			
			v.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(MainActivity.this, MatchActivity.class);
					intent.putExtra("MATCH_DETAILS", MatchesListAdapter.this.getItem(i));
					startActivity(intent);
				}
			});
			
			return v;
		}
		
		public void filterSearch(String s) {
	    	List<String> filtered = new ArrayList<String>();
			JSONObject o;
			JSONArray a;
			
			// Reset items to original values
			items_showing = items;

			// Search in each match inside its details
			for (int i = 0; i < this.items_showing.size(); i++) {
				try {
					a = new JSONArray(getItem(i));
					o = a.getJSONObject(0);
					if (o.getJSONObject("team1").getString("name").toLowerCase().contains(s) ||
						o.getJSONObject("team2").getString("name").toLowerCase().contains(s) ||
						o.getJSONObject("team1").getString("code").toLowerCase().contains(s) ||
						o.getJSONObject("team2").getString("code").toLowerCase().contains(s) ||
						o.getJSONObject("city").getString("name").toLowerCase().contains(s) ||
						o.getJSONObject("city").getString("stadium").toLowerCase().contains(s) ||
						o.getJSONObject("team1").getString("name").toUpperCase().contains(s) ||
						o.getJSONObject("team1").getString("name").toUpperCase().contains(s) ||
						o.getJSONObject("city").getString("name").toUpperCase().contains(s) ||
						o.getJSONObject("city").getString("stadium").toUpperCase().contains(s) ||
						o.getJSONObject("team1").getString("name").contains(s) ||
						o.getJSONObject("team1").getString("name").contains(s) ||
						o.getJSONObject("city").getString("name").contains(s) ||
						o.getJSONObject("city").getString("stadium").contains(s) ||
						o.getJSONObject("team2").getString("code").contains(s) ||
						o.getJSONObject("team2").getString("code").contains(s)) {
						filtered.add("[" + o.toString() + "]");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			this.items_showing = filtered;
			notifyDataSetChanged();
		}
		
    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        final MenuItem menuItemSearch = menu.findItem(R.id.action_search);
        s = (SearchView) menuItemSearch.getActionView();
        menuItemSearch.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
			@Override
			public boolean onMenuItemActionExpand(MenuItem item) {
				return true;
			}
			
			@Override
			public boolean onMenuItemActionCollapse(MenuItem item) {
				s.setQuery("", true);
				return true;
			}
		});
        s.setQueryHint("Search for matches");
        s.setOnQueryTextListener(this);
        
        return true;
    }

    public boolean onQueryTextChange(String query) {
    	matchesListAdapter.filterSearch(query);
        return true;
    }
 
    public boolean onQueryTextSubmit (String query) {
        // Hide keyboard because search was already
    	// made in real time using onQueryTextChange
    	InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    	imm.hideSoftInputFromWindow(s.getWindowToken(), 0);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case R.id.action_reload:
    		GetMatchesTask getMatches = new GetMatchesTask();
    		getMatches.execute();
    		break;
    	case R.id.action_settings:
    		Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
    		break;
    	default:
    		break;
    	}
    	return true;
    } 
    
}
